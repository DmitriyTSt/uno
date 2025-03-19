package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Rank
import ru.dmitriyt.uno.desktop.presentation.extensions.toComposeColor

@Composable
fun UnoCard(
    card: Card,
    size: DpSize,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    UnoBaseCard(
        color = card.color.toComposeColor(),
        size = size,
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        val textStyle = MaterialTheme.typography.body1
        val color = Color.White
        val text = when (card.rank) {
            Rank.WILD -> "O"
            Rank.WILD_DRAW_4 -> "O+4"
            Rank.SKIP -> "S"
            Rank.DRAW_2 -> "+2"
            Rank.REVERSE -> "R"
            else -> card.rank.points.toString()
        }
        Text(text = text, color = color, style = textStyle, modifier = Modifier.align(Alignment.TopStart))
        Text(text = text, color = color, style = MaterialTheme.typography.h2, modifier = Modifier.align(Alignment.Center))
        Text(text = text, color = color, style = textStyle, modifier = Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun UnoUnknownCard(
    size: DpSize,
    modifier: Modifier = Modifier,
) {
    UnoBaseCard(
        color = Color.Gray,
        size = size,
        modifier = modifier,
    ) {
        Text("UNO", modifier = Modifier.align(Alignment.Center))
    }
}