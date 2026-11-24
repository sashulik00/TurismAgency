package com.example.travelagency.ui.bookings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.BookingListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class BookingsUiState(
    val bookings: List<BookingListItem> = emptyList(),
    val statusFilter: String = "ALL",
    val hideAmounts: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BookingsViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val statusFilter = MutableStateFlow("ALL")

    val uiState: StateFlow<BookingsUiState> = statusFilter
        .flatMapLatest { status ->
            combine(
                container.bookingsRepository.observeBookings(status),
                container.settingsRepository.hideAmounts,
            ) { bookings, hideAmounts ->
                BookingsUiState(bookings = bookings, statusFilter = status, hideAmounts = hideAmounts)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookingsUiState())

    fun onStatusFilterChanged(status: String) {
        statusFilter.value = status
    }
}
