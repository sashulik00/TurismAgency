package com.example.travelagency.ui.bookings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.components.TabTopAppBar
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

private val statusFilters = listOf("ALL", "PENDING", "CONFIRMED", "CANCELLED")

@Composable
internal fun BookingsScreen(onOpenBooking: (Long) -> Unit, onAddBooking: () -> Unit) {
    val viewModel: BookingsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TabTopAppBar(R.string.bookings_title) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBooking,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = WhiteText,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
            ) {
                statusFilters.forEach { status ->
                    FilterChip(
                        selected = state.statusFilter == status,
                        onClick = { viewModel.onStatusFilterChanged(status) },
                        label = { Text(filterLabel(status)) },
                    )
                }
            }
            if (state.bookings.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.bookings, key = { it.id }) { booking ->
                        BookingCard(booking, hideAmounts = state.hideAmounts, onClick = { onOpenBooking(booking.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun filterLabel(status: String): String = when (status) {
    "ALL" -> stringResource(R.string.booking_filter_all)
    "PENDING" -> stringResource(R.string.booking_status_pending)
    "CONFIRMED" -> stringResource(R.string.booking_status_confirmed)
    else -> stringResource(R.string.booking_status_cancelled)
}

@Composable
private fun BookingCard(booking: BookingListItem, hideAmounts: Boolean, onClick: () -> Unit) {
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
                    booking.clientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(booking.status)
            }
            Text(booking.tourName, color = NavyText)
            Text(
                booking.destination + " · " + booking.clientPhone,
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
            ) {
                Text(
                    stringResource(R.string.booking_tourists) + ": " + booking.touristsCount +
                        " · " + booking.createdAt.asDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyText,
                )
                AmountText(amountMinor = booking.totalPriceMinor, hidden = hideAmounts, bold = true)
            }
        }
    }
}
