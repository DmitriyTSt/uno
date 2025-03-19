package ru.dmitriyt.uno.desktop.presentation.model

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.model.Player

data class UiUnoState(
    val desk: Desk? = null,
    val playableCards: List<Card> = emptyList(),
    val selectedPlayer: Player? = null,
    val requiredColor: CardColor? = null,
    val error: String? = null,
    val winner: String? = null,
)