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
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun MealTypesScreen(onBack: () -> Unit) {
    val viewModel: MealTypesViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val mealTypes = state.items
    val errorMessageRes = state.errorMessageRes
    var editing by remember { mutableStateOf<MealTypeEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.meal_types_title), onBack = onBack) },
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
            if (mealTypes.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(mealTypes, key = { it.id }) { mealType ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editing = mealType
                                    showForm = true
                                },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(mealType.code, fontWeight = FontWeight.Bold, color = NavyText)
                                Text(mealType.name, color = NavyText)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        MealTypeFormDialog(
            initial = editing,
            onSave = { viewModel.save(it); showForm = false },
            onDelete = { viewModel.delete(it.id); showForm = false },
            onDismiss = { showForm = false },
        )
    }
}

@Composable
private fun MealTypeFormDialog(
    initial: MealTypeEntity?,
    onSave: (MealTypeEntity) -> Unit,
    onDelete: (MealTypeEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.meal_types_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = code, onValueChange = { code = it.uppercase().take(4) }, label = { Text(stringResource(R.string.meal_type_code)) }, singleLine = true)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.meal_type_name)) }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(MealTypeEntity(id = initial?.id ?: 0, code = code.trim(), name = name.trim())) },
                enabled = code.isNotBlank() && name.isNotBlank(),
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
