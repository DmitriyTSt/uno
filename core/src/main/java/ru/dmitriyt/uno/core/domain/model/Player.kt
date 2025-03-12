package ru.dmitriyt.uno.core.domain.model

import ru.dmitriyt.uno.core.domain.strategy.Strategy

data class Player(
    /** Имя */
    val name: String,
    /** Текущие карты */
    val cards: List<Card>,
    /** Стратегия */
    val strategy: Strategy,
)
