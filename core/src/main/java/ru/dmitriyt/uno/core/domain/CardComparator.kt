package ru.dmitriyt.uno.core.domain

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor

interface CardComparator {
    fun isSameRank(card1: Card, card2: Card): Boolean
    fun isSameColor(card1: Card, card2: Card): Boolean
    fun isSameColor(color1: CardColor?, color2: CardColor?): Boolean
    fun isSameCard(card1: Card, card2: Card): Boolean
}
