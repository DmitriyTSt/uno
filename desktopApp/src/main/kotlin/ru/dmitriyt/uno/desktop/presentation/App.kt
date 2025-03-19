package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.util.isWild
import ru.dmitriyt.uno.desktop.presentation.component.ColorSelectorDialog
import ru.dmitriyt.uno.desktop.presentation.component.Desk
import ru.dmitriyt.uno.desktop.presentation.model.UiUnoState

private val viewModel = AppViewModel()

@Composable
fun App() {
    val state by viewModel.gameState.collectAsState(UiUnoState())

    var colorPickerDialogCard by remember { mutableStateOf<Card?>(null) }

    Box(Modifier.fillMaxSize()) {
        val desk = state.desk
        if (desk != null && state.winner == null) {
            Desk(
                desk = desk,
                state = state,
                onPlayerCardClick = { card ->
                    if (card.isWild()) {
                        colorPickerDialogCard = card
                    } else {
                        viewModel.selectCard(card, null)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else if (state.winner != null) {
            Column(Modifier.align(Alignment.Center)) {
                Text(
                    text = "WINNER: ${state.winner}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.h4,
                )
                Button(onClick = { viewModel.startGame() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("New game")
                }
            }
        }
    }

    if (state.error != null) {
        Dialog(onDismissRequest = {
            viewModel.hideError()
        }) {
            Column(Modifier.background(Color.White)) {
                Text(
                    text = "ERROR",
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = state.error.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }

    if (colorPickerDialogCard != null) {
        ColorSelectorDialog(
            onDismissRequest = { colorPickerDialogCard = null },
            onColorSelected = { cardColor ->
                viewModel.selectCard(colorPickerDialogCard!!, cardColor)
                colorPickerDialogCard = null
            }
        )
    }
}
