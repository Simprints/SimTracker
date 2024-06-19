package com.simprints.simprints.repository.datastores

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprints.Constants
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Healthcare programs in this SimCapture app, and in DHIS2 in general,
 * can be mapped to Simprints ID projects.
 *
 * In a DHIS2 instance backend, the linkages between DHIS2 programs and Simprints projects
 * are stored in the JSON-based datastore
 * accessible from admin's web UI dashboard via Datastore Management
 * under the namespace simprints and key projectIdMapping, as an array or JSON objects
 * each consisting of string key-values, among keys of which there are programUid and projectId.
 *
 * In the SimCapture app, these mappings are persisted on device storage by the DHIS2 SDK read-only;
 * this repository is not responsible for mappings syncing from the DHIS2 backend to device storage.
 */
class SimprintsProjectIdRepository @Inject constructor(
    private val d2: D2,
    private val gson: Gson,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ProjectIdMapping(
        val programUid: String? = null,
        val projectId: String? = null,
    )

    private val cachedMappings: MutableMap<String, List<ProjectIdMapping>> = mutableMapOf()

    fun getSimprintsProjectId(programUid: String?): String? =
        d2.dataStoreModule().dataStore()
            .value(Constants.SIMPRINTS_NAMESPACE, PROJECT_ID_MAPPING_KEY)
            .blockingGet()?.value()?.run {
                cachedMappings.getOrPut(this) {
                    cachedMappings.clear()
                    gson.fromJson(this, object : TypeToken<List<ProjectIdMapping>>() {}.type)
                }.find { it.programUid == programUid }?.projectId
            }

    fun clearCache() {
        cachedMappings.clear()
    }

    private companion object {
        private const val PROJECT_ID_MAPPING_KEY = "projectIdMapping"
    }
}
