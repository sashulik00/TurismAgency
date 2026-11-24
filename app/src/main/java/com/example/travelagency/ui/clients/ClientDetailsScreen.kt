package com.example.travelagency.ui.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.travelagency.data.dao.BookingListItem
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.ConfirmDialog
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.InfoRow
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun ClientDetailsScreen(clientId: Long, onEdit: () -> Unit, onBack: () -> Unit) {
    val viewModel: ClientDetailsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = state.client?.fullName ?: "",
                onBack = onBack,
                actionText = stringResource(R.string.action_edit),
                onAction = onEdit,
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
            val client = state.client
            if (client == null) {
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
                            InfoRow(
                                label = stringResource(R.string.client_birth_date_hint),
                                value = client.birthDate.asDate(),
                            )
                            InfoRow(
                                label = stringResource(R.string.client_passport_number),
                                value = "${client.passportSeries} ${client.passportNumber}",
                            )
                            InfoRow(label = stringResource(R.string.client_phone), value = client.phone)
                            InfoRow(label = stringResource(R.string.client_email), value = client.email)
                        }
                    }
                }
                item {
                    Text(
                        stringResource(R.string.client_bookings_title),
                        fontWeight = FontWeight.Bold,
                        color = NavyText,
                    )
                }
                items(state.bookings, key = { it.id }) { booking ->
                    ClientBookingRow(booking, hideAmounts = state.hideAmounts)
                }
                state.errorMessageRes?.let { errorRes ->
                    item { Text(stringResource(errorRes), color = ErrorRed) }
                }
                item {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(48.dp),
                    ) {
                        Text(stringResource(R.string.action_delete))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            text = stringResource(R.string.action_delete) + " «${state.client?.fullName ?: ""}»?",
            onConfirm = {
                showDeleteDialog = false
                viewModel.delete()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
private fun ClientBookingRow(booking: BookingListItem, hideAmounts: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(booking.tourName, fontWeight = FontWeight.Bold, color = NavyText)
            Text(
                booking.destination + " · " + stringResource(R.string.booking_tourists) +
                    ": " + booking.touristsCount,
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AmountText(amountMinor = booking.totalPriceMinor, hidden = hideAmounts)
                StatusChip(booking.status)
            }
            Text(
                booking.createdAt.asDate(),
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
        }
    }
}
