package com.example.travelagency.ui.contracts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.ContractListItem
import com.example.travelagency.data.entity.PaymentEntity
import com.example.travelagency.ui.navigation.ContractDetailsRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ContractDetailsUiState(
    val contract: ContractListItem? = null,
    val payments: List<PaymentEntity> = emptyList(),
    val hideAmounts: Boolean = false,
)

class ContractDetailsViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<ContractDetailsRoute>()

    private val cancelled = MutableStateFlow(false)

    val uiState: StateFlow<ContractDetailsUiState> = combine(
        container.contractsRepository.observeContractDetails(route.contractId),
        container.paymentsRepository.observeByContract(route.contractId),
        container.settingsRepository.hideAmounts,
        cancelled,
    ) { contract, payments, hideAmounts, _ ->
        ContractDetailsUiState(
            contract = contract,
            payments = payments,
            hideAmounts = hideAmounts,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContractDetailsUiState())

    /** Расторжение: договор и бронь отменяются в одной транзакции, места возвращаются триггером. */
    fun cancelContract() {
        val bookingId = uiState.value.contract?.bookingId ?: return
        viewModelScope.launch {
            container.bookingsRepository.cancelBookingWithContract(bookingId)
            cancelled.value = true
        }
    }
}
