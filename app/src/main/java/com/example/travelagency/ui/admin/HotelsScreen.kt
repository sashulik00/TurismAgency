package com.example.travelagency.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.ListPickerDialog
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun HotelsScreen(onBack: () -> Unit) {
    val viewModel: HotelsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<HotelEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.hotels_title), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = null; showForm = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = WhiteText,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            state.errorMessageRes?.let { Text(stringResource(it), color = ErrorRed) }
            if (state.hotels.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.hotels, key = { it.id }) { hotel ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editing = HotelEntity(
                                        id = hotel.id,
                                        name = hotel.name,
                                        stars = hotel.stars,
                                        destinationId = hotel.destinationId,
                                    )
                                    showForm = true
                                },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(hotel.name, fontWeight = FontWeight.Bold, color = NavyText)
                                Text(
                                    hotel.destinationTitle + " · " +
                                        stringResource(R.string.hotel_stars_value, hotel.stars),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NavyText,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        HotelFormDialog(
            initial = editing,
            destinations = state.destinations,
            onSave = { viewModel.save(it); showForm = false },
            onDelete = { viewModel.delete(it.id); showForm = false },
            onDismiss = { showForm = false },
        )
    }
}

@Composable
private fun HotelFormDialog(
    initial: HotelEntity?,
    destinations: List<com.example.travelagency.data.entity.DestinationEntity>,
    onSave: (HotelEntity) -> Unit,
    onDelete: (HotelEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var stars by remember { mutableStateOf((initial?.stars ?: 4).toString()) }
    var destinationId by remember { mutableStateOf(initial?.destinationId ?: destinations.firstOrNull()?.id ?: 0L) }
    var showDestinationPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hotels_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.hotel_name)) }, singleLine = true)
                OutlinedTextField(
                    value = stars,
                    onValueChange = { if (it.length <= 1 && it.all(Char::isDigit)) stars = it },
                    label = { Text(stringResource(R.string.hotel_stars)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedButton(onClick = { showDestinationPicker = true }, modifier = Modifier.fillMaxWidth()) {
                    val destination = destinations.firstOrNull { it.id == destinationId }
                    Text(destination?.title ?: stringResource(R.string.hotel_destination))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val starsValue = stars.toIntOrNull()?.coerceIn(1, 5) ?: 4
                    onSave(
                        HotelEntity(
                            id = initial?.id ?: 0,
                            name = name.trim(),
                            stars = starsValue,
                            destinationId = destinationId,
                        ),
                    )
                },
                enabled = name.isNotBlank() && destinationId != 0L,
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            if (initial != null) {
                TextButton(onClick = { onDelete(initial) }) { Text(stringResource(R.string.action_delete), color = ErrorRed) }
            } else {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
            }
        },
    )

    if (showDestinationPicker) {
        ListPickerDialog(
            title = stringResource(R.string.hotel_destination),
            items = destinations,
            itemLabel = { it.title },
            onPick = { destinationId = it.id },
            onDismiss = { showDestinationPicker = false },
        )
    }
}
