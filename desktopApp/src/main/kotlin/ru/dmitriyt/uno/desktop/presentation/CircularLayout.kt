package ru.dmitriyt.uno.desktop.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    startAngle: Float = -180f,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = circularMeasurePolicy(startAngle),
    )
}

private fun circularMeasurePolicy(startAngle: Float): MeasurePolicy = MeasurePolicy { measurables, constraints ->
    // определяем минимальную сторону контейнера (ширину или высоту)
    val containerSize = min(constraints.maxWidth, constraints.maxHeight)

    val centerX = containerSize / 2f
    val centerY = containerSize / 2f

    val placeables = measurables.map { measurable ->
        measurable.measure(Constraints(0, constraints.maxWidth, 0, constraints.maxHeight))
    }
    val maxItemSize = max(placeables.maxOf { it.width }, placeables.maxOf { it.height })
    val radius = containerSize / 2f - maxItemSize / 2f

    layout(containerSize, containerSize) {
        val angleStep = (2 * PI) / placeables.size // угол между элементами в радианах
        placeables.forEachIndexed { index, placeable ->
            val angle = Math.toRadians(startAngle.toDouble()) + angleStep * index
            val x = centerX + radius * cos(angle) - placeable.width / 2
            val y = centerY + radius * sin(angle) - placeable.height / 2

            placeable.place(
                x = x.toInt(),
                y = y.toInt()
            )
        }
    }
}

@Preview
@Composable
fun CircularLayoutPreview() {
    CircularLayout(Modifier.background(Color.Cyan).size(200.dp)) {
        repeat(6) {
            Box(Modifier.background(Color.Red).size(40.dp))
        }
    }
}