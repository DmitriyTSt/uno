package ru.dmitriyt.uno.core.domain

import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.model.GameResult
import ru.dmitriyt.uno.core.domain.model.Player
import ru.dmitriyt.uno.core.domain.strategy.Strategy
import ru.dmitriyt.uno.core.domain.util.hasNoCards

class GameController(
    private val deskController: DeskController,
) {

    /** Игровой процесс */
    suspend fun game(strategies: List<Strategy>, onStep: (Result<Desk>) -> Unit = {}): GameResult {
        var currentDesk = deskController.start(strategies)
        onStep(Result.success(currentDesk))
        var winner: Player?
        val onInternalStep = { desk: Desk ->
            onStep(Result.success(desk))
        }
        do {
            var currentStep = kotlin.runCatching { deskController.gameStep(currentDesk, onInternalStep) }
            onStep(currentStep)
            while (currentStep.isFailure) {
                currentStep = kotlin.runCatching { deskController.gameStep(currentDesk, onInternalStep) }
                onStep(currentStep)
            }
            currentDesk = currentStep.getOrThrow()
            winner = currentDesk.players.find { it.hasNoCards() }
        } while (winner == null)
        return GameResult(
            winner = winner.name,
            fails = countLoss(currentDesk.players - winner),
        )
    }

    /** Сумма очков по картам каждого игрока. Возвращает Map<Имя игрока, количество очков> */
    private fun countLoss(players: List<Player>): Map<String, Int> {
        return players.associate { player -> player.name to player.cards.sumOf { it.rank.points } }
    }
}