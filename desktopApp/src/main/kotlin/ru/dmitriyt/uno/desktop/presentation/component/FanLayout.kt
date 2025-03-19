package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param angleStep шаг угла между элементами в градусах
 */
@Composable
fun FanLayout(
    modifier: Modifier = Modifier,
    angleStep: Float = 10f,
    content: @Composable () -> Unit,
) {
    val fanMeasurePolicy = remember(angleStep) { fanMeasurePolicy(angleStep) }
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = fanMeasurePolicy,
    )
}

private fun fanMeasurePolicy(angleStep: Float): MeasurePolicy = MeasurePolicy { measurables, constraints ->
    if (measurables.isEmpty()) {
        return@MeasurePolicy layout(0, 0) {}
    }
    val placeables = measurables.map { measurable ->
        measurable.measure(constraints)
    }

    // определяем максимальную ширину и высоту среди всех элементов
    val maxItemWidth = placeables.maxOfOrNull { it.width }!!
    val maxItemHeight = placeables.maxOfOrNull { it.height }!!

    // начальный угол поворота
    val startAngle = -angleStep * (measurables.size - 1) / 2f

    // угол для текущего элемента
    val angles = List(placeables.size) { index -> startAngle + index * angleStep }
    val angleRads = angles.map { Math.toRadians(it.toDouble()) }

    val xOffsets = List(placeables.size) { index -> (maxItemWidth / 2) * sin(angleRads[index]).toFloat() }
    val yOffsets = List(placeables.size) { index -> (maxItemHeight / 2) * (1 - cos(angleRads[index])).toFloat() }

    // размеры контейнера
    // смотрим половину реальной ширины которую займет повернутый элемент + его отступ от центра
    val width = placeables.withIndex().maxOfOrNull { (index, placeable) ->
        val realWidth = sin(angleRads[index]) * placeable.height + cos(angleRads[index]) * placeable.width
        val halfRealWidth = realWidth / 2
        val fullHalfContainerWidth = halfRealWidth + abs(xOffsets[index])
        (fullHalfContainerWidth * 2).toInt()
    }!!
    // реальные высоты уже повернутых элементов
    val realHeights = placeables.mapIndexed { index, placeable ->
        abs(cos(angleRads[index])) * placeable.height + abs(sin(angleRads[index])) * placeable.width
    }
    // центр - центр элемента который не повернут (или среднее если два в центре)
    // максимальный размер над центром с учетом высоты повернутого элемента
    val maxHeightTopPart = realHeights.withIndex().maxOfOrNull { (index, realHeight) ->
        val halfRealHeight = realHeight / 2
        (halfRealHeight - yOffsets[index]).toInt()
    }!!
    // максимальный размер под центром с учетом высоты повернутого элемента
    val maxHeightBottomPart = realHeights.withIndex().maxOfOrNull { (index, realHeight) ->
        val halfRealHeight = realHeight / 2
        (halfRealHeight + yOffsets[index]).toInt()
    }!!
    val height = maxHeightTopPart + maxHeightBottomPart

    layout(width, height) {
        placeables.forEachIndexed { index, placeable ->
            val angle = angles[index]
            // Смещение по X и Y для создания дуги
            val xOffset = xOffsets[index]
            val yOffset = yOffsets[index]

            // Позиция элемента
            val x = (width / 2) + xOffset - (placeable.width / 2)
            val y = maxHeightTopPart + yOffset - (placeable.height / 2)

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
}

@Preview
@Composable
fun FanLayoutPreview() {
    FanLayout(Modifier.background(Color.Cyan)) {
        repeat(28) {
            Box(
                Modifier
                    .size(width = 90.dp, height = 130.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 4.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color.Black).align(Alignment.Center))
                Box(Modifier.fillMaxHeight().width(1.dp).background(Color.Black).align(Alignment.Center))
            }
        }
    }
}