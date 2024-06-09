package com.simprints.simprints.touchpoints.app.usescases.teidashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.simprints.repository.SimprintsBiometricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SimprintsTEIDataViewModel (
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

}