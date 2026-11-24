package com.example.travelagency.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Светлая схема в стиле Discord: фиолетовые панели с белым текстом,
 * светло-серый фон, белые карточки, серые второстепенные тексты.
 */
private val TravelColorScheme = lightColorScheme(
    primary = DiscordBlurple,
    onPrimary = WhiteText,
    secondary = DiscordPurple,
    onSecondary = WhiteText,
    background = LightBackground,
    onBackground = DarkText,
    surface = WhiteCard,
    onSurface = DarkText,
    surfaceVariant = LightContainer,
    onSurfaceVariant = GrayText,
    secondaryContainer = WhiteCard,
    onSecondaryContainer = DarkText,
    primaryContainer = DiscordBlurple,
    onPrimaryContainer = WhiteText,
    error = DiscordRed,
    onError = WhiteText,
)

@Composable
fun TravelAgencyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TravelColorScheme,
        typography = TravelTypography,
        content = content,
    )
}
