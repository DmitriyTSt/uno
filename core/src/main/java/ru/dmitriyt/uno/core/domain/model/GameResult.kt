package ru.dmitriyt.uno.core.domain.model

data class GameResult(
    val winner: String,
    val fails: Map<String, Int>,
)