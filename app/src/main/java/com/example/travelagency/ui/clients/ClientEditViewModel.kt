package com.example.travelagency.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.core.asDate
import com.example.travelagency.core.isAdultBirthDate
import com.example.travelagency.core.isValidEmail
import com.example.travelagency.core.isValidName
import com.example.travelagency.core.isValidPassport
import com.example.travelagency.core.isValidPhone
import com.example.travelagency.core.parseDate
import com.example.travelagency.data.entity.ClientEntity
import com.example.travelagency.ui.navigation.ClientEditRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Какие поля заполнены неверно — форма подсвечивает их (валидация из лекции 04). */
enum class ClientField { LAST_NAME, FIRST_NAME, BIRTH_DATE, PASSPORT, PHONE, EMAIL }

data class ClientEditUiState(
    val clientId: Long = 0,
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val passportSeries: String = "",
    val passportNumber: String = "",
    val phone: String = "",
    val email: String = "",
    val errors: List<ClientField> = emptyList(),
    val saved: Boolean = false,
)

class ClientEditViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<ClientEditRoute>()

    private val form = MutableStateFlow(ClientEditUiState(clientId = route.clientId))

    init {
        if (route.clientId != 0L) {
            viewModelScope.launch {
                container.clientsRepository.findClient(route.clientId)?.let { client ->
                    form.value = form.value.copy(
                        lastName = client.lastName,
                        firstName = client.firstName,
                        middleName = client.middleName,
                        birthDate = client.birthDate.asDate(),
                        passportSeries = client.passportSeries,
                        passportNumber = client.passportNumber,
                        phone = client.phone,
                        email = client.email,
                    )
                }
            }
        }
    }

    val uiState: StateFlow<ClientEditUiState> = form.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ClientEditUiState(clientId = route.clientId),
    )

    fun onLastNameChanged(value: String) {
        form.value = form.value.copy(lastName = value)
    }

    fun onFirstNameChanged(value: String) {
        form.value = form.value.copy(firstName = value)
    }

    fun onMiddleNameChanged(value: String) {
        form.value = form.value.copy(middleName = value)
    }

    fun onBirthDateChanged(value: String) {
        form.value = form.value.copy(birthDate = value)
    }

    fun onPassportSeriesChanged(value: String) {
        if (value.length <= 4 && value.all(Char::isDigit)) {
            form.value = form.value.copy(passportSeries = value)
        }
    }

    fun onPassportNumberChanged(value: String) {
        if (value.length <= 6 && value.all(Char::isDigit)) {
            form.value = form.value.copy(passportNumber = value)
        }
    }

    fun onPhoneChanged(value: String) {
        if (value.length <= 12) {
            form.value = form.value.copy(phone = value)
        }
    }

    fun onEmailChanged(value: String) {
        form.value = form.value.copy(email = value)
    }

    fun save() {
        val current = form.value
        val errors = buildList {
            if (!isValidName(current.lastName)) add(ClientField.LAST_NAME)
            if (!isValidName(current.firstName)) add(ClientField.FIRST_NAME)
            val birthMillis = parseDate(current.birthDate)
            if (birthMillis == null || !isAdultBirthDate(birthMillis)) add(ClientField.BIRTH_DATE)
            if (!isValidPassport(current.passportSeries, current.passportNumber)) add(ClientField.PASSPORT)
            if (!isValidPhone(current.phone)) add(ClientField.PHONE)
            if (!isValidEmail(current.email)) add(ClientField.EMAIL)
        }
        if (errors.isNotEmpty()) {
            form.value = current.copy(errors = errors)
            return
        }
        viewModelScope.launch {
            container.clientsRepository.save(
                ClientEntity(
                    id = route.clientId,
                    lastName = current.lastName.trim(),
                    firstName = current.firstName.trim(),
                    middleName = current.middleName.trim(),
                    birthDate = parseDate(current.birthDate) ?: 0L,
                    passportSeries = current.passportSeries.trim(),
                    passportNumber = current.passportNumber.trim(),
                    phone = current.phone.trim(),
                    email = current.email.trim(),
                ),
            )
            form.value = form.value.copy(saved = true)
        }
    }
}
