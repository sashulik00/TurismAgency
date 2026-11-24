package com.example.travelagency.ui.contracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.core.asRubles
import com.example.travelagency.data.entity.PaymentKind
import com.example.travelagency.data.entity.PaymentMethod
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.theme.DiscordGreen
import com.example.travelagency.ui.theme.DiscordRed
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun PaymentEditScreen(contractId: Long, onDone: () -> Unit) {
    val viewModel: PaymentEditViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.payment_new_title), onBack = onDone) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(R.string.contract_remaining, state.remainingMinor.asRubles()),
                color = if (state.remainingMinor > 0) DiscordRed else DiscordGreen,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
            )
            OutlinedTextField(
                value = state.amountText,
                onValueChange = viewModel::onAmountChanged,
                label = { Text(stringResource(R.string.payment_amount)) },
                singleLine = true,
                isError = state.amountError,
                supportingText = if (state.amountError) {
                    { Text(stringResource(R.string.payment_error_amount), color = ErrorRed) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(stringResource(R.string.payment_kind), fontWeight = FontWeight.Bold, color = NavyText)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentKind.entries.forEach { kind ->
                    FilterChip(
                        selected = state.kind == kind,
                        onClick = { viewModel.onKindSelected(kind) },
                        label = { Text(paymentKindLabel(kind)) },
                    )
                }
            }
            Text(stringResource(R.string.payment_method), fontWeight = FontWeight.Bold, color = NavyText)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.entries.forEach { method ->
                    FilterChip(
                        selected = state.method == method,
                        onClick = { viewModel.onMethodSelected(method) },
                        label = { Text(paymentMethodLabel(method)) },
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(stringResource(R.string.action_save), fontWeight = FontWeight.Bold)
            }
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
