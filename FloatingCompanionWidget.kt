package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompanionConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Floating Living Companion Widget displayed at the bottom-right of the screen.
 * Perfectly follows the uploaded motion video reference:
 * - Gentle sinus-wave floating bob
 * - Subtle breathing stretch (1.00x → 1.03x)
 * - Gentle head/body tilt sway (±2.5°)
 * - Timed natural blinking cycle (every 3.8s, 140ms eyelid drop)
 * - Interactive tap reaction (spring hop + squish + speech bubble)
 * - Non-disruptive, stays tucked into the bottom-right corner without blocking navigation
 */
@Composable
fun FloatingCompanionWidget(
    config: CompanionConfig,
    isFocusModeActive: Boolean,
    isStudyTimerRunning: Boolean = false,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!config.isEnabled) return

    val character = config.selectedCharacter
    val primaryAuraColor = Color(character.auraColorHex)
    val accentColor = Color(character.accentColorHex)
    val coroutineScope = rememberCoroutineScope()

    // 1. Natural Infinite Idle Floating & Breathing (Sinusoidal Easing)
    val infiniteTransition = rememberInfiniteTransition(label = "CompanionIdle")

    val floatOffsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatY"
    )

    val breathingScaleY by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = if (isFocusModeActive) 1.015f else 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingY"
    )

    val idleRotationAngle by infiniteTransition.animateFloat(
        initialValue = -2.0f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleTilt"
    )

    val auraPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraPulse"
    )

    // 2. Realistic Natural Blinking Cycle (Every ~3.8 seconds)
    var blinkProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3400L + (Math.random() * 800).toLong())
            // Fast eyelid close
            blinkProgress = 1f
            delay(140L)
            blinkProgress = 0f
            // Occasional double blink (25% chance)
            if (Math.random() < 0.25) {
                delay(120L)
                blinkProgress = 1f
                delay(130L)
                blinkProgress = 0f
            }
        }
    }

    // 3. Interactive Hop & Tap Reaction
    val tapHopY = remember { Animatable(0f) }
    val tapSquishX = remember { Animatable(1f) }
    val tapSquishY = remember { Animatable(1f) }
    var speechBubbleText by remember { mutableStateOf<String?>(null) }
    var speechBubbleVisible by remember { mutableStateOf(false) }

    fun triggerTapReaction() {
        coroutineScope.launch {
            // Speech quote selection
            val quotes = if (isStudyTimerRunning) {
                listOf(
                    "Stay focused, Krish! 🍃",
                    "You've got this! ⏳",
                    "Deep work builds the future.",
                    "Rooted in focus... ✨"
                )
            } else if (isFocusModeActive) {
                listOf(
                    "Focus Mode active 🛡️",
                    "Silence is strength 🍃",
                    "Distractions blocked ✨"
                )
            } else {
                listOf(
                    "Hello, Krish! 🍃",
                    "${config.customName} is ready!",
                    "Let's grow together 🌱",
                    "Ready for study? 📖",
                    "Proud of your progress! 🏆"
                )
            }
            speechBubbleText = quotes.random()
            speechBubbleVisible = true

            // Hop & Squish animation
            launch {
                tapHopY.animateTo(-16f, tween(160, easing = FastOutSlowInEasing))
                tapHopY.animateTo(0f, spring(dampingRatio = 0.45f, stiffness = 600f))
            }
            launch {
                tapSquishX.animateTo(1.15f, tween(120))
                tapSquishX.animateTo(0.92f, tween(120))
                tapSquishX.animateTo(1.0f, spring(dampingRatio = 0.5f))
            }
            launch {
                tapSquishY.animateTo(0.88f, tween(120))
                tapSquishY.animateTo(1.12f, tween(120))
                tapSquishY.animateTo(1.0f, spring(dampingRatio = 0.5f))
            }

            delay(2800L)
            speechBubbleVisible = false
        }
    }

    Box(
        modifier = modifier
            .testTag("floating_companion_widget")
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // Speech Bubble Balloon (Appears gently above character)
            AnimatedVisibility(
                visible = speechBubbleVisible && speechBubbleText != null,
                enter = fadeIn(tween(180)) + scaleIn(tween(180)),
                exit = fadeOut(tween(220)) + scaleOut(tween(220))
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xF20B1320))
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(primaryAuraColor.copy(alpha = 0.8f), accentColor.copy(alpha = 0.8f))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("companion_speech_bubble")
                ) {
                    Text(
                        text = speechBubbleText ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            // Companion Character Core Container
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (floatOffsetY + tapHopY.value).roundToInt()
                        )
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        triggerTapReaction()
                    }
                    .testTag("companion_interactive_character"),
                contentAlignment = Alignment.Center
            ) {
                // Subtle Soft Aura
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    primaryAuraColor.copy(alpha = 0.22f),
                                    accentColor.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // The Authentic Companion PNG Character Artwork
                ForestCompanionArtwork(
                    character = character,
                    size = 82.dp
                )

                // Companion Name Badge on Bottom-Center
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE6080B10))
                        .border(0.8.dp, primaryAuraColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = config.customName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = primaryAuraColor
                    )
                }
            }
        }
    }
}
