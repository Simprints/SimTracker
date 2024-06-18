package com.simprints.simprints.touchpoints.app.usescases.teidashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.simprints.repository.SimprintsBiometricsRepository
import com.simprints.simprints.ui.SimprintsBiometricsUiModel
import kotlinx.coroutines.launch

class SimprintsTEIDataViewModel(
    private val simprintsBiometricsRepository: SimprintsBiometricsRepository
) : ViewModel() {

    var simprintsRecordLocked = true
    val biometricLockState = MutableLiveData<Boolean>(true)

    init {
        viewModelScope.launch {
            simprintsBiometricsRepository.getSimprintsBiometricsStateFlow().collect {
                simprintsRecordLocked = it.isLocked(System.currentTimeMillis())
                biometricLockState.postValue(simprintsRecordLocked)
            }
        }
    }

    fun getSimprintsBiometricsUiModel(
        teiUid: String,
        programUid: String?,
    ): SimprintsBiometricsUiModel = SimprintsBiometricsUiModel(
        simprintsBiometricsRepository.getSimprintsBiometricsStateFlow(
            teiUid,
            programUid,
        ),
        simprintsBiometricsRepository::dispatchSimprintsAction,
    )
}
