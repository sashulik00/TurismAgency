package com.example.travelagency.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.travelagency.core.asDate
import com.example.travelagency.data.entity.AuditEntryEntity
import com.example.travelagency.ui.components.DetailTopAppBar
import com.example.travelagency.ui.components.EmptyListPlaceholder
import com.example.travelagency.ui.theme.NavyText

@Composable
internal fun AuditScreen(onBack: () -> Unit) {
    val viewModel: AuditViewModel = viewModel()
    val entries by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { DetailTopAppBar(title = stringResource(R.string.audit_title), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (entries.isEmpty()) {
            Column(Modifier.padding(padding)) { EmptyListPlaceholder() }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            ) {
                items(entries, key = { it.id }) { entry ->
                    AuditRow(entry)
                }
            }
        }
    }
}

@Composable
private fun AuditRow(entry: AuditEntryEntity) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                entry.entity + " #" + entry.entityId + " · " + entry.action,
                fontWeight = FontWeight.Bold,
                color = NavyText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(entry.details, color = NavyText, style = MaterialTheme.typography.bodySmall)
            Text(entry.at.asDate(), color = NavyText, style = MaterialTheme.typography.bodySmall)
        }
    }
}
