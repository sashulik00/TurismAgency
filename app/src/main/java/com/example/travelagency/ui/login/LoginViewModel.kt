package com.example.travelagency.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.entity.EmployeeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LoginUiState(
    val employees: List<EmployeeEntity> = emptyList(),
    val selectedEmployeeId: Long = 0,
    val password: String = "",
    val showError: Boolean = false,
    val loggedIn: Boolean = false,
)

private data class LoginFields(
    val selectedEmployeeId: Long = 0,
    val password: String = "",
    val showError: Boolean = false,
    val loggedIn: Boolean = false,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionRepository =
        (application as TravelAgencyApplication).container.sessionRepository

    private val fields = MutableStateFlow(LoginFields())

    val uiState: StateFlow<LoginUiState> = combine(
        sessionRepository.observeEmployees(),
        fields,
        sessionRepository.lastEmployeeId,
    ) { employees, fields, lastId ->
        LoginUiState(
            employees = employees,
            selectedEmployeeId = if (fields.selectedEmployeeId == 0L && employees.isNotEmpty()) {
                lastId
            } else {
                fields.selectedEmployeeId
            },
            password = fields.password,
            showError = fields.showError,
            loggedIn = fields.loggedIn,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LoginUiState())

    fun onEmployeeSelected(id: Long) {
        fields.value = fields.value.copy(selectedEmployeeId = id, showError = false)
    }

    fun onPasswordChanged(value: String) {
        fields.value = fields.value.copy(password = value, showError = false)
    }

    fun login() {
        val current = fields.value
        if (current.selectedEmployeeId == 0L || current.password.isBlank()) {
            fields.value = current.copy(showError = true)
            return
        }
        viewModelScope.launch {
            if (!sessionRepository.login(current.selectedEmployeeId, current.password)) {
                fields.value = fields.value.copy(showError = true)
            } else {
                fields.value = fields.value.copy(loggedIn = true)
            }
        }
    }
}
