package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = circularMeasurePolicy()
    )
}

private fun circularMeasurePolicy(): MeasurePolicy = MeasurePolicy { measurables, constraints ->
    // Определяем минимальную сторону контейнера (ширину или высоту)
    val containerSize = min(constraints.maxWidth, constraints.maxHeight)
    // Радиус круга будет половиной минимальной стороны, чтобы элементы не выходили за границы
    val radius = containerSize / 2f

    // Центр контейнера
    val centerX = containerSize / 2f
    val centerY = containerSize / 2f

    // Измеряем все дочерние элементы
    val placeables = measurables.map { measurable ->
        measurable.measure(Constraints(0, constraints.maxWidth, 0, constraints.maxHeight))
    }

    // Размещаем элементы
    layout(containerSize, containerSize) {
        val angleStep = (2 * PI) / placeables.size // Угол между элементами в радианах
        placeables.forEachIndexed { index, placeable ->
            val angle = -PI + angleStep * index // Текущий угол
            // Вычисляем координаты для размещения элемента
            val x = centerX + radius * cos(angle) - placeable.width / 2
            val y = centerY + radius * sin(angle) - placeable.height / 2
            // Размещаем элемент
            placeable.place(
                x = x.toInt(),
                y = y.toInt()
            )
        }
    }
}