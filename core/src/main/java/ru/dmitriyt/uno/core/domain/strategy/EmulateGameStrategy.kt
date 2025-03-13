package ru.dmitriyt.uno.core.domain.strategy

import ru.dmitriyt.uno.core.domain.CardComparator
import ru.dmitriyt.uno.core.domain.CardComparatorImpl
import ru.dmitriyt.uno.core.domain.DeskController
import ru.dmitriyt.uno.core.domain.factory.CardFactory
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.model.Player
import ru.dmitriyt.uno.core.domain.model.Rank
import ru.dmitriyt.uno.core.domain.util.hasNoCards
import ru.dmitriyt.uno.core.domain.util.isNumeric
import ru.dmitriyt.uno.core.domain.util.isPositiveAction
import ru.dmitriyt.uno.core.domain.util.isWild
import ru.dmitriyt.uno.core.domain.util.playableCards

class EmulateGameStrategy(
    private val deskController: DeskController = DeskController(debug = false),
    private val cardComparator: CardComparator = CardComparatorImpl(),
    private val cardFactory: CardFactory = CardFactory(),
) : Strategy {
    private val naiveStrategy = NaiveStrategy(emulateDelay = false)
    override suspend fun getStrategyMove(
        move: Move,
        cards: List<Card>,
        topCard: Card,
        playersCardCounts: List<Int>
    ): StrategyMove {
        val playableCards = cards.playableCards(move, topCard, cardComparator)
        val testMoves = playableCards.flatMap { card ->
            when (card.rank) {
                Rank.WILD,
                Rank.WILD_DRAW_4 -> CardColor.entries.map { StrategyMove(card, it) }
                else -> listOf(StrategyMove(card, null))
            }
        }
        val emulatedGamesCount = 1
        val resultStrategyMove = testMoves.map { testMove ->
            val emulatedGames = List(emulatedGamesCount) {
                emulateGame(cards, move, topCard, playersCardCounts.drop(1), testMove)
            }
            testMove to (emulatedGames.sumOf { it.parrots } / emulatedGamesCount)
        }
            .maxByOrNull { it.second }!!.first
        val naiveStrategyMove = naiveStrategy.getStrategyMove(move, cards, topCard, playersCardCounts)
        if (resultStrategyMove != naiveStrategyMove) {
//            println("$resultStrategyMove vs $naiveStrategyMove")
        }
        return resultStrategyMove
    }

    private suspend fun emulateGame(
        currentCards: List<Card>,
        move: Move,
        topCard: Card,
        playersCardCounts: List<Int>,
        strategyMove: StrategyMove,
    ): EmulatedGameResult {
        var pool = cardFactory.getCardPool().shuffled().toMutableList()
        currentCards.forEach { card ->
            pool.remove(card)
        }
        pool.remove(topCard)
        val players = playersCardCounts.mapIndexed { index, count ->
            val playerRandomCards = pool.take(count)
            pool = pool.drop(count).toMutableList()
            Player(
                name = "Test player $index",
                cards = playerRandomCards,
                strategy = RandomStrategy(emulateDelay = false),
            )
        }
        val currentPlayer = Player(
            name = name,
            cards = currentCards,
            strategy = object : RandomStrategy(emulateDelay = false) {
                private var firstStep = true
                override suspend fun getStrategyMove(
                    move: Move,
                    cards: List<Card>,
                    topCard: Card,
                    playersCardCounts: List<Int>
                ): StrategyMove {
                    return if (firstStep) {
                        strategyMove.apply {
                            firstStep = false
                        }
                    } else {
                        super.getStrategyMove(move, cards, topCard, playersCardCounts)
                    }
                }
            },
        )
        var desk = Desk(
            players = listOf(currentPlayer) + players,
            pile = listOf(topCard),
            deck = pool,
            state = move,
        )
        var winner: Player? = null
        while (winner == null) {
            desk = deskController.gameStep(desk) {}
            winner = desk.players.find { it.hasNoCards() }
        }
        return if (winner.name == name) {
            EmulatedGameResult.Win
        } else {
            EmulatedGameResult.Fail
        }
    }

    sealed interface EmulatedGameResult {
        val parrots: Int

        data object Win : EmulatedGameResult {
            override val parrots = Int.MAX_VALUE
        }

        data object Fail : EmulatedGameResult {
            override val parrots = 0
        }

        data class Step(val desk: Desk, val playerName: String) : EmulatedGameResult {
            override val parrots: Int
                get() {
                    val currentPlayer = desk.players.find { it.name == playerName }!!
                    val currentPlayerWildCards = currentPlayer.cards.filter { it.isWild() }
                    val currentPlayerPositiveActionCards = currentPlayer.cards.filter { it.isPositiveAction() }
                    val currentPlayerNumericAndNormalActionCards =
                        currentPlayer.cards.filter { it.isNumeric() || it.rank == Rank.REVERSE }
                    return Int.MAX_VALUE / 2 +
                        desk.players.filter { it.name != playerName }.sumOf { enemy ->
                            // очки противника и очки игрока
                            // для простых кард - больше - лучше
                            // для wild карт - меньше лучше, с коэф 5
                            // для action карт которые помогают - меньше лучше, с коэф 2

                            (currentPlayerWildCards.sumOf { it.rank.points } - enemy.cards.filter { it.isWild() }
                                .sumOf { it.rank.points }) * 5 +

                                (currentPlayerPositiveActionCards.sumOf { it.rank.points } -
                                    enemy.cards.filter { it.isPositiveAction() }.sumOf { it.rank.points }) * 2 +

                                enemy.cards.filter { it.isNumeric() || it.rank == Rank.REVERSE }
                                    .sumOf { it.rank.points } - currentPlayerNumericAndNormalActionCards.sumOf { it.rank.points }
                        }
                }
        }
    }
}