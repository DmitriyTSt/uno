package ru.dmitriyt.uno.core.domain.util

import ru.dmitriyt.uno.core.domain.model.Player

/** Нет ли у игрока карт */
fun Player.hasNoCards(): Boolean {
    return cards.isEmpty()
}
