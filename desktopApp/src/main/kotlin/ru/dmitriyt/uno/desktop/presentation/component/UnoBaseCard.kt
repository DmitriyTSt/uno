package ru.dmitriyt.uno.desktop.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun UnoBaseCard(
    color: Color,
    size: DpSize,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isHoverSelected = isHovered && onClick != null && enabled
    val scaleAnimated by animateFloatAsState(if (isHoverSelected) 1.1f else 1f)
    val yOffset = with(LocalDensity.current) { (if (isHoverSelected) 16.dp else 0.dp).toPx() }.toInt()
    val yOffsetAnimated by animateIntAsState(yOffset)
    Box(
        modifier
            .width(size.width)
            .height(size.height)
            .scale(scaleAnimated)
            .offset { IntOffset(0, -yOffsetAnimated) }
            .clip(shape)
            .background(
                color = color,
                shape = shape,
            )
            .border(
                width = 4.dp,
                color = Color.White,
                shape = shape,
            )
            .hoverable(interactionSource)
            .let { if (onClick != null && enabled) it.clickable { onClick() } else it },
    ) {
        Box(Modifier.fillMaxSize().padding(vertical = 4.dp, horizontal = 8.dp)) {
            content()
        }
        if (!enabled) {
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f)))
        }
    }
}