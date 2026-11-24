package com.example.travelagency.ui.contracts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.dao.ContractListItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ContractsUiState(
    val contracts: List<ContractListItem> = emptyList(),
    val hideAmounts: Boolean = false,
)

class ContractsViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container

    val uiState: StateFlow<ContractsUiState> = combine(
        container.contractsRepository.observeContracts(),
        container.settingsRepository.hideAmounts,
    ) { contracts, hideAmounts ->
        ContractsUiState(contracts = contracts, hideAmounts = hideAmounts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContractsUiState())
}
