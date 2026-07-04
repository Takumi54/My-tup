package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

// Custom shape for customizable Friend Bubble - Hexagon
val HexagonShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val radius = width / 2f
    val cx = width / 2f
    val cy = height / 2f
    moveTo(cx + radius * cos(0f), cy + radius * sin(0f))
    for (i in 1..5) {
        val angle = i * Math.PI / 3f
        lineTo(cx + radius * cos(angle).toFloat(), cy + radius * sin(angle).toFloat())
    }
    close()
}

// Custom shape for Friend Bubble - Message Bubble
val ChatBubbleShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val r = 16f
    moveTo(r, 0f)
    lineTo(w - r, 0f)
    quadraticTo(w, 0f, w, r)
    lineTo(w, h - r * 1.5f)
    quadraticTo(w, h - r, w - r, h - r)
    // The little bubble pointer
    lineTo(w / 2f + 12f, h - r)
    lineTo(w / 2f, h)
    lineTo(w / 2f - 12f, h - r)
    lineTo(r, h - r)
    quadraticTo(0f, h - r, 0f, h - r * 1.5f)
    lineTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)
    close()
}

// Helper Modifier for creating a pulsing dynamic glow frame
fun Modifier.neonGlow(
    color: Color,
    radius: Float = 30f,
    isPlaying: Boolean = true,
    speedMultiplier: Float = 1f
): Modifier = this.drawBehind {
    val alphaFactor = if (isPlaying) 0.5f else 0.2f
    // Draw outer shadow layer
    drawCircle(
        color = color.copy(alpha = alphaFactor),
        radius = size.minDimension / 2f + radius,
        center = center,
        style = Stroke(width = 4f)
    )
}

// Dynamic Neon Box for the Player Frame
@Composable
fun GlowCard(
    glowColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    borderWidth: Float = 2f,
    isPlaying: Boolean = true,
    moodPace: String = "peaceful",
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowCardTransition")
    
    // Animate glow radius and intensity based on video playing state and mood pace
    val duration = if (moodPace == "action") 1200 else 2400
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val animatedColor = glowColor.copy(
        alpha = if (isPlaying) 0.8f else 0.4f
    )

    Box(
        modifier = modifier
            .drawBehind {
                // Drawing outer atmospheric shadow
                drawRoundRect(
                    color = animatedColor.copy(alpha = 0.22f),
                    size = Size(size.width + glowPulse * 2, size.height + glowPulse * 2),
                    topLeft = Offset(-glowPulse, -glowPulse),
                    cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx())
                )
                // Crisp glowing border stroke
                drawRoundRect(
                    color = animatedColor,
                    size = size,
                    cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx()),
                    style = Stroke(width = borderWidth)
                )
            }
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
        content = content
    )
}

// Organic Spring-Loaded Click Feedback Button
@Composable
fun OrganicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = Color(0xFFD0BCFF),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "SpringScale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 1.5.dp,
                color = if (enabled) glowColor.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            )
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        glowColor.copy(alpha = 0.15f)
                    )
                )
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Custom visual physics are handled by scale
            ),
        color = Color.Transparent,
        contentColor = LocalContentColor.current
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}
