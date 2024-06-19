package com.simprints.simprints.repository.datastores

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

private typealias TeiUidToProgramUid = Pair<String?, String?>

/**
 * Local-only, DHIS2 SDK-based, persisting single key-value entry storage
 * for keeping record of one run of the separate Simprints ID app for an Activity result
 * while this SimCapture app is in the background and at risk of being removed from memory by system
 */
class SimprintsBiometricsProgressRepository @Inject constructor(
    private val d2: D2,
    private val gson: Gson,
) {

    fun setSimprintsBiometricsInProgress(teiUid: String?, programUid: String?) {
        d2.dataStoreModule().localDataStore().value(SIMPRINTS_BIOMETRICS_PROGRESS)
            .blockingSet(gson.toJson(TeiUidToProgramUid(teiUid, programUid)))
    }

    fun getSimprintsBiometricsFinished(): TeiUidToProgramUid =
        d2.dataStoreModule().localDataStore().value(SIMPRINTS_BIOMETRICS_PROGRESS).run {
            blockingGet()?.value()?.also { blockingDelete() }?.run {
                gson.fromJson(this, object : TypeToken<TeiUidToProgramUid>() {}.type)
            } ?: (null to null)
        }

    private companion object {
        private const val SIMPRINTS_BIOMETRICS_PROGRESS = "simprintsBiometricsProgress"
    }
}
