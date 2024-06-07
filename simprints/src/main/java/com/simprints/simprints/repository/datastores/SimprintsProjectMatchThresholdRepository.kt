package com.simprints.simprints.repository.datastores

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprints.Constants
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Healthcare programs in this SimTracker app, and in DHIS2 in general,
 * can be mapped to Simprints ID projects, and for each of them there can be
 * a separate sensitivity threshold level of Simprints biometrics matching.
 *
 * In a DHIS2 instance backend, the linkages between DHIS2 programs and Simprints match thresholds
 * are stored in the JSON-based datastore
 * accessible from admin's web UI dashboard via Datastore Management
 * under the namespace simprints and key projectIdMapping, as an array or JSON objects
 * each consisting of string key-values,
 * among keys of which there are programUid and matchConfidenceScoreThreshold.
 *
 * In the SimTracker app, these mappings are persisted on device storage by the DHIS2 SDK read-only;
 * this repository is not responsible for mappings syncing from the DHIS2 backend to device storage.
 */
class SimprintsProjectMatchThresholdRepository @Inject constructor(
    private val d2: D2,
    private val gson: Gson,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ProjectMatchThresholdMapping(
        val programUid: String? = null,
        val matchConfidenceScoreThreshold: Int? = null,
    )

    private val cachedMappings: MutableMap<String, List<ProjectMatchThresholdMapping>> = mutableMapOf()

    fun getSimprintsMatchThreshold(programUid: String?): Int? =
        d2.dataStoreModule().dataStore()
            .value(Constants.SIMPRINTS_NAMESPACE, PROJECT_ID_MAPPING_KEY)
            .blockingGet()?.value()?.run {
                cachedMappings.getOrPut(this) {
                    cachedMappings.clear()
                    gson.fromJson(this, object : TypeToken<List<ProjectMatchThresholdMapping>>() {}.type)
                }.find {
                    it.programUid == programUid
                }?.matchConfidenceScoreThreshold?.coerceAtLeast(0)
            }

    fun clearCache() {
        cachedMappings.clear()
    }

    private companion object {
        private const val PROJECT_ID_MAPPING_KEY = "projectIdMapping"
    }
}
