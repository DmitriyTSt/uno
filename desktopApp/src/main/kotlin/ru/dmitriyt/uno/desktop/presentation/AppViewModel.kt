package ru.dmitriyt.uno.desktop.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.dmitriyt.uno.core.domain.DeskController
import ru.dmitriyt.uno.core.domain.GameController
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.CardColor
import ru.dmitriyt.uno.core.domain.strategy.Move
import ru.dmitriyt.uno.core.domain.strategy.NaiveStrategy
import ru.dmitriyt.uno.core.domain.strategy.StrategyMove
import ru.dmitriyt.uno.desktop.domain.strategy.UserInputStrategy
import ru.dmitriyt.uno.desktop.presentation.model.UiUnoState

class AppViewModel(
    private val deskController: DeskController = DeskController(debug = false),
    private val gameController: GameController = GameController(deskController),
) {
    private val viewModelScope = CoroutineScope(Job() + Dispatchers.Main.immediate)

    private val mutableGameState = MutableStateFlow(UiUnoState())
    val gameState = mutableGameState.asStateFlow().filterNotNull()

    @Volatile
    private var selectedCard: StrategyMove? = null

    private val selectCardMutex = Mutex()

    init {
        startGame()
    }

    fun startGame() {
        val strategies = listOf(1, 2).map { "Naive${it}" }.map {
            object : NaiveStrategy(emulateDelay = true) {
                override val name: String
                    get() = it
            }
        }
        val userInputStrategy = UserInputStrategy {
            selectCardMutex.withLock {
                selectedCard.apply {
                    selectedCard = null
                }
            }
        }

        viewModelScope.launch {
            val gameResult = gameController.game(strategies + userInputStrategy) { deskResult ->
                deskResult.onSuccess { desk ->
                    viewModelScope.launch {
                        val state = UiUnoState(
                            desk = desk.copy(
                                players = desk.players.sortedBy { it.name },
                            ),
                            playableCards = deskController.externalGetPlayableCards(
                                desk = desk,
                                player = desk.players.find { it.name == "USER" }!!,
                            ),
                            selectedPlayer = desk.players.first(),
                            requiredColor = (desk.state as? Move.GiveColor)?.color,
                        )
                        mutableGameState.value = state
                    }
                }.onFailure { error ->
                    mutableGameState.update { it.copy(error = error.message) }
                }
            }
            mutableGameState.update { it.copy(winner = gameResult.winner) }
        }
    }

    fun selectCard(card: Card, color: CardColor?) {
        viewModelScope.launch {
            selectCardMutex.withLock {
                selectedCard = StrategyMove(card, color)
            }
        }
    }
}