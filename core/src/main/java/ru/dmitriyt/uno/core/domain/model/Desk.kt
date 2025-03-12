package ru.dmitriyt.uno.core.domain.model

import ru.dmitriyt.uno.core.domain.strategy.Move

/**
 * Состояние игры
 */
data class Desk(
    /** Список игроков в порядке хода игры */
    val players: List<Player>,
    /** Список карт колоды "сброс" от верхней карты к нижней */
    val pile: List<Card>,
    /** Список карт колоды "прикуп" от верхней карты к нижней */
    val deck: List<Card>,
    /** Состояние текущего хода */
    val state: Move,
)