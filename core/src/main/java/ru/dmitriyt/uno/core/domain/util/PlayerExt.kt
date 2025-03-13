package ru.dmitriyt.uno.core.domain.util

import ru.dmitriyt.uno.core.domain.model.Player

/** Нет ли у игрока карт */
fun Player.hasNoCards(): Boolean {
    return cards.isEmpty()
}

/** Сумма очков по картам каждого игрока. Возвращает Map<Имя игрока, количество очков> */
fun List<Player>.countLoss(): Map<String, Int> {
    return associate { player -> player.name to player.cards.sumOf { it.rank.points } }
}