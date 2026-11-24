package com.example.travelagency.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра в стиле Discord: фирменный фиолетовый, серые оттенки, статусы красный/зелёный.
val DiscordBlurple = Color(0xFF5865F2)     // основной фиолетовый — панели и кнопки
val DiscordPurple = Color(0xFF7C5CFF)      // акцентный фиолетовый — вторичные кнопки, FAB
val DiscordGreen = Color(0xFF23A55A)       // подтверждено / оплачено / актуален
val DiscordRed = Color(0xFFED4245)         // отменено / расторгнуто / не оплачено
val DiscordYellow = Color(0xFFF0B232)      // ожидает / в процессе
val LightBackground = Color(0xFFF2F3F5)    // светло-серый фон экранов
val WhiteCard = Color(0xFFFFFFFF)          // белые карточки
val LightContainer = Color(0xFFE3E5E8)     // светло-серые подложки
val DarkText = Color(0xFF2B2D31)           // основной тёмно-серый текст
val GrayText = Color(0xFF6D6F78)           // второстепенный серый текст
val WhiteText = Color(0xFFFFFFFF)

// Полупрозрачные версии для панелей (сквозь них слегка виден фон).
val TranslucentBar = DiscordBlurple.copy(alpha = 0.96f)
val TranslucentPurple = DiscordPurple.copy(alpha = 0.92f)

// Тексты статусов (насыщенные, на полупрозрачной подложке своего цвета).
val GreenStatusText = Color(0xFF178A4B)
val RedStatusText = Color(0xFFC93A3D)
val YellowStatusText = Color(0xFF9A7400)

// Совместимые имена старой темы — ведут на новую палитру.
val NavyText = DarkText
val ErrorRed = DiscordRed
val BluePrimary = DiscordBlurple
val OrangeAccent = DiscordPurple
