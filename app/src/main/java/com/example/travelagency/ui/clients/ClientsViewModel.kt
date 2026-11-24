package com.example.travelagency.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.entity.ClientEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ClientsUiState(
    val clients: List<ClientEntity> = emptyList(),
    val query: String = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
class ClientsViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val query = MutableStateFlow("")

    val uiState: StateFlow<ClientsUiState> = query
        .flatMapLatest { current ->
            container.clientsRepository.observeClients(current).map { clients ->
                ClientsUiState(clients = clients, query = current)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientsUiState())

    fun onQueryChanged(value: String) {
        query.value = value
    }
}
