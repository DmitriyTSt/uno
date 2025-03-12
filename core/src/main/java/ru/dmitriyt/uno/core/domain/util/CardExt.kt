package ru.dmitriyt.uno.core.domain.util

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Rank

fun Card.isWild(): Boolean {
    return this.rank == Rank.WILD || this.rank == Rank.WILD_DRAW_4
}

fun Card.isNumeric(): Boolean {
    return this.rank.points in 0..9
}
