package com.simprints.simprints.ui

import androidx.databinding.ObservableField
import com.simprints.simprints.SimprintsBiometricsAction
import com.simprints.simprints.SimprintsBiometricsState
import com.simprints.simprints.repository.SimprintsBiometricsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hisp.dhis.smscompression.models.UID
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext

// Reactively updating Simprints biometrics readout UI on state's flow updates, by using ViewBinding
// (this approach taken for simpler compatibility with other DHIS2 UI)
data class SimprintsBiometricsUiModel(
        private val repo: SimprintsBiometricsRepository,
        val teiUID: String,
        val onInteraction: (SimprintsBiometricsAction) -> Unit,
        private val coroutineScope: CoroutineScope
) {

    fun onClick() {
        onInteraction(SimprintsBiometricsAction(isOneToMany = false))
    }

    val liveBiometricsState = ObservableField<SimprintsBiometricsState>()
    val teiIsEnrolled = ObservableField<String>()
    val biometricLockingTimeout = ObservableField<String>()

    init {
        coroutineScope.launch {
            repo.getSimprintsBiometricsStateFlow(teiUID).collect {
                liveBiometricsState.set(it)
                biometricLockingTimeout.set(it.lastBiometricsResultTime())

                if (it.simprintsGuid != null) {
                    teiIsEnrolled.set("Verify")
                } else {
                    teiIsEnrolled.set("Enroll")
                }
            }
        }
    }
    private fun SimprintsBiometricsState.lastBiometricsResultTime(): String? =
            lastBiometricsResultTimestamp.takeIf { it != null }?.run {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this)
            }
}
