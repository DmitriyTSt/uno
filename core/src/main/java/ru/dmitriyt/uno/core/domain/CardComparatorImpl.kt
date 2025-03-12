package ru.dmitriyt.uno.core.domain

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Color

class CardComparatorImpl : CardComparator {

    override fun isSameRank(card1: Card, card2: Card): Boolean {
        return card1.rank == card2.rank
    }

    override fun isSameColor(card1: Card, card2: Card): Boolean {
        return card1.color == card2.color
    }

    override fun isSameColor(color1: Color?, color2: Color?): Boolean {
        return color1 == color2
    }

    override fun isSameCard(card1: Card, card2: Card): Boolean {
        return card1 == card2
    }
}
