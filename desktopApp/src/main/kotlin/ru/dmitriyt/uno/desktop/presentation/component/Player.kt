package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Player
import ru.dmitriyt.uno.desktop.presentation.FanLayout

@Composable
fun Player(
    player: Player,
    playableCards: List<Card>,
    isUser: Boolean,
    isSelected: Boolean,
    onCardClick: (Card) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        FanLayout(Modifier.background(Color.Cyan)) {
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
        Text("Player ${player.name}", fontWeight = fontWeight)
    }
}