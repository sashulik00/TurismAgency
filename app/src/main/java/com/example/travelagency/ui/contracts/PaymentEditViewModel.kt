package com.example.travelagency.ui.contracts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.core.parseRubles
import com.example.travelagency.data.entity.PaymentEntity
import com.example.travelagency.data.entity.PaymentKind
import com.example.travelagency.data.entity.PaymentMethod
import com.example.travelagency.ui.navigation.PaymentEditRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaymentEditUiState(
    val amountText: String = "",
    val kind: PaymentKind = PaymentKind.SURCHARGE,
    val method: PaymentMethod = PaymentMethod.CARD,
    val remainingMinor: Long = 0,
    val amountError: Boolean = false,
    val saved: Boolean = false,
)

class PaymentEditViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<PaymentEditRoute>()

    private val form = MutableStateFlow(PaymentEditUiState())
    private val remaining = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            val contract = container.contractsRepository.observeContractDetails(route.contractId).first()
            val remainingValue = (contract?.amountMinor ?: 0) - (contract?.paidMinor ?: 0) +
                (contract?.refundedMinor ?: 0)
            remaining.value = remainingValue
        }
    }

    val uiState: StateFlow<PaymentEditUiState> = combine(form, remaining) { currentForm, remainingValue ->
        currentForm.copy(remainingMinor = remainingValue)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PaymentEditUiState())

    fun onAmountChanged(value: String) {
        if (value.length <= 12 && (value.all(Char::isDigit) || value.isEmpty())) {
            form.value = form.value.copy(amountText = value, amountError = false)
        }
    }

    fun onKindSelected(kind: PaymentKind) {
        form.value = form.value.copy(kind = kind)
    }

    fun onMethodSelected(method: PaymentMethod) {
        form.value = form.value.copy(method = method)
    }

    fun save() {
        val amountMinor = parseRubles(form.value.amountText)
        if (amountMinor == null) {
            form.value = form.value.copy(amountError = true)
            return
        }
        viewModelScope.launch {
            container.paymentsRepository.save(
                PaymentEntity(
                    contractId = route.contractId,
                    kind = form.value.kind,
                    method = form.value.method,
                    amountMinor = amountMinor,
                    paidAt = System.currentTimeMillis(),
                ),
            )
            form.value = form.value.copy(saved = true)
        }
    }
}
