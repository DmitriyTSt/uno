package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.util.pileTop
import ru.dmitriyt.uno.desktop.presentation.extensions.toComposeColor
import ru.dmitriyt.uno.desktop.presentation.model.UiUnoState

@Composable
fun Desk(
    desk: Desk,
    state: UiUnoState,
    isFanHandLayout: Boolean,
    onPlayerCardClick: (Card) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        CircularLayout(
            startAngle = 90f,
            modifier = Modifier.fillMaxSize().background(Color.LightGray),
        ) {
            desk.players.forEach { player ->
                Player(
                    player = player,
                    playableCards = state.playableCards,
                    isUser = player.name == "USER",
                    isSelected = player.name == state.selectedPlayer?.name,
                    isFanLayout = isFanHandLayout,
                    onCardClick = { card ->
                        onPlayerCardClick(card)
                    },
                )
            }
        }

        UnoCard(desk.pileTop, size = DpSize(90.dp, 130.dp), modifier = Modifier.Companion.align(Alignment.Center))

        if (state.requiredColor != null) {
            Row(
                Modifier.Companion
                    .align(Alignment.Center)
            ) {
                Box(
                    Modifier
                        .size(48.dp)
                )
                Spacer(Modifier.width(146.dp))
                Box(
                    Modifier.background(state.requiredColor.toComposeColor())
                        .size(48.dp)
                )
            }
        }
    }
}
