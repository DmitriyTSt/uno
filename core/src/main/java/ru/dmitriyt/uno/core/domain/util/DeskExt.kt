package ru.dmitriyt.uno.core.domain.util

import ru.dmitriyt.uno.core.domain.model.Card
import ru.dmitriyt.uno.core.domain.model.Color
import ru.dmitriyt.uno.core.domain.model.Desk
import ru.dmitriyt.uno.core.domain.strategy.Move

/** Верхняя карта */
val Desk.pileTop: Card
    get() = pile.first()

/** Необходимый цвет для совершения текущего хода */
val Desk.requiredColor: Color
    get() = if (state is Move.GiveColor) {
        state.color
    } else {
        pileTop.color ?: throw IllegalStateException("Should be not null color by state not GiveColor")
    }