package com.example.travelagency.ui.tours

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.travelagency.data.dao.TourListItem
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.ListPickerDialog
import com.example.travelagency.ui.components.SearchField
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.components.TabTopAppBar
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun ToursScreen(onOpenTour: (Long) -> Unit, onAddTour: () -> Unit) {
    val viewModel: ToursViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDestinationPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabTopAppBar(R.string.tours_title)
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                SearchField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChanged,
                    hintRes = R.string.tour_search_hint,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                ) {
                    FilterChip(
                        selected = state.onlyActive,
                        onClick = { viewModel.onOnlyActiveChanged(!state.onlyActive) },
                        label = { Text(stringResource(R.string.tour_filter_active)) },
                    )
                    FilterChip(
                        selected = state.selectedDestinationId != 0L,
                        onClick = { showDestinationPicker = true },
                        label = {
                            val destination = state.destinations.firstOrNull { it.id == state.selectedDestinationId }
                            Text(destination?.title ?: stringResource(R.string.menu_destinations))
                        },
                    )
                }
                if (state.tours.isEmpty()) {
                    EmptyListPlaceholder()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(state.tours, key = { it.id }) { tour ->
                            TourCard(tour = tour, hideAmounts = state.hideAmounts, onClick = { onOpenTour(tour.id) })
                        }
                    }
                }
                }
                if (state.isAdmin) {
                    FloatingActionButton(
                        onClick = onAddTour,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = WhiteText,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomEnd)
                            .padding(16.dp),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
                    }
                }
            }
        }
    }

    if (showDestinationPicker) {
        ListPickerDialog(
            title = stringResource(R.string.menu_destinations),
            items = state.destinations,
            itemLabel = { it.title },
            onPick = { viewModel.onDestinationSelected(if (it.id == state.selectedDestinationId) 0L else it.id) },
            onDismiss = { showDestinationPicker = false },
        )
    }
}

@Composable
private fun TourCard(tour: TourListItem, hideAmounts: Boolean, onClick: () -> Unit) {
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
                    tour.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(tour.status)
            }
            Text(
                "${tour.country}, ${tour.city} · ${tour.hotelName} (${tour.stars}★)",
                style = MaterialTheme.typography.bodyMedium,
                color = NavyText,
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Column {
                    Text(
                        stringResource(R.string.tour_departure, tour.departureDate.asDate()),
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyText,
                    )
                    Text(
                        stringResource(R.string.tour_nights, tour.nights) + " · " +
                            stringResource(R.string.tour_meal, tour.mealName),
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyText,
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    AmountText(amountMinor = tour.priceMinor, hidden = hideAmounts, bold = true)
                    Text(
                        stringResource(R.string.tour_quota, tour.quota),
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyText,
                    )
                }
            }
        }
    }
}
