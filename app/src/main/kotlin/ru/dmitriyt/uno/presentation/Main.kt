package ru.dmitriyt.uno.presentation

import ru.dmitriyt.uno.core.domain.DeskController
import ru.dmitriyt.uno.core.domain.GameController
import ru.dmitriyt.uno.core.domain.model.GameResult
import ru.dmitriyt.uno.core.domain.strategy.EmulateGameStrategy
import ru.dmitriyt.uno.core.domain.strategy.NaiveStrategy
import ru.dmitriyt.uno.core.domain.strategy.RandomStrategy
import ru.dmitriyt.uno.core.domain.util.pileTop
import ru.dmitriyt.uno.core.domain.util.requiredColor

suspend fun main() {
    val deskController = DeskController(debug = false)
    val gameController = GameController(deskController)

    val wins = mutableMapOf<String, Int>()
    val fails = mutableMapOf<String, Int>()
    repeat(5000) {
        val gameResult = testGameWith2NaiveStrategies(deskController, gameController, false)
        wins[gameResult.winner] = wins.getOrDefault(gameResult.winner, 0) + 1
        gameResult.fails.forEach { (playerName, points) ->
            fails[playerName] = fails.getOrDefault(playerName, 0) + points
        }
    }

    println("Wins $wins")
    fails.toList().sortedBy { it.second }.forEachIndexed { index, pair ->
        println("${index + 1}: ${pair.first} (${pair.second})")
    }
}

private suspend fun testGameWith2NaiveStrategies(
    deskController: DeskController,
    gameController: GameController,
    debug: Boolean,
): GameResult {
    val naive1 = object : NaiveStrategy(emulateDelay = false) {
        override val name: String
            get() = "Naive1"
    }
    val naive2 = object : NaiveStrategy(emulateDelay = false) {
        override val name: String
            get() = "Naive2"
    }
    val random = RandomStrategy(emulateDelay = false)
    val emulatedGameResult = EmulateGameStrategy()
    val gameResult = gameController.game(listOf(naive1, naive2, random, emulatedGameResult)) { deskResult ->
        val desk = deskResult.getOrThrow()
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