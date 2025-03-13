package ru.dmitriyt.uno.core.domain.strategy

import kotlinx.coroutines.delay
import ru.dmitriyt.uno.core.domain.CardComparator
import ru.dmitriyt.uno.core.domain.CardComparatorImpl
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.core.domain.model.Rank

open class RandomStrategy(
    private val emulateDelay: Boolean,
    private val cardComparator: CardComparator = CardComparatorImpl(),
) : Strategy {
    override suspend fun getStrategyMove(
        move: Move,
        cards: List<Card>,
        topCard: Card,
        playersCardCounts: List<Int>,
    ): StrategyMove {
        if (emulateDelay) {
            delay(500)
        }
        val maxColor = maxColor(cards)
        val wildCard = cards.find { it.rank == Rank.WILD }
        val wildDraw4Card = cards.find { it.rank == Rank.WILD_DRAW_4 }
        val (card, color) = when (move) {
            is Move.GiveColor -> {
                val sameColorCards = cards.filter { cardComparator.isSameColor(it.color, move.color) }
                if (sameColorCards.isNotEmpty() && wildCard != null) {
                    wildCard to maxColor
                } else if (sameColorCards.isEmpty() && (wildCard != null || wildDraw4Card != null)) {
                    val card = wildDraw4Card
                        ?: wildCard
                        ?: throw IllegalStateException("Must have at least one wild card")
                    card to maxColor
                } else if (sameColorCards.isNotEmpty()) {
                    val card = sameColorCards.random()
                    card to null
                } else {
                    throw IllegalStateException("Must have one playable card")
                }
            }
            Move.Execute -> {
                val card = cards.find { cardComparator.isSameRank(it, topCard) }
                    ?: throw IllegalStateException("Must have at least one card with rank")
                card to null
            }
            Move.Proceed -> {
                val sameColorOrRankCards = cards.filter {
                    cardComparator.isSameRank(it, topCard) || cardComparator.isSameColor(it, topCard)
                }
                if (sameColorOrRankCards.isNotEmpty()) {
                    val card = sameColorOrRankCards.random()
                    card to null
                } else {
                    val card = cards.find { it.rank == Rank.WILD_DRAW_4 }
                        ?: wildCard
                        ?: throw IllegalStateException("Must have at least one wild card")
                    card to maxColor
                }
            }
        }
        return StrategyMove(card, color)
    }

    private fun maxColor(cards: List<Card>): CardColor {
        require(cards.isNotEmpty()) { "Cards must not be empty" }
        return cards.groupBy { it.color }
            .mapValues { it.value.size }
            .toList()
            .filter { it.first != null }
            .sortedBy { it.first }
            .maxByOrNull { it.second }
            ?.first
            ?: CardColor.entries.first()
    }
}