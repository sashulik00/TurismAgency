package com.example.travelagency.ui.tours

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.TourListItem
import com.example.travelagency.data.entity.DestinationEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class ToursUiState(
    val tours: List<TourListItem> = emptyList(),
    val query: String = "",
    val onlyActive: Boolean = true,
    val destinations: List<DestinationEntity> = emptyList(),
    val selectedDestinationId: Long = 0,
    val hideAmounts: Boolean = false,
    val isAdmin: Boolean = false,
)

private data class TourFilters(val query: String, val onlyActive: Boolean, val destinationId: Long)

@OptIn(ExperimentalCoroutinesApi::class)
class ToursViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container

    private val filters = MutableStateFlow(TourFilters("", true, 0))

    /** Запрос к БД пересобирается при каждом изменении фильтров (flatMapLatest, лекция 11). */
    val uiState: StateFlow<ToursUiState> = filters
        .flatMapLatest { current ->
            combine(
                container.toursRepository.observeTours(current.query, current.onlyActive, current.destinationId),
                container.toursRepository.observeDestinations(),
                container.settingsRepository.hideAmounts,
                container.sessionRepository.currentEmployee,
            ) { tours, destinations, hideAmounts, employee ->
                ToursUiState(
                    tours = tours,
                    query = current.query,
                    onlyActive = current.onlyActive,
                    destinations = destinations,
                    selectedDestinationId = current.destinationId,
                    hideAmounts = hideAmounts,
                    isAdmin = employee?.role == com.example.travelagency.data.entity.EmployeeRole.ADMIN,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ToursUiState())

    fun onQueryChanged(value: String) {
        filters.value = filters.value.copy(query = value)
    }

    fun onOnlyActiveChanged(value: Boolean) {
        filters.value = filters.value.copy(onlyActive = value)
    }

    fun onDestinationSelected(destinationId: Long) {
        filters.value = filters.value.copy(destinationId = destinationId)
    }
}
