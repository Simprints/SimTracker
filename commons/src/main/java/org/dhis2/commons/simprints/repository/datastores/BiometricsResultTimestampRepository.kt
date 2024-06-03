package org.dhis2.commons.simprints.repository.datastores

import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Local-only, DHIS2 SDK-based, persisting multiple key-value entry storage
 * for keeping record of when a given DHIS2 TEI / Simprints beneficiary was biometrically unlocked
 */
class BiometricsResultTimestampRepository @Inject constructor(
    private val d2: D2,
) {

    fun setResultTimestamp(teiUid: String?, timestamp: Long?) {
        d2.dataStoreModule().localDataStore().value("$BIOMETRIC_RESULT_TIMESTAMP_PREFIX$teiUid")
            .blockingSet(timestamp?.toString())
    }

    fun getResultTimestamp(teiUid: String?): Long? =
        d2.dataStoreModule().localDataStore().value("$BIOMETRIC_RESULT_TIMESTAMP_PREFIX$teiUid")
            .blockingGet()?.value()?.toLongOrNull()

    private companion object {
        private const val BIOMETRIC_RESULT_TIMESTAMP_PREFIX = "biometricsResultTimestamp:"
    }
}
