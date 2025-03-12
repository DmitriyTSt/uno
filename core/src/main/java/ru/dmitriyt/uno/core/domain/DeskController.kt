package ru.dmitriyt.uno.core.domain

import ru.dmitriyt.uno.core.domain.factory.CardFactory
import ru.dmitriyt.uno.core.domain.factory.PlayerFactory
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.model.Player
import ru.dmitriyt.uno.core.domain.model.Rank
import ru.dmitriyt.uno.core.domain.strategy.Move
import ru.dmitriyt.uno.core.domain.strategy.Strategy
import ru.dmitriyt.uno.core.domain.util.isNumeric
import ru.dmitriyt.uno.core.domain.util.isWild
import ru.dmitriyt.uno.core.domain.util.pileTop

private const val START_CARDS_COUNT = 7

fun DeskController(): DeskController {
    return DeskController(
        cardFactory = CardFactory(),
        playerFactory = PlayerFactory(),
        cardComparator = CardComparatorImpl(),
    )
}

class DeskController(
    private val cardFactory: CardFactory,
    private val playerFactory: PlayerFactory,
    private val cardComparator: CardComparator,
) {

    /** Раздача карт и их корректировка */
    fun start(strategies: List<Strategy>): Desk {
        var cards = cardFactory.getCardPool().shuffled()
        val players = strategies.map {
            val playerCards = cards.take(START_CARDS_COUNT)
            cards = cards.drop(START_CARDS_COUNT)
            playerFactory.create(it, playerCards)
        }

        var topCard = cards.first()
        while (topCard.rank == Rank.WILD_DRAW_4 || topCard.rank == Rank.WILD) {
            cards = cards.shuffled()
            topCard = cards.first()
        }
        cards = cards.drop(1)

        val state = if (topCard.rank == Rank.SKIP || topCard.rank == Rank.DRAW_2) {
            Move.Execute
        } else {
            Move.Proceed
        }

        return Desk(
            players = players,
            pile = listOf(topCard),
            deck = cards,
            state = state,
        ).let { desk ->
            if (topCard.rank == Rank.REVERSE) desk.changeDirection() else desk
        }
    }

    /** Передать ход следующему игроку */
    private fun Desk.nextPlayer(): Desk {
        val currentPlayer = players.first()
        return copy(
            players = players.drop(1) + currentPlayer,
        )
    }

    /** Смена направления игры */
    private fun Desk.changeDirection(): Desk {
        return copy(
            players = listOf(players.first()) + players.drop(1).reversed()
        )
    }

    /** Текущий игрок берет 1 карту из колоды "прикуп" */
    private fun Desk.take1(): Desk {
        val (newDeck, newPile) = if (deck.isEmpty()) {
            val topCard = pileTop
            pile.drop(1).shuffled() to listOf(topCard)
        } else {
            deck to pile
        }
        val takenCard = newDeck.first()
        return copy(
            players = players.updateCurrentPlayer { player ->
                player.copy(
                    cards = player.cards + takenCard,
                )
            },
            deck = newDeck.drop(1),
            pile = newPile,
        )
    }

    /** Текущий игрок берет 2 карты из колоды "прикуп" */
    private fun Desk.take2(): Desk {
        return take1().take1()
    }

    /** Текущий игрок берет 4 карты из колоды "прикуп" */
    private fun Desk.take4(): Desk {
        return take2().take2()
    }

    /** Моделирование ситуации, когда у игрока нет возможности сделать ход */
    private fun Desk.pass(): Desk {
        val correctedStateDesk = if (state == Move.Execute) {
            val topCard = pileTop
            if (topCard.rank == Rank.SKIP || topCard.rank == Rank.DRAW_2) {
                copy(
                    state = Move.Proceed
                ).let { newStateDesk ->
                    if (topCard.rank == Rank.DRAW_2) {
                        newStateDesk.take2()
                    } else {
                        newStateDesk
                    }
                }
            } else {
                this
            }
        } else {
            this
        }
        return correctedStateDesk.nextPlayer()
    }

    fun externalGetPlayableCards(desk: Desk): List<Card> {
        return desk.getPlayableCards()
    }

    /**
     * Выдает список тех карт, находящихся на руках у текущего игрока,
     * которыми он мог бы сделать ход в данной конфигурации игры
     */
    private fun Desk.getPlayableCards(): List<Card> {
        val predicate = when (state) {
            Move.Execute -> { card: Card -> cardComparator.isSameRank(card, pileTop) }
            is Move.GiveColor -> { card: Card -> card.color == state.color || card.isWild() }
            Move.Proceed -> { card: Card ->
                cardComparator.isSameRank(card, pileTop) ||
                    cardComparator.isSameColor(card, pileTop) ||
                    card.isWild()
            }
        }
        return players.first().cards.filter(predicate)
    }

    /** Выдает список целых чисел — количество карт каждого игрока в порядке их следования по ходу игры */
    private fun Desk.getPlayersCardCounts(): List<Int> {
        return players.map { it.cards.size }
    }

    /** Моделирование хода текущего игрока */
    private fun Desk.play(playableCards: List<Card>): Desk {
        require(playableCards.isNotEmpty()) { "Playable cards must not be empty" }

        val strategyMove = players.first().strategy.getStrategyMove(state, playableCards, pileTop, getPlayersCardCounts())

        require(playableCards.contains(strategyMove.card)) { "${strategyMove.card} must contain in playable cards" }

        val movedCardDesk = copy(
            pile = listOf(strategyMove.card) + pile,
            players = players.updateCurrentPlayer { player ->
                player.copy(
                    cards = player.cards - strategyMove.card,
                )
            },
        )
        return if (strategyMove.card.rank == Rank.WILD_DRAW_4) {
            val selectedColor = strategyMove.color
                ?: throw IllegalStateException("Must have color if card is wild")

            val isWrongPlay = players.first().cards.find { it.color == pileTop.color } != null

            movedCardDesk.copy(
                state = Move.GiveColor(selectedColor),
            ).let {
                if (isWrongPlay) {
                    it.take4().nextPlayer()
                } else {
                    it.nextPlayer().take4().nextPlayer()
                }
            }
        } else if (strategyMove.card.rank == Rank.WILD) {
            val selectedColor = strategyMove.color
                ?: throw IllegalStateException("Must have color if card is wild")
            movedCardDesk.copy(
                state = Move.GiveColor(selectedColor),
            ).nextPlayer()
        } else if (strategyMove.card.isNumeric()) {
            movedCardDesk.copy(
                state = Move.Proceed,
            ).nextPlayer()
        } else if (strategyMove.card.rank == Rank.REVERSE) {
            movedCardDesk.copy(
                state = Move.Proceed,
            ).changeDirection().nextPlayer()
        } else if (strategyMove.card.rank == Rank.SKIP || strategyMove.card.rank == Rank.DRAW_2) {
            movedCardDesk.copy(
                state = Move.Execute,
            ).nextPlayer()
        } else {
            movedCardDesk.nextPlayer()
        }
    }

    /** Моделирование одного хода игры */
    fun gameStep(desk: Desk): Desk {
        return desk.internalGameStep()
    }

    /** Моделирование одного хода игры */
    private fun Desk.internalGameStep(): Desk {
        val playableCards = getPlayableCards()
        return if (playableCards.isNotEmpty()) {
            play(playableCards)
        } else {
            if (state == Move.Execute) {
                pass()
            } else {
                take1().let { takenCardDesk ->
                    val newPlayableCards = takenCardDesk.getPlayableCards()
                    if (newPlayableCards.isNotEmpty()) {
                        takenCardDesk.play(newPlayableCards)
                    } else {
                        takenCardDesk.pass()
                    }
                }
            }
        }
    }

    /** Обновление текущего игрока */
    private fun List<Player>.updateCurrentPlayer(transformer: (Player) -> Player): List<Player> {
        return mapIndexed { index, player ->
            if (index == 0) {
                transformer(player)
            } else {
                player
            }
        }
    }
}