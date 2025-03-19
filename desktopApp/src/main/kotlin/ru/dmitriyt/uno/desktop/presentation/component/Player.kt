package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Player

@Composable
fun Player(
    player: Player,
    playableCards: List<Card>,
    isUser: Boolean,
    isSelected: Boolean,
    isFanLayout: Boolean,
    onCardClick: (Card) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        HandLayout(isFanLayout = isFanLayout || !isUser) {
            player.cards.forEach { card ->
                if (isUser) {
                    UnoCard(
                        card = card,
                        size = DpSize(90.dp, 130.dp),
                        enabled = playableCards.contains(card),
                        onClick = {
                            onCardClick(card)
                        },
                    )
                } else {
                    UnoUnknownCard(size = DpSize(90.dp, 130.dp))
                }
            }
        }
        val fontWeight = if (isSelected) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        }
        Text(
            text = "Player ${player.name}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = fontWeight,
        )
    }
}

@Composable
private fun HandLayout(isFanLayout: Boolean, content: @Composable () -> Unit) {
    if (isFanLayout) {
        FanLayout(
            angleStep = 15f,
        ) {
            content()
        }
    } else {
        Row { content() }
    }
}