@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.travelagency.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.travelagency.R
import com.example.travelagency.core.asRubles
import com.example.travelagency.ui.theme.DarkText
import com.example.travelagency.ui.theme.DiscordGreen
import com.example.travelagency.ui.theme.DiscordRed
import com.example.travelagency.ui.theme.DiscordYellow
import com.example.travelagency.ui.theme.GrayText
import com.example.travelagency.ui.theme.GreenStatusText
import com.example.travelagency.ui.theme.LightContainer
import com.example.travelagency.ui.theme.RedStatusText
import com.example.travelagency.ui.theme.TranslucentBar
import com.example.travelagency.ui.theme.WhiteText
import com.example.travelagency.ui.theme.YellowStatusText

/** Цвет статуса: (фон-подложка, текст) — стиль бейджей Discord, полупрозрачная плашка. */
private data class StatusStyle(val background: Color, val text: Color)

private val STATUS_STYLES = mapOf(
    "CONFIRMED" to StatusStyle(DiscordGreen, GreenStatusText),
    "ACTIVE" to StatusStyle(DiscordGreen, GreenStatusText),
    "COMPLETED" to StatusStyle(DiscordGreen, GreenStatusText),
    "PENDING" to StatusStyle(DiscordYellow, YellowStatusText),
    "CANCELLED" to StatusStyle(DiscordRed, RedStatusText),
    "DISCONTINUED" to StatusStyle(DiscordRed, RedStatusText),
)

private val STATUS_LABELS = mapOf(
    "PENDING" to R.string.booking_status_pending,
    "CONFIRMED" to R.string.booking_status_confirmed,
    "CANCELLED" to R.string.booking_status_cancelled,
    "ACTIVE" to R.string.tour_status_active,
    "DISCONTINUED" to R.string.tour_status_discontinued,
    "COMPLETED" to R.string.contract_status_completed,
)

/** Статус-бейдж: полупрозрачная подложка своего цвета + насыщенный текст (как в Discord). */
@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val label = STATUS_LABELS[status]?.let { stringResource(it) } ?: status
    val style = STATUS_STYLES[status] ?: StatusStyle(LightContainer, GrayText)
    Surface(
        color = style.background.copy(alpha = STATUS_CHIP_ALPHA),
        contentColor = style.text,
        shape = RoundedCornerShape(50),
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

private const val STATUS_CHIP_ALPHA = 0.16f

/** Сумма из копеек; скрывается настройкой «Скрывать суммы»; цвет задаётся экраном. */
@Composable
fun AmountText(
    amountMinor: Long,
    hidden: Boolean,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = if (hidden) "••• ₽" else amountMinor.asRubles(),
        color = color,
        style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        fontWeight = if (bold) FontWeight.Bold else null,
        modifier = modifier,
    )
}

/** Поле поиска над списками: белая карточка на сером фоне. */
@Composable
fun SearchField(value: String, onValueChange: (String) -> Unit, hintRes: Int, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(hintRes), color = GrayText) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = DarkText,
            unfocusedTextColor = DarkText,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Фиолетовая полупрозрачная панель-заголовок с белым текстом. */
@Composable
fun TabTopAppBar(titleRes: Int) {
    TopAppBar(
        title = { Text(stringResource(titleRes), color = WhiteText) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TranslucentBar),
    )
}

/**
 * Панель деталей: стрелка «назад» иконкой, заголовок в одну строку с многоточием
 * (исправляет перенос в 4 строки на длинных названиях), действие справа.
 */
@Composable
fun DetailTopAppBar(
    title: String,
    onBack: () -> Unit,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                title,
                color = WhiteText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = WhiteText)
            }
        },
        actions = {
            if (actionText != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(actionText, color = WhiteText, maxLines = 1)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TranslucentBar),
    )
}

/** Строка «подпись — значение» на серой подложке внутри белой карточки. */
@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = DarkText) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LightContainer, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GrayText)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun EmptyListPlaceholder(text: String = stringResource(R.string.empty_list)) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = GrayText,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    )
}

/** Диалог подтверждения опасного действия. */
@Composable
fun ConfirmDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_delete)) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.action_delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

/**
 * Простой выбор из списка в диалоге: клик по строке возвращает элемент.
 * Используется для выбора клиента, тура и направления.
 */
@Composable
fun <T> ListPickerDialog(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onPick: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, items) {
        if (query.isBlank()) items else items.filter { itemLabel(it).contains(query, ignoreCase = true) }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (items.size > MIN_ITEMS_FOR_SEARCH) {
                    SearchField(value = query, onValueChange = { query = it }, hintRes = R.string.search_hint)
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(filtered) { item ->
                        Text(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPick(item)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

private const val MIN_ITEMS_FOR_SEARCH = 6
