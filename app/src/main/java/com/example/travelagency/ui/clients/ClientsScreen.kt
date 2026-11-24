package com.example.travelagency.ui.clients

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.travelagency.data.entity.ClientEntity
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.components.SearchField
import com.example.travelagency.ui.components.TabTopAppBar
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun ClientsScreen(onOpenClient: (Long) -> Unit, onAddClient: () -> Unit) {
    val viewModel: ClientsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TabTopAppBar(R.string.clients_title) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClient,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = WhiteText,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            SearchField(
                value = state.query,
                onValueChange = viewModel::onQueryChanged,
                hintRes = R.string.client_search_hint,
                modifier = Modifier.padding(top = 12.dp),
            )
            if (state.clients.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.clients, key = { it.id }) { client ->
                        ClientCard(client = client, onClick = { onOpenClient(client.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientCard(client: ClientEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                client.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyText,
            )
            Text(client.phone, style = MaterialTheme.typography.bodyMedium, color = NavyText)
            Text(
                stringResource(R.string.client_passport, client.passportSeries, client.passportNumber),
                style = MaterialTheme.typography.bodySmall,
                color = NavyText,
            )
        }
    }
}
