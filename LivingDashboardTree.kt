package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Highly realistic, detailed, living mystical tree component for the Dashboard.
 * Accurately reflects the bioluminescent green/red split bonsai reference art:
 * - Foliage & branches softly swaying in the breeze
 * - Tree transitioning between vibrant emerald green and deep crimson red/orange tones
 * - Soft golden aura framing all four sides of the tree with radiant orbital halo
 * - Flowing green (left) and red (right) energy particles drawn inward toward the tree core
 * - Natural leaves gently swaying and fluttering down to the reflective ground
 * - Subtle glowing light movement pulsing through the trunk and branching canopy
 *
 * Optimized for zero per-frame Shader/Brush JNI allocations and zero composition-scope state reads.
 */
@Composable
fun LivingDashboardTree(
    modifier: Modifier = Modifier,
    isPaused: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Lightweight continuous loop (0f -> 1f -> 0f -> 1f indefinitely)
    // Read exclusively inside graphicsLayer and Canvas draw scopes so zero recompositions occur.
    val masterLoopState = remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

    LaunchedEffect(isPaused) {
        val cycleDurationMs = 4200L
        val frameStepMs = 50L // Smooth continuous loop with zero UI touch contention
        while (!isPaused) {
            kotlinx.coroutines.delay(frameStepMs)
            val next = masterLoopState.floatValue + (frameStepMs.toFloat() / cycleDurationMs.toFloat())
            masterLoopState.floatValue = if (next >= 1f) next - 1f else next
        }
    }

    // Pre-calculated particle metadata (deterministic to eliminate GC pressure)
    val particles = remember {
        List(18) { index ->
            val isLeftSide = index < 9
            LivingParticleData(
                id = index,
                isLeft = isLeftSide,
                initialAngle = (index * (360f / 18f)),
                radiusFactor = 0.65f + (index % 5) * 0.08f,
                speedMultiplier = 1.0f,
                phaseOffset = (index * (1f / 18f)),
                baseSize = 2.4f + (index % 3) * 1.0f
            )
        }
    }

    val fallingLeaves = remember {
        List(8) { index ->
            FallingLeafData(
                id = index,
                isGreen = index % 2 == 0,
                spawnXFactor = if (index % 2 == 0) 0.22f + (index * 0.05f) else 0.58f + ((index - 1) * 0.05f),
                spawnYFactor = 0.20f + (index % 4) * 0.07f,
                driftAmplitude = 10f + (index % 3) * 5f,
                phaseOffset = (index * 0.125f),
                scale = 0.7f + (index % 4) * 0.15f
            )
        }
    }

    val reusableLeafPath = remember { Path() }

    Box(
        modifier = modifier
            .testTag("living_dashboard_tree")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Highly Detailed Realistic Tree Base Image with Soft Foliage Sway
        // Clean background with NO artificial brown circular patches or halos behind the tree
        Image(
            painter = painterResource(id = R.drawable.img_living_tree),
            contentDescription = "Bioluminescent Living Study Tree",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val loop = masterLoopState.floatValue
                    val sway = if (isPaused) 0f else sin(loop * 2f * PI).toFloat() * 0.9f
                    val breath = if (isPaused) 1f else 1f + sin(loop * 2f * PI).toFloat() * 0.008f
                    transformOrigin = TransformOrigin(0.5f, 0.94f)
                    rotationZ = sway
                    scaleX = breath
                    scaleY = breath
                },
            contentScale = ContentScale.Fit
        )

        // LAYER 2: Inward Energy Flow, Subtle Branch Light Nodes & Falling Leaves
        if (!isPaused) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val masterLoopProgress = masterLoopState.floatValue
                val cx = size.width * 0.5f
                val focalTarget = Offset(cx, size.height * 0.52f)

                val energyFlowProgress = (masterLoopProgress * 2.0f) % 1f
                val branchLightFlow = (masterLoopProgress * 2.0f) % 1f
                val leafFallProgress = masterLoopProgress

                // Subtle pinpoint light nodes in branches (no circular/oval background wash)
                val waveAlpha = sin(branchLightFlow * PI).toFloat()
                if (waveAlpha > 0.02f) {
                    val nodeAlpha1 = (sin((branchLightFlow * 3f + 1f) * PI).coerceIn(0.0, 1.0) * waveAlpha).toFloat()
                    val nodeAlpha2 = (cos((branchLightFlow * 3f + 2f) * PI).coerceIn(0.0, 1.0) * waveAlpha).toFloat()

                    drawCircle(
                        color = Color.White,
                        alpha = 0.65f * nodeAlpha1,
                        radius = 2.5f,
                        center = Offset(cx - size.width * 0.16f, size.height * 0.40f)
                    )
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        alpha = 0.60f * nodeAlpha2,
                        radius = 2.5f,
                        center = Offset(cx + size.width * 0.18f, size.height * 0.36f)
                    )
                }

                // C. Flowing Green and Red Energy Particles Being Drawn TOWARD the Tree
                particles.forEach { p ->
                    val progress = (energyFlowProgress * p.speedMultiplier + p.phaseOffset) % 1f
                    val inwardFactor = progress * progress

                    val angleRad = p.initialAngle * (PI / 180f)
                    val rx = size.width * 0.44f * p.radiusFactor
                    val ry = size.height * 0.42f * p.radiusFactor
                    val startX = if (p.isLeft) {
                        cx - kotlin.math.abs(cos(angleRad).toFloat()) * rx - 10f
                    } else {
                        cx + kotlin.math.abs(cos(angleRad).toFloat()) * rx + 10f
                    }
                    val startY = size.height * 0.50f + sin(angleRad).toFloat() * ry

                    val currentX = startX + (focalTarget.x - startX) * inwardFactor
                    val currentY = startY + (focalTarget.y - startY) * inwardFactor

                    val particleColor = if (p.isLeft) {
                        Color(0xFF00E676)
                    } else {
                        Color(0xFFFF2A4B)
                    }

                    val alpha = when {
                        progress < 0.2f -> progress / 0.2f
                        progress > 0.85f -> (1f - progress) / 0.15f
                        else -> 1f
                    } * 0.85f

                    val tailLength = 6f + inwardFactor * 8f
                    val dirX = (focalTarget.x - currentX)
                    val dirY = (focalTarget.y - currentY)
                    val len = kotlin.math.sqrt(dirX * dirX + dirY * dirY).coerceAtLeast(0.01f)
                    val normX = (dirX / len) * tailLength
                    val normY = (dirY / len) * tailLength

                    drawLine(
                        color = particleColor,
                        alpha = alpha * 0.5f,
                        start = Offset(currentX - normX, currentY - normY),
                        end = Offset(currentX, currentY),
                        strokeWidth = p.baseSize * 0.7f,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = Color.White,
                        alpha = alpha,
                        radius = p.baseSize * 0.6f,
                        center = Offset(currentX, currentY)
                    )
                    drawCircle(
                        color = particleColor,
                        alpha = alpha * 0.7f,
                        radius = p.baseSize,
                        center = Offset(currentX, currentY)
                    )
                }

                // D. Leaves Gently Moving and Occasionally Falling
                fallingLeaves.forEach { leaf ->
                    val progress = (leafFallProgress + leaf.phaseOffset) % 1f

                    val startY = size.height * leaf.spawnYFactor
                    val endY = size.height * 0.90f
                    val currentY = startY + (endY - startY) * progress

                    val sway = sin(progress * 4 * PI + leaf.id).toFloat() * leaf.driftAmplitude
                    val currentX = (size.width * leaf.spawnXFactor + sway).coerceIn(10f, size.width - 10f)
                    val rotationAngle = (progress * 360f * if (leaf.id % 2 == 0) 1f else -1f) + (leaf.id * 35f)

                    val alpha = when {
                        progress < 0.15f -> progress / 0.15f
                        progress > 0.80f -> (1f - progress) / 0.20f
                        else -> 1f
                    } * 0.80f

                    val leafColor = if (leaf.isGreen) {
                        Color(0xFF00E676)
                    } else {
                        Color(0xFFFF3D00)
                    }

                    drawLeaf(
                        path = reusableLeafPath,
                        center = Offset(currentX, currentY),
                        color = leafColor,
                        alpha = alpha,
                        scale = leaf.scale,
                        rotationDegrees = rotationAngle
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawLeaf(
    path: Path,
    center: Offset,
    color: Color,
    alpha: Float,
    scale: Float,
    rotationDegrees: Float
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        val leafWidth = 5.5f * scale
        val leafHeight = 11.5f * scale

        path.reset()
        path.moveTo(center.x, center.y - leafHeight)
        path.cubicTo(
            center.x + leafWidth, center.y - leafHeight * 0.3f,
            center.x + leafWidth, center.y + leafHeight * 0.3f,
            center.x, center.y + leafHeight
        )
        path.cubicTo(
            center.x - leafWidth, center.y + leafHeight * 0.3f,
            center.x - leafWidth, center.y - leafHeight * 0.3f,
            center.x, center.y - leafHeight
        )
        path.close()

        drawPath(path = path, color = color, alpha = alpha)

        drawLine(
            color = Color.White,
            alpha = alpha * 0.7f,
            start = Offset(center.x, center.y - leafHeight * 0.8f),
            end = Offset(center.x, center.y + leafHeight * 0.8f),
            strokeWidth = 1f * scale,
            cap = StrokeCap.Round
        )
    }
}

private fun lerpColor(start: Color, stop: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (stop.red - start.red) * f,
        green = start.green + (stop.green - start.green) * f,
        blue = start.blue + (stop.blue - start.blue) * f,
        alpha = start.alpha + (stop.alpha - start.alpha) * f
    )
}

private data class LivingParticleData(
    val id: Int,
    val isLeft: Boolean,
    val initialAngle: Float,
    val radiusFactor: Float,
    val speedMultiplier: Float,
    val phaseOffset: Float,
    val baseSize: Float
)

private data class FallingLeafData(
    val id: Int,
    val isGreen: Boolean,
    val spawnXFactor: Float,
    val spawnYFactor: Float,
    val driftAmplitude: Float,
    val phaseOffset: Float,
    val scale: Float
)
