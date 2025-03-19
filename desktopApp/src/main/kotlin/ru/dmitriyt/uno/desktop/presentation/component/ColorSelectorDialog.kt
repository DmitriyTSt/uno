package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.desktop.presentation.extensions.toComposeColor

@Composable
fun ColorSelectorDialog(
    onDismissRequest: () -> Unit,
    onColorSelected: (CardColor) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Row(Modifier.fillMaxSize()) {
            CardColor.entries.forEach { cardColor ->
                Box(Modifier.background(cardColor.toComposeColor()).weight(1f).aspectRatio(1f).clickable {
                    onColorSelected(cardColor)
                })
            }
        }
    }
}