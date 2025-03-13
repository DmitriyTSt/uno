package ru.dmitriyt.uno.core.domain.util

import ru.dmitriyt.uno.core.domain.CardComparator
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Rank
import ru.dmitriyt.uno.core.domain.strategy.Move

fun Card.isWild(): Boolean {
    return this.rank == Rank.WILD || this.rank == Rank.WILD_DRAW_4
}

fun Card.isPositiveAction(): Boolean {
    return this.rank == Rank.SKIP || this.rank == Rank.DRAW_2
}

fun Card.isNumeric(): Boolean {
    return this.rank.points in 0..9
}

fun List<Card>.playableCards(move: Move, pileTop: Card, cardComparator: CardComparator): List<Card> {
    val predicate = when (move) {
        Move.Execute -> { card: Card -> cardComparator.isSameRank(card, pileTop) }
        is Move.GiveColor -> { card: Card -> card.color == move.color || card.isWild() }
        Move.Proceed -> { card: Card ->
            cardComparator.isSameRank(card, pileTop) ||
                cardComparator.isSameColor(card, pileTop) ||
                card.isWild()
        }
    }
    return filter(predicate)
}