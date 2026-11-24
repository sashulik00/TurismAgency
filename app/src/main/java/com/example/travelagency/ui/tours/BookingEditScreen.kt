package com.example.travelagency.ui.tours

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.core.asDate
import com.example.travelagency.data.entity.BookingStatus
import com.example.travelagency.ui.components.AmountText
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.ListPickerDialog
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.GrayText
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun BookingEditScreen(bookingId: Long, presetTourId: Long, onDone: () -> Unit) {
    val viewModel: BookingEditViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showClientPicker by remember { mutableStateOf(false) }
    var showTourPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = if (state.isNew) {
                    stringResource(R.string.booking_new_title)
                } else {
                    stringResource(R.string.booking_edit_title, state.bookingId)
                },
                onBack = onDone,
            )
        },
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
            // Клиент
            SelectorCard(
                label = stringResource(R.string.booking_client),
                value = state.client?.fullName,
                subtitle = state.client?.phone,
                enabled = state.isNew,
                onClick = { if (state.isNew) showClientPicker = true },
            )
            if (BookingFieldError.CLIENT in state.fieldErrors) {
                FieldErrorText(stringResource(R.string.booking_error_client))
            }

            // Тур
            SelectorCard(
                label = stringResource(R.string.booking_tour),
                value = state.tour?.name,
                subtitle = state.tour?.let { "${it.country}, ${it.city} · ${it.hotelName} · ${it.departureDate.asDate()}" },
                enabled = !state.tourFixed,
                onClick = { if (!state.tourFixed) showTourPicker = true },
            )
            if (BookingFieldError.TOUR in state.fieldErrors) {
                FieldErrorText(stringResource(R.string.booking_error_tour))
            }

            // Туристы и итог
            OutlinedTextField(
                value = state.tourists,
                onValueChange = viewModel::onTouristsChanged,
                label = { Text(stringResource(R.string.booking_tourists)) },
                singleLine = true,
                isError = BookingFieldError.TOURISTS in state.fieldErrors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                enabled = state.isNew,
                modifier = Modifier.fillMaxWidth(),
            )
            if (BookingFieldError.TOURISTS in state.fieldErrors) {
                FieldErrorText(stringResource(R.string.booking_error_tourists))
            }
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    Text(stringResource(R.string.details_total), color = NavyText)
                    AmountText(amountMinor = state.totalMinor, hidden = false, bold = true)
                }
            }

            // Предоплата при создании
            if (state.isNew) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.payment_kind_prepay), color = NavyText)
                    Switch(checked = state.withPrepayment, onCheckedChange = viewModel::onPrepaymentChanged)
                }
            } else if (state.status == BookingStatus.CANCELLED) {
                // Отменённая бронь не изменяется: договор уже расторгнут, места возвращены.
                Text(stringResource(R.string.booking_status_cancelled), color = NavyText)
            } else {
                Text(stringResource(R.string.booking_status), fontWeight = FontWeight.Bold, color = NavyText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BookingStatus.entries.forEach { status ->
                        FilterChip(
                            selected = state.status == status,
                            onClick = { viewModel.onStatusSelected(status) },
                            label = { Text(bookingStatusLabel(status)) },
                        )
                    }
                }
            }

            state.errorMessageRes?.let { FieldErrorText(stringResource(it)) }

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

    if (showClientPicker) {
        ListPickerDialog(
            title = stringResource(R.string.booking_client),
            items = state.clients,
            itemLabel = { "${it.fullName} (${it.phone})" },
            onPick = viewModel::onClientSelected,
            onDismiss = { showClientPicker = false },
        )
    }
    if (showTourPicker) {
        ListPickerDialog(
            title = stringResource(R.string.booking_tour),
            items = state.availableTours,
            itemLabel = { "${it.name} · ${it.country}, ${it.city}" },
            onPick = viewModel::onTourSelected,
            onDismiss = { showTourPicker = false },
        )
    }
}

@Composable
private fun bookingStatusLabel(status: BookingStatus): String = when (status) {
    BookingStatus.PENDING -> stringResource(R.string.booking_status_pending)
    BookingStatus.CONFIRMED -> stringResource(R.string.booking_status_confirmed)
    BookingStatus.CANCELLED -> stringResource(R.string.booking_status_cancelled)
}

/** Карточка-селектор: высота по содержимому — не «расплющивается» на любом экране. */
@Composable
private fun SelectorCard(
    label: String,
    value: String?,
    subtitle: String?,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = GrayText)
            Text(
                value ?: "—",
                fontWeight = FontWeight.Bold,
                color = NavyText,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = GrayText)
            }
        }
    }
}

@Composable
private fun FieldErrorText(text: String) {
    Text(text, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
}
