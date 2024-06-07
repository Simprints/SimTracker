package com.simprints.simprints.repository.datastores

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprints.Constants
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Healthcare programs in this SimTracker app, and in DHIS2 in general,
 * can be mapped to Simprints ID projects, and for each of them there can be a separate setting for:
 * for how long to consider a TEI's profile unlocked by the means of Simprints biometrics.
 *
 * In a DHIS2 instance backend, the linkages between DHIS2 programs and Simprints biometric timeouts
 * are stored in the JSON-based datastore
 * accessible from admin's web UI dashboard via Datastore Management
 * under the namespace simprints and key projectIdMapping, as an array or JSON objects
 * each consisting of string key-values,
 * among keys of which there are programUid and biometricLockingTimeoutMinutes.
 *
 * In the SimTracker app, these mappings are persisted on device storage by the DHIS2 SDK read-only;
 * this repository is not responsible for mappings syncing from the DHIS2 backend to device storage.
 */
class SimprintsProjectBiometricLockingTimeoutRepository @Inject constructor(
    private val d2: D2,
    private val gson: Gson,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class BiometricLockingTimeoutMapping(
        val programUid: String? = null,
        val biometricLockingTimeoutMinutes: Int? = null,
    )

    private val cachedMappings: MutableMap<String, List<BiometricLockingTimeoutMapping>> =
        mutableMapOf()

    fun getTimeoutMinutes(programUid: String?): Int? =
        d2.dataStoreModule().dataStore()
            .value(Constants.SIMPRINTS_NAMESPACE, PROJECT_ID_MAPPING_KEY)
            .blockingGet()?.value()?.run {
                cachedMappings.getOrPut(this) {
                    cachedMappings.clear()
                    gson.fromJson(
                        this,
                        object : TypeToken<List<BiometricLockingTimeoutMapping>>() {}.type,
                    )
                }.find {
                    it.programUid == programUid
                }?.biometricLockingTimeoutMinutes?.coerceAtLeast(0)
            }

    fun clearCache() {
        cachedMappings.clear()
    }

    private companion object {
        private const val PROJECT_ID_MAPPING_KEY = "projectIdMapping"
    }
}
