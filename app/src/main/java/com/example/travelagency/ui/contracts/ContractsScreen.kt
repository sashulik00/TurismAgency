package com.example.travelagency.ui.contracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.travelagency.core.asRubles
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.core.asDate
import com.example.travelagency.data.dao.ContractListItem
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.components.TabTopAppBar
import com.example.travelagency.ui.theme.DiscordGreen
import com.example.travelagency.ui.theme.DiscordRed
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun ContractsScreen(onOpenContract: (Long) -> Unit) {
    val viewModel: ContractsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TabTopAppBar(R.string.contracts_title) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (state.contracts.isEmpty()) {
            Column(Modifier.padding(padding)) { EmptyListPlaceholder() }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            ) {
                items(state.contracts, key = { it.id }) { contract ->
                    ContractCard(contract, hideAmounts = state.hideAmounts, onClick = { onOpenContract(contract.id) })
                }
            }
        }
    }
}

@Composable
private fun ContractCard(contract: ContractListItem, hideAmounts: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(R.string.contract_number, contract.number),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(contract.status)
            }
            Text(contract.clientName, color = NavyText)
            Text(
                contract.tourName + " · " + contract.destination,
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
            val progress = if (contract.amountMinor == 0L) {
                0f
            } else {
                (contract.paidMinor.toFloat() / contract.amountMinor).coerceIn(0f, 1f)
            }
            LinearProgressIndicator(
                progress = { progress },
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Column {
                    Text(
                        stringResource(R.string.contract_paid, contract.paidMinor.asRublesSafe(hideAmounts)),
                        style = MaterialTheme.typography.bodySmall,
                        color = DiscordGreen,
                        fontWeight = FontWeight.SemiBold,
                    )
                    val remaining = contract.amountMinor - contract.paidMinor + contract.refundedMinor
                    if (remaining > 0 && contract.status == "ACTIVE") {
                        Text(
                            stringResource(R.string.contract_remaining, remaining.asRublesSafe(hideAmounts)),
                            style = MaterialTheme.typography.bodySmall,
                            color = DiscordRed,
                        )
                    }
                }
                AmountText(amountMinor = contract.amountMinor, hidden = hideAmounts, bold = true)
            }
            Text(
                stringResource(R.string.booking_created, contract.concludedAt.asDate()),
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
        }
    }
}

private fun Long.asRublesSafe(hidden: Boolean): String =
    if (hidden) "•••" else asRubles()
