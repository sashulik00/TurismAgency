package com.example.travelagency.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.data.entity.EmployeeEntity
import com.example.travelagency.data.entity.EmployeeRole
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.StatusChip
import com.example.travelagency.ui.theme.ErrorRed
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun EmployeesScreen(onBack: () -> Unit) {
    val viewModel: EmployeesViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val employees = state.items
    val errorMessageRes = state.errorMessageRes
    var editing by remember { mutableStateOf<EmployeeEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.employees_title), onBack = onBack) },
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
            if (employees.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(employees, key = { it.id }) { employee ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editing = employee
                                    showForm = true
                                },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(employee.fullName, fontWeight = FontWeight.Bold, color = NavyText)
                                Text(employee.position, style = MaterialTheme.typography.bodySmall, color = NavyText)
                                Row(modifier = Modifier.padding(top = 4.dp)) {
                                    StatusChip(employee.role.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        EmployeeFormDialog(
            initial = editing,
            onSave = { item, password -> viewModel.save(item, password); showForm = false },
            onDelete = { viewModel.delete(it.id); showForm = false },
            onDismiss = { showForm = false },
        )
    }
}

@Composable
private fun EmployeeFormDialog(
    initial: EmployeeEntity?,
    onSave: (EmployeeEntity, String?) -> Unit,
    onDelete: (EmployeeEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var fullName by remember { mutableStateOf(initial?.fullName ?: "") }
    var position by remember { mutableStateOf(initial?.position ?: "") }
    var role by remember { mutableStateOf(initial?.role ?: EmployeeRole.MANAGER) }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.employees_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text(stringResource(R.string.employee_full_name)) }, singleLine = true)
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text(stringResource(R.string.employee_position)) }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = role == EmployeeRole.ADMIN,
                        onClick = { role = EmployeeRole.ADMIN },
                        label = { Text(stringResource(R.string.menu_role_admin)) },
                    )
                    FilterChip(
                        selected = role == EmployeeRole.MANAGER,
                        onClick = { role = EmployeeRole.MANAGER },
                        label = { Text(stringResource(R.string.menu_role_manager)) },
                    )
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.employee_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        EmployeeEntity(
                            id = initial?.id ?: 0,
                            fullName = fullName.trim(),
                            position = position.trim(),
                            role = role,
                            passwordHash = initial?.passwordHash ?: "",
                        ),
                        password.ifBlank { null },
                    )
                },
                enabled = fullName.isNotBlank() && (initial != null || password.length >= MIN_PASSWORD_LENGTH),
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

private const val MIN_PASSWORD_LENGTH = 4
