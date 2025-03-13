package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param angleStep шаг угла между элементами
 */
@Composable
fun FanLayout(
    modifier: Modifier = Modifier,
    angleStep: Float = 10f,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        // Измеряем каждый дочерний элемент
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // Определяем максимальную ширину и высоту среди всех элементов
        val maxItemWidth = placeables.maxOfOrNull { it.width }
        val maxItemHeight = placeables.maxOfOrNull { it.height }

        if (maxItemWidth != null && maxItemHeight != null) {
            // Угол поворота для каждого элемента
            val startAngle = -angleStep * (measurables.size - 1) / 2f // Начальный угол

            // Размеры контейнера
            val width = (maxItemWidth * 1.75f).toInt()
            val height = (maxItemHeight * 1.2f).toInt()

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    // Угол для текущего элемента
                    val angle = startAngle + index * angleStep

                    // Преобразуем угол в радианы
                    val radians = Math.toRadians(angle.toDouble())

                    // Смещение по X и Y для создания дуги
                    val xOffset = (maxItemWidth / 2) * sin(radians).toFloat()
                    val yOffset = (maxItemHeight / 2) * (1 - cos(radians)).toFloat()

                    // Позиция элемента
                    val x = (width / 2) + xOffset - (placeable.width / 2)
                    val y = (height / 2) + yOffset - (placeable.height / 2)

                    // Поворачиваем и размещаем элемент
                    placeable.placeWithLayer(
                        x = x.toInt(),
                        y = y.toInt(),
                        zIndex = index.toFloat(),
                        layerBlock = {
                            rotationZ = angle
                            transformOrigin = TransformOrigin(0.5f, 0.5f)
                        }
                    )
                }
            }
        } else {
            layout(0, 0) {}
        }
    }
}