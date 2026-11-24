package com.example.travelagency.ui.tours

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.InfoRow
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun TourDetailsScreen(
    tourId: Long,
    onBook: (Long) -> Unit,
    onEditTour: () -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: TourDetailsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = state.tour?.name ?: "",
                onBack = onBack,
                actionText = if (state.isAdmin) stringResource(R.string.tour_action_edit) else null,
                onAction = onEditTour,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val tour = state.tour
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (tour == null) {
                Text(stringResource(R.string.empty_list))
            } else {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.details_departure) + ": " + tour.departureDate.asDate(),
                                color = NavyText,
                            )
                            StatusChip(tour.status)
                        }
                        AmountText(amountMinor = tour.priceMinor, hidden = state.hideAmounts, bold = true)
                    }
                }
                Text(stringResource(R.string.tour_description), fontWeight = FontWeight.Bold, color = NavyText)
                Text(tour.description, color = NavyText)
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
                            label = stringResource(R.string.details_destination),
                            value = "${tour.country}, ${tour.city}",
                        )
                        InfoRow(
                            label = stringResource(R.string.details_hotel),
                            value = "${tour.hotelName} (${tour.stars}★)",
                        )
                        InfoRow(label = stringResource(R.string.details_meal), value = "${tour.mealName} (${tour.mealCode})")
                        InfoRow(
                            label = stringResource(R.string.details_operator),
                            value = tour.operatorName ?: "—",
                        )
                        InfoRow(
                            label = stringResource(R.string.details_quota),
                            value = tour.quota.toString(),
                        )
                        InfoRow(
                            label = stringResource(R.string.details_nights),
                            value = stringResource(R.string.tour_nights, tour.nights),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (tour.status == "ACTIVE") {
                    Button(
                        onClick = { onBook(tourId) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    ) {
                        Text(stringResource(R.string.tour_action_book), fontWeight = FontWeight.Bold)
                    }
                }
                if (state.isAdmin) {
                    OutlinedButton(
                        onClick = {
                            if (tour.status == "ACTIVE") viewModel.discontinue() else viewModel.activate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text(
                            if (tour.status == "ACTIVE") {
                                stringResource(R.string.tour_action_discontinue)
                            } else {
                                stringResource(R.string.tour_action_restore)
                            },
                        )
                    }
                }
            }
        }
    }
}
