package ru.dmitriyt.uno.presentation

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.dmitriyt.uno.core.domain.DeskController
import ru.dmitriyt.uno.core.domain.GameController
import ru.dmitriyt.uno.core.domain.model.GameResult
import ru.dmitriyt.uno.core.domain.strategy.NaiveStrategy
import ru.dmitriyt.uno.core.domain.strategy.RandomStrategy
import ru.dmitriyt.uno.core.domain.util.pileTop
import ru.dmitriyt.uno.core.domain.util.requiredColor

suspend fun main() = coroutineScope {
    val deskController = DeskController(debug = false)
    val gameController = GameController(deskController)

    val wins = mutableMapOf<String, Int>()
    val winPoints = mutableMapOf<String, Int>()
    val fails = mutableMapOf<String, Int>()
    val gameResultRequests = List(200_000) {
        async { testGameStrategies(deskController, gameController, false) }
    }
    gameResultRequests.awaitAll().forEach { gameResult ->
        wins[gameResult.winner] = wins.getOrDefault(gameResult.winner, 0) + 1
        winPoints[gameResult.winner] = winPoints.getOrDefault(gameResult.winner, 0) +
            gameResult.fails.toList().sumOf { it.second }
        gameResult.fails.forEach { (playerName, points) ->
            fails[playerName] = fails.getOrDefault(playerName, 0) + points
        }
    }

    println("--- Wins by win count -- (desc)")
    wins.toList().sortedByDescending { it.second }.forEachIndexed { index, pair ->
        println("${index + 1}: ${pair.first} (${pair.second})")
    }
    println("-- Wins by points --- (desc)")
    winPoints.toList().sortedByDescending { it.second }.forEachIndexed { index, pair ->
        println("${index + 1}: ${pair.first} (${pair.second})")
    }
    println("-- Fail points --- (asc)")
    fails.toList().sortedBy { it.second }.forEachIndexed { index, pair ->
        println("${index + 1}: ${pair.first} (${pair.second})")
    }
}

private suspend fun testGameStrategies(
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
    val strategies = listOf(
        naive1,
        naive2,
//        random,
//        NaivePlusStrategy(emulateDelay = false),
    )
    val gameResult = gameController.game(strategies.shuffled()) { deskResult ->
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