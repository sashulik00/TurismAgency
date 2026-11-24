package com.example.travelagency.ui.tours

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.TourDetailsRow
import com.example.travelagency.data.entity.EmployeeRole
import com.example.travelagency.ui.navigation.TourDetailsRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TourDetailsUiState(
    val tour: TourDetailsRow? = null,
    val hideAmounts: Boolean = false,
    val isAdmin: Boolean = false,
)

/** Аргумент маршрута приходит в ViewModel через SavedStateHandle — приём из лекции 09. */
class TourDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<TourDetailsRoute>()

    val uiState: StateFlow<TourDetailsUiState> = combine(
        container.toursRepository.observeTourDetails(route.tourId),
        container.settingsRepository.hideAmounts,
        container.sessionRepository.currentEmployee,
    ) { tour, hideAmounts, employee ->
        TourDetailsUiState(
            tour = tour,
            hideAmounts = hideAmounts,
            isAdmin = employee?.role == EmployeeRole.ADMIN,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TourDetailsUiState())

    fun discontinue() {
        viewModelScope.launch { container.toursRepository.setDiscontinued(route.tourId) }
    }

    fun activate() {
        viewModelScope.launch { container.toursRepository.setActive(route.tourId) }
    }
}
