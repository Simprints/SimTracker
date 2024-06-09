package com.simprints.simprints.touchpoints.app.usescases.teidashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simprints.simprints.repository.SimprintsBiometricsRepository

@Suppress("UNCHECKED_CAST")
class SimprintsTEIDataViewModelFactory (
    private val simprintsBiometricsRepository: SimprintsBiometricsRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SimprintsTEIDataViewModel(simprintsBiometricsRepository) as T
    }

}