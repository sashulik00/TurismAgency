package com.example.travelagency.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun DestinationsScreen(onBack: () -> Unit) {
    val viewModel: DestinationsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val destinations = state.items
    val errorMessageRes = state.errorMessageRes
    var editing by remember { mutableStateOf<DestinationEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.destinations_title), onBack = onBack) },
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
            errorMessageRes?.let { Text(stringResource(it), color = ErrorRed) }
            if (destinations.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(destinations, key = { it.id }) { destination ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editing = destination
                                    showForm = true
                                },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(destination.country, fontWeight = FontWeight.Bold, color = NavyText)
                                Text(destination.city, color = NavyText)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        DestinationFormDialog(
            initial = editing,
            onSave = { viewModel.save(it); showForm = false },
            onDelete = { viewModel.delete(it.id); showForm = false },
            onDismiss = { showForm = false },
        )
    }
}

@Composable
private fun DestinationFormDialog(
    initial: DestinationEntity?,
    onSave: (DestinationEntity) -> Unit,
    onDelete: (DestinationEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var country by remember { mutableStateOf(initial?.country ?: "") }
    var city by remember { mutableStateOf(initial?.city ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.destinations_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text(stringResource(R.string.destination_country)) }, singleLine = true)
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text(stringResource(R.string.destination_city)) }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(DestinationEntity(id = initial?.id ?: 0, country = country.trim(), city = city.trim()))
                },
                enabled = country.isNotBlank() && city.isNotBlank(),
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
}
