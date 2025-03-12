package ru.dmitriyt.uno.core.domain.factory

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Player
import ru.dmitriyt.uno.core.domain.strategy.Strategy

class PlayerFactory {

    fun create(strategy: Strategy, cards: List<Card>): Player {
        return Player(
            name = strategy.name,
            cards = cards,
            strategy = strategy,
        )
    }
}