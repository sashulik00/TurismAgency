package com.example.travelagency.ui.contracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.core.asDate
import com.example.travelagency.core.asRubles
import com.example.travelagency.data.entity.PaymentEntity
import com.example.travelagency.data.entity.PaymentKind
import com.example.travelagency.data.entity.PaymentMethod
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.ConfirmDialog
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.InfoRow
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.theme.DiscordGreen
import com.example.travelagency.ui.theme.DiscordRed
import com.example.travelagency.ui.theme.GrayText
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun ContractDetailsScreen(contractId: Long, onAddPayment: () -> Unit, onBack: () -> Unit) {
    val viewModel: ContractDetailsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCancelDialog by remember { mutableStateOf(false) }
    val contract = state.contract

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = contract?.let { stringResource(R.string.contract_number, it.number) } ?: "",
                onBack = onBack,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (contract == null) {
                item { EmptyListPlaceholder() }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            InfoRow(label = stringResource(R.string.contract_of_client), value = contract.clientName)
                            InfoRow(label = stringResource(R.string.booking_tour), value = contract.tourName)
                            InfoRow(label = stringResource(R.string.details_destination), value = contract.destination)
                            InfoRow(
                                label = stringResource(R.string.booking_created, contract.concludedAt.asDate()),
                                value = "",
                            )
                            InfoRow(
                                label = stringResource(R.string.contract_amount, contract.amountMinor.asRubles()),
                                value = "",
                            )
                            InfoRow(
                                label = stringResource(R.string.contract_paid, contract.paidMinor.asRubles()),
                                value = "",
                                valueColor = DiscordGreen,
                            )
                            val remaining = contract.amountMinor - contract.paidMinor + contract.refundedMinor
                            InfoRow(
                                label = stringResource(R.string.contract_remaining, remaining.asRubles()),
                                value = "",
                                valueColor = if (remaining > 0) DiscordRed else DiscordGreen,
                            )
                            Row(modifier = Modifier.padding(top = 4.dp)) {
                                StatusChip(contract.status)
                            }
                        }
                    }
                }
                item {
                    Text(
                        stringResource(R.string.contract_payments_title),
                        fontWeight = FontWeight.Bold,
                        color = NavyText,
                    )
                }
                if (state.payments.isEmpty()) {
                    item { EmptyListPlaceholder(stringResource(R.string.payments_empty)) }
                } else {
                    items(state.payments, key = { it.id }) { payment ->
                        PaymentRow(payment, hideAmounts = state.hideAmounts)
                    }
                }
                if (contract.status == "ACTIVE") {
                    item {
                        Button(
                            onClick = onAddPayment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .height(48.dp),
                        ) {
                            Text(stringResource(R.string.payment_new_title))
                        }
                    }
                    item {
                        Button(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        ) {
                            Text(stringResource(R.string.contract_action_cancel))
                        }
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        ConfirmDialog(
            text = stringResource(R.string.contract_cancel_confirm),
            onConfirm = {
                showCancelDialog = false
                viewModel.cancelContract()
            },
            onDismiss = { showCancelDialog = false },
        )
    }
}

@Composable
private fun PaymentRow(payment: PaymentEntity, hideAmounts: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(paymentKindLabel(payment.kind), fontWeight = FontWeight.Bold, color = NavyText)
                AmountText(
                    amountMinor = payment.amountMinor,
                    hidden = hideAmounts,
                    bold = true,
                    color = if (payment.kind == PaymentKind.REFUND) DiscordRed else DiscordGreen,
                )
            }
            Text(
                paymentMethodLabel(payment.method) + " · " + payment.paidAt.asDate(),
                style = MaterialTheme.typography.bodySmall,
                color = GrayText,
            )
        }
    }
}

@Composable
private fun paymentKindLabel(kind: PaymentKind): String = when (kind) {
    PaymentKind.FULL -> stringResource(R.string.payment_kind_full)
    PaymentKind.PREPAY -> stringResource(R.string.payment_kind_prepay)
    PaymentKind.SURCHARGE -> stringResource(R.string.payment_kind_surcharge)
    PaymentKind.REFUND -> stringResource(R.string.payment_kind_refund)
}

@Composable
private fun paymentMethodLabel(method: PaymentMethod): String = when (method) {
    PaymentMethod.CASH -> stringResource(R.string.payment_method_cash)
    PaymentMethod.CARD -> stringResource(R.string.payment_method_card)
    PaymentMethod.TRANSFER -> stringResource(R.string.payment_method_transfer)
    PaymentMethod.SBP -> stringResource(R.string.payment_method_sbp)
}
