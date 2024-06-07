package com.simprints.simprints.repository.datastores

import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Local-only, DHIS2 SDK-based, persisting multiple key-value entry storage
 * for keeping record of if a given DHIS2 TEI / Simprints beneficiary was biometrically unlocked
 */
class BiometricsResultSuccessRepository @Inject constructor(
    private val d2: D2,
) {

    fun setResultSuccess(teiUid: String?, success: Boolean?) {
        d2.dataStoreModule().localDataStore().value("$BIOMETRICS_RESULT_SUCCESS_PREFIX$teiUid")
            .blockingSet(success?.toString())
    }

    fun getResultSuccess(teiUid: String?): Boolean =
        d2.dataStoreModule().localDataStore().value("$BIOMETRICS_RESULT_SUCCESS_PREFIX$teiUid")
            .blockingGet()?.value().toBoolean()

    private companion object {
        private const val BIOMETRICS_RESULT_SUCCESS_PREFIX = "biometricsResultSuccess:"
    }
}
