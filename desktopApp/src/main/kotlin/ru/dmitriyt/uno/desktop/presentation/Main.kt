package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(title = "Uno", onCloseRequest = ::exitApplication) {
        App()
    }
}