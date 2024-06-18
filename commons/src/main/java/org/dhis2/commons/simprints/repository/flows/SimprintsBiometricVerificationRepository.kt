package org.dhis2.commons.simprints.repository.flows

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.simprints.libsimprints.Constants
import com.simprints.libsimprints.SimHelper
import com.simprints.libsimprints.Verification
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.dhis2.commons.simprints.repository.flows.SimprintsBiometricConstants.GUID
import org.dhis2.commons.simprints.repository.flows.SimprintsBiometricConstants.MODULE_ID
import org.dhis2.commons.simprints.repository.flows.SimprintsBiometricConstants.PROJECT_ID
import org.dhis2.commons.simprints.repository.flows.SimprintsBiometricConstants.USER_ID
import javax.inject.Inject

data class SimprintsBiometricVerificationResult(
    val simprintsMatchConfidenceScore: Int? = null,
)

class SimprintsBiometricVerificationRepository @Inject constructor(
    private val context: Context,
) {

    private companion object {
        // This SimCapture app, after calling Simprints ID app, may be removed from memory by system
        // so the Simprints ID result is returned by a flow rather than as a function return value.
        private val verificationResultFlow: MutableSharedFlow<SimprintsBiometricVerificationResult> =
            MutableSharedFlow(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

        private fun Intent?.get(key: String): String =
            this?.getStringExtra(key) ?: ""
    }

    fun getVerificationResultFlow(): SharedFlow<SimprintsBiometricVerificationResult> =
        verificationResultFlow

    fun launchVerify(projectId: String, moduleId: String, userId: String, guid: String) {
        context.startActivity(
            Intent(context, SimprintsBiometricVerificationHandlingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PROJECT_ID, projectId)
                putExtra(MODULE_ID, moduleId)
                putExtra(USER_ID, userId)
                putExtra(GUID, guid)
            },
        )
    }

    // Serves as an Android-specific and Simprints ID-compatible data transporter
    // between this SimCapture and the separate Simprints ID app. Not part of the UI.
    class SimprintsBiometricVerificationHandlingActivity : ComponentActivity() {

        private val registerForVerifyResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { activityResult ->
            activityResult.data?.takeIf { intent ->
                intent.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK, false)
            }?.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getParcelableExtra(Constants.SIMPRINTS_VERIFICATION, Verification::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    getParcelableExtra(Constants.SIMPRINTS_VERIFICATION)
                }
            }.let { verification ->
                verificationResultFlow.tryEmit(
                    SimprintsBiometricVerificationResult(
                        simprintsMatchConfidenceScore = verification?.confidence?.toInt(),
                    ),
                )
            }
            finish()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerForVerifyResult.launch(
                with(intent) {
                    SimHelper(get(PROJECT_ID), get(USER_ID)).verify(get(MODULE_ID), get(GUID))
                },
            )
        }
    }
}
