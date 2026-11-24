package com.example.travelagency.ui.admin

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.R
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.core.sha256
import com.example.travelagency.data.dao.HotelListItem
import com.example.travelagency.data.entity.AuditEntryEntity
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.EmployeeEntity
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.data.entity.TourOperatorEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Состояние экрана справочника: элементы + сообщение об ошибке БД. */
data class DirectoryUiState<T>(
    val items: List<T> = emptyList(),
    val errorMessageRes: Int? = null,
)

private class ErrorHolder {
    val messageRes = MutableStateFlow<Int?>(null)

    suspend fun runRepository(block: suspend () -> Unit) {
        try {
            block()
            messageRes.value = null
        } catch (e: SQLiteException) {
            messageRes.value = R.string.error_foreign_key
        }
    }
}

class OperatorsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository
    private val error = ErrorHolder()

    val uiState: StateFlow<DirectoryUiState<TourOperatorEntity>> = combine(
        repository.observeOperators(),
        error.messageRes,
    ) { items, errorRes -> DirectoryUiState(items, errorRes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DirectoryUiState())

    fun save(item: TourOperatorEntity) {
        viewModelScope.launch { error.runRepository { repository.saveOperator(item) } }
    }

    fun delete(id: Long) {
        viewModelScope.launch { error.runRepository { repository.deleteOperator(id) } }
    }
}

class DestinationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository
    private val error = ErrorHolder()

    val uiState: StateFlow<DirectoryUiState<DestinationEntity>> = combine(
        repository.observeDestinations(""),
        error.messageRes,
    ) { items, errorRes -> DirectoryUiState(items, errorRes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DirectoryUiState())

    fun save(item: DestinationEntity) {
        viewModelScope.launch { error.runRepository { repository.saveDestination(item) } }
    }

    fun delete(id: Long) {
        viewModelScope.launch { error.runRepository { repository.deleteDestination(id) } }
    }
}

data class HotelsUiState(
    val hotels: List<HotelListItem> = emptyList(),
    val destinations: List<DestinationEntity> = emptyList(),
    val errorMessageRes: Int? = null,
)

class HotelsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository
    private val error = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<HotelsUiState> = combine(
        repository.observeHotels(""),
        repository.observeDestinations(""),
        error,
    ) { hotels, destinations, errorRes ->
        HotelsUiState(hotels = hotels, destinations = destinations, errorMessageRes = errorRes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HotelsUiState())

    fun save(item: HotelEntity) {
        viewModelScope.launch {
            try {
                repository.saveHotel(item)
                error.value = null
            } catch (e: SQLiteException) {
                error.value = R.string.error_foreign_key
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteHotel(id)
                error.value = null
            } catch (e: SQLiteException) {
                error.value = R.string.error_foreign_key
            }
        }
    }
}

class MealTypesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository
    private val error = ErrorHolder()

    val uiState: StateFlow<DirectoryUiState<MealTypeEntity>> = combine(
        repository.observeMealTypes(),
        error.messageRes,
    ) { items, errorRes -> DirectoryUiState(items, errorRes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DirectoryUiState())

    fun save(item: MealTypeEntity) {
        viewModelScope.launch { error.runRepository { repository.saveMealType(item) } }
    }

    fun delete(id: Long) {
        viewModelScope.launch { error.runRepository { repository.deleteMealType(id) } }
    }
}

class EmployeesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository
    private val error = ErrorHolder()

    val uiState: StateFlow<DirectoryUiState<EmployeeEntity>> = combine(
        repository.observeEmployees(),
        error.messageRes,
    ) { items, errorRes -> DirectoryUiState(items, errorRes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DirectoryUiState())

    fun save(item: EmployeeEntity, password: String?) {
        viewModelScope.launch {
            error.runRepository {
                val withPassword = if (password.isNullOrBlank()) {
                    item
                } else {
                    item.copy(passwordHash = password.sha256())
                }
                repository.saveEmployee(withPassword)
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { error.runRepository { repository.deleteEmployee(id) } }
    }
}

class AuditViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as TravelAgencyApplication).container.directoryRepository

    val uiState: StateFlow<List<AuditEntryEntity>> = repository.observeAudit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
