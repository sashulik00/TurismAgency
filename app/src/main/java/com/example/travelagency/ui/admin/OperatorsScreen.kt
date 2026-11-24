package com.example.travelagency.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.travelagency.data.entity.TourOperatorEntity
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun OperatorsScreen(onBack: () -> Unit) {
    val viewModel: OperatorsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val operators = state.items
    val errorMessageRes = state.errorMessageRes
    var editing by remember { mutableStateOf<TourOperatorEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.operators_title), onBack = onBack) },
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
            if (operators.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(operators, key = { it.id }) { operator ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editing = operator
                                    showForm = true
                                },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(operator.name, fontWeight = FontWeight.Bold, color = NavyText)
                                Text(operator.contactPerson, style = MaterialTheme.typography.bodySmall, color = NavyText)
                                Text(
                                    operator.phone + " · " + operator.email,
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
        OperatorFormDialog(
            initial = editing,
            onSave = { viewModel.save(it); showForm = false },
            onDelete = { viewModel.delete(it.id); showForm = false },
            onDismiss = { showForm = false },
        )
    }
}

@Composable
private fun OperatorFormDialog(
    initial: TourOperatorEntity?,
    onSave: (TourOperatorEntity) -> Unit,
    onDelete: (TourOperatorEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var contact by remember { mutableStateOf(initial?.contactPerson ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var email by remember { mutableStateOf(initial?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.operators_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.operator_name)) }, singleLine = true)
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text(stringResource(R.string.operator_contact)) }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.operator_phone)) }, singleLine = true)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.operator_email)) }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        TourOperatorEntity(
                            id = initial?.id ?: 0,
                            name = name.trim(),
                            contactPerson = contact.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                        ),
                    )
                },
                enabled = name.isNotBlank(),
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
