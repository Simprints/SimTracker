package org.dhis2.utils.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.dhis2.R
import org.dhis2.commons.resources.ColorUtils
import org.dhis2.commons.simprints.ui.SimprintsBiometricsUiModel
import org.dhis2.databinding.SimprintsBiometricLockingBottomDialogBindingImpl
import org.dhis2.form.BR

/**
 * Popup generally similar to BreakTheGlassBottomDialog, but containing the same
 * TEI's Simprints biometrics UI status readout and biometrics action button
 * as the biometrics form in the TEI's profile.
 */
class SimprintsBiometricLockingBottomDialog(
    private val simprintsBiometricsUiModel: SimprintsBiometricsUiModel,
) : BottomSheetDialogFragment() {

    val colorUtils: ColorUtils = ColorUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BreakTheGlassBottomSheetDialogTheme)

        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return SimprintsBiometricLockingBottomDialogBindingImpl.inflate(inflater, container, false)
            .apply {
                simprintsBiometrics.setVariable(
                    BR.simprintsBiometricsUiModel,
                    simprintsBiometricsUiModel,
                )
                cancel.apply {
                    setOnClickListener {
                        dismiss()
                    }
                }
            }.root
    }
}
