package com.example.travelagency.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.R
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.BookingListItem
import com.example.travelagency.data.entity.ClientEntity
import com.example.travelagency.ui.navigation.ClientDetailsRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientDetailsUiState(
    val client: ClientEntity? = null,
    val bookings: List<BookingListItem> = emptyList(),
    val hideAmounts: Boolean = false,
    val errorMessageRes: Int? = null,
    val deleted: Boolean = false,
)

class ClientDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<ClientDetailsRoute>()

    private val error = MutableStateFlow<Int?>(null)
    private val deleted = MutableStateFlow(false)

    val uiState: StateFlow<ClientDetailsUiState> = combine(
        container.clientsRepository.observeClient(route.clientId),
        container.bookingsRepository.observeBookingsOfClient(route.clientId),
        container.settingsRepository.hideAmounts,
        error,
        deleted,
    ) { client, bookings, hideAmounts, errorRes, isDeleted ->
        ClientDetailsUiState(
            client = client,
            bookings = bookings,
            hideAmounts = hideAmounts,
            errorMessageRes = errorRes,
            deleted = isDeleted,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientDetailsUiState())

    fun delete() {
        viewModelScope.launch {
            try {
                container.clientsRepository.delete(route.clientId)
                deleted.value = true
            } catch (e: IllegalStateException) {
                error.value = R.string.error_foreign_key
            }
        }
    }
}
