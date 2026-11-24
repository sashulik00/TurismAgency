package com.example.travelagency.ui.tours

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.ListPickerDialog
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.GrayText
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun TourEditScreen(onDone: () -> Unit) {
    val viewModel: TourEditViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showOperatorPicker by remember { mutableStateOf(false) }
    var showDestinationPicker by remember { mutableStateOf(false) }
    var showHotelPicker by remember { mutableStateOf(false) }
    var showMealPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = if (state.isNew) {
                    stringResource(R.string.tour_add_title)
                } else {
                    stringResource(R.string.tour_edit_title)
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
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text(stringResource(R.string.tour_name)) },
                singleLine = true,
                isError = TourFieldError.NAME in state.errors,
                supportingText = errorSupport(TourFieldError.NAME, R.string.tour_error_name, state.errors),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            PickerCard(
                label = stringResource(R.string.tour_operator_field),
                value = state.operator?.name,
                onClick = { showOperatorPicker = true },
            )
            PickerCard(
                label = stringResource(R.string.details_destination),
                value = state.destination?.title,
                error = TourFieldError.PICKERS in state.errors && state.destination == null,
                onClick = { showDestinationPicker = true },
            )
            PickerCard(
                label = stringResource(R.string.tour_hotel_field),
                value = state.hotel?.let { hotel ->
                    state.destinations.firstOrNull { it.id == hotel.destinationId }?.let { "${hotel.name} (${it.title})" }
                        ?: hotel.name
                },
                error = TourFieldError.PICKERS in state.errors && state.hotel == null,
                onClick = { showHotelPicker = true },
            )
            PickerCard(
                label = stringResource(R.string.tour_meal_field),
                value = state.mealType?.let { "${it.name} (${it.code})" },
                error = TourFieldError.PICKERS in state.errors && state.mealType == null,
                onClick = { showMealPicker = true },
            )
            OutlinedTextField(
                value = state.departureText,
                onValueChange = viewModel::onDepartureChanged,
                label = { Text(stringResource(R.string.tour_departure_field)) },
                singleLine = true,
                isError = TourFieldError.DEPARTURE in state.errors,
                supportingText = errorSupport(TourFieldError.DEPARTURE, R.string.tour_error_departure, state.errors),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.nightsText,
                    onValueChange = viewModel::onNightsChanged,
                    label = { Text(stringResource(R.string.tour_nights_field)) },
                    singleLine = true,
                    isError = TourFieldError.NIGHTS in state.errors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.quotaText,
                    onValueChange = viewModel::onQuotaChanged,
                    label = { Text(stringResource(R.string.tour_quota_field)) },
                    singleLine = true,
                    isError = TourFieldError.QUOTA in state.errors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = state.priceText,
                onValueChange = viewModel::onPriceChanged,
                label = { Text(stringResource(R.string.tour_price)) },
                singleLine = true,
                isError = TourFieldError.PRICE in state.errors,
                supportingText = errorSupport(TourFieldError.PRICE, R.string.tour_error_price, state.errors),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text(stringResource(R.string.tour_description)) },
                minLines = 2,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
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

    if (showOperatorPicker) {
        ListPickerDialog(
            title = stringResource(R.string.tour_operator_field),
            items = state.operators,
            itemLabel = { it.name },
            onPick = { viewModel.onOperatorSelected(if (it.id == state.operator?.id) null else it) },
            onDismiss = { showOperatorPicker = false },
        )
    }
    if (showDestinationPicker) {
        ListPickerDialog(
            title = stringResource(R.string.details_destination),
            items = state.destinations,
            itemLabel = { it.title },
            onPick = { viewModel.onDestinationSelected(it) },
            onDismiss = { showDestinationPicker = false },
        )
    }
    if (showHotelPicker) {
        val destinationId = state.destination?.id
        val hotels = if (destinationId == null) state.hotels else state.hotels.filter { it.destinationId == destinationId }
        ListPickerDialog(
            title = stringResource(R.string.tour_hotel_field),
            items = hotels,
            itemLabel = { hotel ->
                state.destinations.firstOrNull { it.id == hotel.destinationId }?.let { "${hotel.name} (${it.title})" }
                    ?: hotel.name
            },
            onPick = { viewModel.onHotelSelected(it) },
            onDismiss = { showHotelPicker = false },
        )
    }
    if (showMealPicker) {
        ListPickerDialog(
            title = stringResource(R.string.tour_meal_field),
            items = state.mealTypes,
            itemLabel = { "${it.name} (${it.code})" },
            onPick = { viewModel.onMealSelected(it) },
            onDismiss = { showMealPicker = false },
        )
    }
}

@Composable
private fun PickerCard(label: String, value: String?, error: Boolean = false, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = GrayText)
            Text(
                value ?: "—",
                fontWeight = FontWeight.Bold,
                color = if (error) ErrorRed else NavyText,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun errorSupport(
    field: TourFieldError,
    messageRes: Int,
    errors: List<TourFieldError>,
): (@Composable () -> Unit)? =
    if (field in errors) {
        { Text(stringResource(messageRes), color = ErrorRed) }
    } else {
        null
    }
