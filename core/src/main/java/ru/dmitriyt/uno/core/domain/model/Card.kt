package ru.dmitriyt.uno.core.domain.model

data class Card(
    /** Значение карты */
    val rank: Rank,
    /** Цвет. Null, если черный */
    val color: Color?,
)

enum class Rank(val points: Int) {
    NUM_0(0),
    NUM_1(1),
    NUM_2(2),
    NUM_3(3),
    NUM_4(4),
    NUM_5(5),
    NUM_6(6),
    NUM_7(7),
    NUM_8(8),
    NUM_9(9),
    SKIP(20),
    DRAW_2(20),
    REVERSE(20),
    WILD(50),
    WILD_DRAW_4(50),
}

enum class Color {
    Red,
    Green,
    Blue,
    Yellow,
}