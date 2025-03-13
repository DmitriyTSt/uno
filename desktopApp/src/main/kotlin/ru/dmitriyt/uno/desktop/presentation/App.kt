package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.core.domain.model.Rank
import ru.dmitriyt.uno.core.domain.util.isWild
import ru.dmitriyt.uno.core.domain.util.pileTop

private val viewModel = AppViewModel()

@Composable
fun App() {
    val state by viewModel.gameState.collectAsState(UiUnoState())

    var colorPickerDialogCard by remember { mutableStateOf<Card?>(null) }

    state.let { (desk, playableCards, currentPlayer, requiredColor, winner) ->
        Box(Modifier.fillMaxSize()) {
            val cardModifier = Modifier.width(90.dp).height(130.dp)
            if (desk != null && winner == null) {
                CircularLayout(Modifier.fillMaxSize().background(Color.DarkGray)) {
                    desk.players.forEach { player ->
                        Column {
                            FanLayout(Modifier.background(Color.Cyan)) {
                                player.cards.forEach { card ->
                                    if (player.name == "USER") {
                                        UnoCard(card, modifier = cardModifier.clickable {
                                            if (card.isWild()) {
                                                colorPickerDialogCard = card
                                            } else {
                                                viewModel.selectCard(card, null)
                                            }
                                        })
                                    } else {
                                        UnoUnknownCard(modifier = cardModifier)
                                    }
                                }
                            }
                            val fontWeight = if (player.name == currentPlayer?.name) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                            Text("Player ${player.name}", fontWeight = fontWeight)
                        }
                    }
                }

                UnoCard(desk.pileTop, modifier = cardModifier.align(Alignment.Center))

                if (requiredColor != null) {
                    Row(
                        Modifier
                            .align(Alignment.Center)
                    ) {
                        Box(
                            Modifier
                                .size(48.dp)
                        )
                        Spacer(Modifier.width(146.dp))
                        Box(
                            Modifier.background(getComposeColor(requiredColor))
                                .size(48.dp)
                        )
                    }
                }
            }
            if (state.winner != null) {
                Text("WINNER: ${state.error}", style = MaterialTheme.typography.h4)
            }
        }
        Column {
            Text("PLAYERS = ${desk?.players?.map { it.name }}")
            Text("PLAYABLE CARDS = $playableCards")
            if (state.error != null) {
                Text("ERROR: ${state.error}")
            }
        }
    }

    if (colorPickerDialogCard != null) {
        Dialog(onDismissRequest = {
            colorPickerDialogCard = null
        }) {
            Row(Modifier.fillMaxSize()) {
                CardColor.entries.forEach { cardColor ->
                    Box(Modifier.background(getComposeColor(cardColor)).weight(1f).aspectRatio(1f).clickable {
                        viewModel.selectCard(colorPickerDialogCard!!, cardColor)
                        colorPickerDialogCard = null
                    })
                }
            }
        }
    }
}

@Composable
fun UnoUnknownCard(modifier: Modifier) {
    UnoBaseCard(Color.Gray, modifier = modifier) {
        Text("UNO", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun UnoCard(card: Card, modifier: Modifier = Modifier) {
    UnoBaseCard(getComposeColor(card.color), modifier = modifier) {
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
fun UnoBaseCard(color: Color, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 4.dp,
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        Box(Modifier.fillMaxSize().padding(vertical = 4.dp, horizontal = 8.dp)) {
            content()
        }
    }
}

private fun getComposeColor(color: CardColor?): Color {
    return when (color) {
        CardColor.Red -> Color(0xFFC40B00)
        CardColor.Green -> Color(0xFF328A0F)
        CardColor.Blue -> Color(0xFF074AA3)
        CardColor.Yellow -> Color(0xFFE8D005)
        null -> Color.Black
    }
}