package ru.dmitriyt.uno.core.domain.factory

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Color
import ru.dmitriyt.uno.core.domain.model.Rank

class CardFactory {

    /** Получить колоду карт */
    fun getCardPool(): List<Card> {
        return Rank.entries.flatMap { rank ->
            val cardSetCount = when (rank) {
                Rank.WILD -> 4
                Rank.WILD_DRAW_4 -> 4
                Rank.NUM_0 -> 1
                else -> 2
            }
            getAllCards(rank).flatMap { it * cardSetCount }
        }
    }

    /** Доступные цвета для значения карты */
    private fun allRankColors(rank: Rank): List<Color?> {
        val wildColorList = listOf(null)
        val simpleColorList = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
        return when (rank) {
            Rank.WILD -> wildColorList
            Rank.WILD_DRAW_4 -> wildColorList
            else -> simpleColorList
        }
    }

    /** Неповторяющиеся карты с этим значением */
    private fun getAllCards(rank: Rank): Set<Card> {
        return allRankColors(rank).map { Card(rank, it) }.toSet()
    }

    /** Повторяем карты */
    private operator fun Card.times(count: Int): List<Card> {
        return List(count) { this }
    }
}
