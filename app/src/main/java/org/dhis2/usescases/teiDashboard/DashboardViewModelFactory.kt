package org.dhis2.usescases.teiDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simprints.simprints.repository.SimprintsBiometricsRepository
import org.dhis2.commons.viewmodel.DispatcherProvider
import org.dhis2.utils.analytics.AnalyticsHelper

@Suppress("UNCHECKED_CAST")
class DashboardViewModelFactory(
    val repository: DashboardRepository,
    private val simprintsBiometricsRepository: SimprintsBiometricsRepository,
    val analyticsHelper: AnalyticsHelper,
    val dispatcher: DispatcherProvider,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(
                repository,
                analyticsHelper,
                dispatcher,
        ) as T
    }
}
