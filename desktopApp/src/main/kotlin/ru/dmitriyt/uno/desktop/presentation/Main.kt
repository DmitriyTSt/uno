package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(1024.dp, 768.dp)
    )
    Window(title = "Uno", state = state, onCloseRequest = ::exitApplication) {
        App()
    }
}