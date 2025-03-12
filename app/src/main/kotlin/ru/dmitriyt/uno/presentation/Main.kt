package ru.dmitriyt.uno.presentation

import ru.dmitriyt.uno.core.domain.CardComparator
import ru.dmitriyt.uno.core.domain.CardComparatorImpl
import ru.dmitriyt.uno.core.domain.DeskController
import ru.dmitriyt.uno.core.domain.GameController
import ru.dmitriyt.uno.core.domain.model.GameResult
import ru.dmitriyt.uno.core.domain.strategy.NaiveStrategy
import ru.dmitriyt.uno.core.domain.util.pileTop
import ru.dmitriyt.uno.core.domain.util.requiredColor

fun main() {
    val cardComparator: CardComparator = CardComparatorImpl()
    val deskController = DeskController()
    val gameController = GameController(deskController)

    val wins = mutableMapOf<String, Int>()
    repeat(50000) {
        val gameResult = testGameWith2NaiveStrategies(cardComparator, deskController, gameController, false)
        wins[gameResult.winner] = wins.getOrDefault(gameResult.winner, 0) + 1
    }

    println(wins)
}

private fun testGameWith2NaiveStrategies(
    cardComparator: CardComparator,
    deskController: DeskController,
    gameController: GameController,
    debug: Boolean,
): GameResult {
    val naive1 = object : NaiveStrategy(cardComparator) {
        override val name: String
            get() = "Naive1"
    }
    val naive2 = object : NaiveStrategy(cardComparator) {
        override val name: String
            get() = "Naive2"
    }
    val gameResult = gameController.game(listOf(naive1, naive2)) { desk ->
        if (debug) {
            println("Top: ${desk.pileTop}, required: ${desk.requiredColor}")
            println("Playable cards: ${deskController.externalGetPlayableCards(desk)}")
            desk.players.forEach {
                println("${it.name} : ${it.cards}")
            }
        }
    }
    return gameResult
}