package com.simprints.simprints.repository.datastores

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprints.Constants
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Healthcare organizational or geographical areas in this SimCapture app, and in DHIS2 in general,
 * are organizational units, and in Simprints ID they are modules.
 *
 * In a DHIS2 instance backend, the linkages between DHIS2 org units and Simprints modules
 * are stored in the JSON-based datastore
 * accessible from admin's web UI dashboard via Datastore Management
 * under the namespace simprints and key moduleIdMapping, as an array or JSON objects
 * each consisting of two string key-values with keys orgUnitUid and moduleId.
 *
 * In the SimCapture app, these mappings are persisted on device storage by the DHIS2 SDK read-only;
 * this repository is not responsible for mappings syncing from the DHIS2 backend to device storage.
 *
 * Org units are hierarchically structured. If an org unit has no Simprints module ID,
 * then its nearest parent's Simprints module ID will be used, if available.
 */
class SimprintsModuleIdRepository @Inject constructor(
    private val d2: D2,
    private val gson: Gson,
) {

    fun getSimprintsModuleId(orgUnitUid: String?): String? {
        // First, looking for this exact org unit
        getModuleIdNoParent(orgUnitUid)
            ?.let { return it }
        // If not found, looking for higher level org units
        var higherLevelOrgUnitUid: String? = orgUnitUid
        do {
            higherLevelOrgUnitUid = getHigherLevelOrgUnitUidOrNull(higherLevelOrgUnitUid)
            higherLevelOrgUnitUid?.run(::getModuleIdNoParent)
                ?.let { return it }
        } while (higherLevelOrgUnitUid != null)
        // If none found
        return null
    }

    private fun getHigherLevelOrgUnitUidOrNull(orgUnitUid: String?): String? =
        d2.organisationUnitModule().organisationUnits().byUid().eq(orgUnitUid).blockingGet()
            .map { it.parent()?.uid() }.firstOrNull()

    fun clearCache() {
        cachedMappings.clear()
    }

    // Exact level module ID mappings

    private data class ModuleIdMapping(
        val orgUnitUid: String? = null,
        val moduleId: String? = null,
    )

    private val cachedMappings: MutableMap<String, List<ModuleIdMapping>> = mutableMapOf()

    private fun getModuleIdNoParent(orgUnitUid: String?): String? =
        d2.dataStoreModule().dataStore()
            .value(Constants.SIMPRINTS_NAMESPACE, MODULE_ID_MAPPING_KEY)
            .blockingGet()?.value()?.run {
                cachedMappings.getOrPut(this) {
                    cachedMappings.clear()
                    gson.fromJson(this, object : TypeToken<List<ModuleIdMapping>>() {}.type)
                }.find { it.orgUnitUid == orgUnitUid }?.moduleId
            }

    private companion object {
        private const val MODULE_ID_MAPPING_KEY = "moduleIdMapping"
    }
}
