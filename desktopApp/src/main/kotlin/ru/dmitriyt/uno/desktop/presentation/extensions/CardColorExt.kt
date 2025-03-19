package ru.dmitriyt.uno.desktop.presentation.extensions

import androidx.compose.ui.graphics.Color
import ru.dmitriyt.uno.core.domain.model.CardColor

fun CardColor?.toComposeColor(): Color {
    return when (this) {
        CardColor.Red -> Color(0xFFC40B00)
        CardColor.Green -> Color(0xFF328A0F)
        CardColor.Blue -> Color(0xFF074AA3)
        CardColor.Yellow -> Color(0xFFE8D005)
        null -> Color.Black
    }
}