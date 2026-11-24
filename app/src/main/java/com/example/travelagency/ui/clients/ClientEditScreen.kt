package com.example.travelagency.ui.clients

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelagency.R
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.theme.ErrorRed

@Composable
internal fun ClientEditScreen(clientId: Long, onDone: () -> Unit) {
    val viewModel: ClientEditViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Scaffold(
        topBar = {
            DetailTopAppBar(
                title = if (state.clientId == 0L) {
                    stringResource(R.string.client_add_title)
                } else {
                    stringResource(R.string.client_edit_title)
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChanged,
                label = { Text(stringResource(R.string.client_last_name)) },
                singleLine = true,
                isError = ClientField.LAST_NAME in state.errors,
                supportingText = errorText(ClientField.LAST_NAME, state.errors, R.string.client_error_last_name),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChanged,
                label = { Text(stringResource(R.string.client_first_name)) },
                singleLine = true,
                isError = ClientField.FIRST_NAME in state.errors,
                supportingText = errorText(ClientField.FIRST_NAME, state.errors, R.string.client_error_first_name),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.middleName,
                onValueChange = viewModel::onMiddleNameChanged,
                label = { Text(stringResource(R.string.client_middle_name)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.birthDate,
                onValueChange = viewModel::onBirthDateChanged,
                label = { Text(stringResource(R.string.client_birth_date_hint)) },
                singleLine = true,
                isError = ClientField.BIRTH_DATE in state.errors,
                supportingText = errorText(ClientField.BIRTH_DATE, state.errors, R.string.client_error_birth_date),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.passportSeries,
                    onValueChange = viewModel::onPassportSeriesChanged,
                    label = { Text(stringResource(R.string.client_passport_series)) },
                    singleLine = true,
                    isError = ClientField.PASSPORT in state.errors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.passportNumber,
                    onValueChange = viewModel::onPassportNumberChanged,
                    label = { Text(stringResource(R.string.client_passport_number)) },
                    singleLine = true,
                    isError = ClientField.PASSPORT in state.errors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(2f),
                )
            }
            if (ClientField.PASSPORT in state.errors) {
                Text(stringResource(R.string.client_error_passport), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
            }
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChanged,
                label = { Text(stringResource(R.string.client_phone)) },
                singleLine = true,
                isError = ClientField.PHONE in state.errors,
                supportingText = errorText(ClientField.PHONE, state.errors, R.string.client_error_phone),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                label = { Text(stringResource(R.string.client_email)) },
                singleLine = true,
                isError = ClientField.EMAIL in state.errors,
                supportingText = errorText(ClientField.EMAIL, state.errors, R.string.client_error_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
}

@Composable
private fun errorText(field: ClientField, errors: List<ClientField>, messageRes: Int): (@Composable () -> Unit)? =
    if (field in errors) {
        { Text(stringResource(messageRes), color = ErrorRed) }
    } else {
        null
    }
