package ru.dmitriyt.uno.desktop.presentation

import kotlinx.coroutines.delay
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.strategy.Move
import ru.dmitriyt.uno.core.domain.strategy.Strategy
import ru.dmitriyt.uno.core.domain.strategy.StrategyMove

class UserInputStrategy(private val getStrategyMove: suspend () -> StrategyMove?) : Strategy {
    override val name: String = "USER"

    override suspend fun getStrategyMove(
        move: Move,
        cards: List<Card>,
        topCard: Card,
        playersCardCounts: List<Int>
    ): StrategyMove {
        var strategyMove: StrategyMove?
        strategyMove = getStrategyMove()
        while (strategyMove == null) {
            delay(100)
            strategyMove = getStrategyMove()
        }
        return strategyMove
    }
}