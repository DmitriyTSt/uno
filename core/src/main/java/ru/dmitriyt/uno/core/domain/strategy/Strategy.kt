package ru.dmitriyt.uno.core.domain.strategy

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor

/**
 * Стратегия
 */
interface Strategy {

    /** Название стратегии */
    val name: String
        get() = this::class.java.simpleName

    /**
     * Ход стратегии
     * Предполагается, что метод запускается, если среди карт игрока есть карта, которой можно сделать ход.
     * Метод должен выбирать карту для хода, а если выбирается Wild карта, то еще и задавать нужный цвет
     */
    suspend fun getStrategyMove(
        /** Состояние хода */
        move: Move,
        /** Ваши карты */
        cards: List<Card>,
        /** Верхняя карта на столе */
        topCard: Card,
        /** Количество карт игроков в порядке их хождения */
        playersCardCounts: List<Int>,
    ): StrategyMove
}

data class StrategyMove(
    /** Карта, которую кладете */
    val card: Card,
    /** Заданный цвет, если положили Wild карту */
    val color: CardColor?,
)

/**
 * Состояние хода
 */
sealed interface Move {

    /** Обычный ход, просто положить карту  */
    data object Proceed : Move

    /**
     * Ход с активной картой Skip или Draw2.
     * Необходимо положить такую же имеющуюся карту. Если у вас их нет - ход к вам не перейдет
     */
    data object Execute : Move

    /** Карта задает новый цвет */
    data class GiveColor(val color: CardColor) : Move
}