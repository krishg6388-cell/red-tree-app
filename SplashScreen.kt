package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlowButton
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.OrangeYellowGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Deterministic background & orbital light particle for the R.E.D TREE intro.
 * Animates exclusively in the environment around the untouched official logo.
 */
private data class IntroLightMote(
    val startAngleRad: Float,
    val angularSweepRad: Float,
    val baseOrbitFactor: Float,
    val radialDeltaFactor: Float,
    val verticalRiseDp: Float,
    val color: Color,
    val radiusDp: Float,
    val startFraction: Float,
    val endFraction: Float,
    val holdsAtCompletion: Boolean = false
)

/**
 * Deterministic radial energy filament that converges from the dark background
 * toward the synchronized ring radius before and during the logo reveal.
 */
private data class ConvergingEnergyRay(
    val angleRad: Float,
    val outerFactor: Float,
    val innerFactor: Float,
    val color: Color,
    val startFraction: Float,
    val endFraction: Float
)

private val SynchronizedRevealEasing = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)

@Composable
fun SplashScreen(
    onEnterApp: () -> Unit = {}
) {
    // Master timeline progress (0f -> 1f over 5500ms = 5.5 seconds, then HOLDS at 1f waiting for user click):
    // • 0.0s – 1.9s (p: 0.00 – 0.34): Dark background -> subtle ambient light -> dense, controlled background particles & converging energy build naturally
    // • 1.9s – 3.8s (p: 0.34 – 0.69): Synchronized Official RED TREE Logo reveal + concentric perimeter ring formation (unified easing curve & exact shared center)
    // • 3.8s – 5.0s (p: 0.69 – 0.91): Horizon lock-in wave + smooth reveal of R.E.D TREE identity & "Remove • Education • Distraction"
    // • 5.0s – 5.5s (p: 0.91 – 1.00): Animation completes -> HOLDS on screen with "Enter R.E.D Tree →" button until manual user tap
    val animProgress = remember { Animatable(0f) }
    var hasNavigated by remember { mutableStateOf(false) }

    val triggerEnterApp = remember(onEnterApp) {
        {
            if (!hasNavigated) {
                hasNavigated = true
                onEnterApp()
            }
        }
    }

    // 36 Dense, controlled background light particles building from 0.0s across the dark field
    val backgroundFieldMotes = remember {
        val ambientPalette = listOf(
            Color(0xFF00E676), // R.E.D Tree Emerald
            Color(0xFFFFD54F), // Sovereign Gold
            Color(0xFFFF1744), // R.E.D Tree Crimson
            Color(0xFF69F0AE)  // Luminous Mint
        )
        List(36) { i ->
            val angle = (i * (360f / 36f) + (i % 3) * 7f) * (PI.toFloat() / 180f)
            val ringLayer = i % 4
            IntroLightMote(
                startAngleRad = angle,
                angularSweepRad = if (i % 2 == 0) 0.42f else -0.42f,
                baseOrbitFactor = 0.68f + ringLayer * 0.14f,
                radialDeltaFactor = -0.26f - ringLayer * 0.05f,
                verticalRiseDp = -14f - (i % 5) * 4f,
                color = ambientPalette[i % ambientPalette.size],
                radiusDp = 1.25f + (i % 3) * 0.55f,
                startFraction = (i % 9) * 0.024f,
                endFraction = 0.56f + (i % 5) * 0.05f,
                holdsAtCompletion = false
            )
        }
    }

    // 24 Converging radial energy streams that feed directly into the perimeter ring before/during logo reveal
    val convergingRays = remember {
        val palette = listOf(
            Color(0xFF00E676),
            Color(0xFFFF1744),
            Color(0xFFFFD54F),
            Color(0xFF69F0AE)
        )
        List(24) { i ->
            val angle = (i * (360f / 24f)) * (PI.toFloat() / 180f)
            ConvergingEnergyRay(
                angleRad = angle,
                outerFactor = 0.92f + (i % 3) * 0.10f,
                innerFactor = 0.43f, // Exact perimeter ring radius factor in Logo Stage Box
                color = palette[i % palette.size],
                startFraction = 0.10f + (i % 6) * 0.028f,
                endFraction = 0.48f + (i % 4) * 0.035f
            )
        }
    }

    // 28 Synchronized orbital light motes around the logo perimeter that remain gently poised at completion
    val orbitalMotes = remember {
        val signaturePalette = listOf(
            Color(0xFF00E676), // R.E.D Tree Emerald
            Color(0xFFFF1744), // R.E.D Tree Crimson
            Color(0xFFFFD54F), // R.E.D Tree Sovereign Gold
            Color(0xFF69F0AE)  // Luminous Mint
        )
        List(28) { i ->
            val angle = (i * (360f / 28f)) * (PI.toFloat() / 180f)
            val direction = if (i % 2 == 0) 1f else -1f
            IntroLightMote(
                startAngleRad = angle,
                angularSweepRad = direction * (0.58f + (i % 4) * 0.14f),
                baseOrbitFactor = 0.435f + (i % 5) * 0.032f,
                radialDeltaFactor = 0.05f + (i % 3) * 0.018f,
                verticalRiseDp = -6f - (i % 4) * 3f,
                color = signaturePalette[i % signaturePalette.size],
                radiusDp = 1.35f + (i % 3) * 0.55f,
                startFraction = 0.28f + (i % 7) * 0.025f,
                endFraction = 1.0f,
                holdsAtCompletion = i % 2 == 0
            )
        }
    }

    val progressBarBrush = remember {
        Brush.horizontalGradient(listOf(NeonRed, NeonYellow, NeonGreen))
    }

    val dividerLineBrush = remember {
        Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                NeonRed.copy(alpha = 0.75f),
                NeonYellow,
                NeonGreen.copy(alpha = 0.75f),
                Color.Transparent
            )
        )
    }

    val brandAnnotatedTitle = remember {
        buildAnnotatedString {
            withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.Black)) { append("R.") }
            withStyle(SpanStyle(color = NeonGreen, fontWeight = FontWeight.Black)) { append("E.") }
            withStyle(SpanStyle(color = NeonYellow, fontWeight = FontWeight.Black)) { append("D ") }
            withStyle(SpanStyle(color = NeonGreenBright, fontWeight = FontWeight.Black)) { append("TREE") }
        }
    }

    // Runs the 5.5-second Beginning Animation and HOLDS at 1.0f (NEVER auto-clicks or auto-navigates)
    LaunchedEffect(Unit) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5500, easing = LinearEasing)
        )
    }

    val isReady by remember {
        derivedStateOf { animProgress.value >= 0.94f }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .testTag("splash_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // HERO STAGE BOX: Concentric Background Particles, Synchronized Perimeter Ring, and Untouched Official Logo
            // Placing both the Canvas and the Image inside the exact same 240.dp centered Box guarantees
            // 100% spatial concentric alignment between the ring and the official logo on all screen sizes.
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val p = animProgress.value
                    val stageRadius = size.minDimension * 0.5f
                    val center = Offset(size.width * 0.5f, size.height * 0.5f)

                    // Unified Logo + Ring synchronization curve (p: 0.34 -> 0.69, i.e., 1.87s -> 3.80s)
                    val rawReveal = ((p - 0.34f) / 0.35f).coerceIn(0f, 1f)
                    val syncReveal = SynchronizedRevealEasing.transform(rawReveal)

                    // 1. OPENING BACKGROUND ENERGY (0.0s – 2.5s): Subtle ambient horizon wake rings
                    val wakePhase1 = (p / 0.42f).coerceIn(0f, 1f)
                    if (wakePhase1 in 0.01f..0.99f) {
                        val wakeAlpha = sin(wakePhase1 * PI).toFloat() * 0.28f
                        val wakeR = stageRadius * (1.18f - 0.32f * wakePhase1)
                        drawCircle(
                            color = NeonGreenBright.copy(alpha = wakeAlpha),
                            radius = wakeR,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    val wakePhase2 = ((p - 0.08f) / 0.38f).coerceIn(0f, 1f)
                    if (wakePhase2 in 0.01f..0.99f) {
                        val wakeAlpha2 = sin(wakePhase2 * PI).toFloat() * 0.24f
                        val wakeR2 = stageRadius * (1.05f - 0.22f * wakePhase2)
                        drawCircle(
                            color = NeonRed.copy(alpha = wakeAlpha2),
                            radius = wakeR2,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // 2. DENSE CONTROLLED BACKGROUND PARTICLES (0.0s – 3.1s): Drifting inward from the dark field
                    backgroundFieldMotes.forEach { mote ->
                        val span = (mote.endFraction - mote.startFraction).coerceAtLeast(0.01f)
                        val localP = ((p - mote.startFraction) / span).coerceIn(0f, 1f)
                        if (localP in 0.01f..0.99f) {
                            val envAlpha = sin(localP * PI).toFloat()
                            val currentAngle = mote.startAngleRad + mote.angularSweepRad * localP
                            val currentRadius = stageRadius * (mote.baseOrbitFactor + mote.radialDeltaFactor * localP)
                            val px = center.x + cos(currentAngle) * currentRadius
                            val py = center.y + sin(currentAngle) * currentRadius + (mote.verticalRiseDp * localP * density)
                            val rPx = mote.radiusDp * density

                            drawCircle(
                                color = mote.color.copy(alpha = 0.78f * envAlpha),
                                radius = rPx,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f * envAlpha),
                                radius = rPx * 0.42f,
                                center = Offset(px, py)
                            )
                        }
                    }

                    // 3. CONVERGING ENERGY FILAMENTS (0.55s – 2.65s): Feeding energy into the perimeter ring
                    convergingRays.forEach { ray ->
                        val span = (ray.endFraction - ray.startFraction).coerceAtLeast(0.01f)
                        val localP = ((p - ray.startFraction) / span).coerceIn(0f, 1f)
                        if (localP in 0.01f..0.99f) {
                            val rayAlpha = sin(localP * PI).toFloat() * 0.55f
                            val headProgress = SynchronizedRevealEasing.transform(localP)
                            val tailProgress = (localP - 0.28f).coerceIn(0f, 1f)
                            val rStart = stageRadius * (ray.outerFactor + (ray.innerFactor - ray.outerFactor) * tailProgress)
                            val rEnd = stageRadius * (ray.outerFactor + (ray.innerFactor - ray.outerFactor) * headProgress)
                            val cosA = cos(ray.angleRad)
                            val sinA = sin(ray.angleRad)

                            drawLine(
                                color = ray.color.copy(alpha = rayAlpha),
                                start = Offset(center.x + cosA * rStart, center.y + sinA * rStart),
                                end = Offset(center.x + cosA * rEnd, center.y + sinA * rEnd),
                                strokeWidth = 1.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // 4. SYNCHRONIZED PERIMETER RING & LIGHT ARCS (Locked 1:1 with Official Logo Reveal)
                    // Pre-build phase (p: 0.22 -> 0.34) seamlessly transitions into syncReveal (p: 0.34 -> 0.69) and holds cleanly
                    val ringBuildPhase = ((p - 0.22f) / 0.47f).coerceIn(0f, 1f)
                    if (ringBuildPhase > 0.01f) {
                        val ringCurve = SynchronizedRevealEasing.transform(ringBuildPhase)
                        // Exact scale synchronization with logo (0.90f -> 1.00f)
                        val syncedScale = 0.90f + 0.10f * syncReveal
                        val outerRingRadius = stageRadius * 0.84f * syncedScale
                        val innerRingRadius = stageRadius * 0.78f * syncedScale

                        // Settle alpha softly to a poised halo after reveal completes so the screen looks premium while waiting
                        val postRevealSettle = if (p > 0.72f) {
                            1f - 0.35f * ((p - 0.72f) / 0.28f).coerceIn(0f, 1f)
                        } else 1f
                        val masterRingAlpha = ringCurve * postRevealSettle

                        // Sweep angle grows from 24° to a full 172° per hemisphere as the logo reaches 100% visibility
                        val hemisphereSweep = 24f + 148f * ringCurve
                        // Smooth rotation that decelerates to an exact aligned lock at 0° when ringCurve reaches 1f
                        val rotationDeg = (1f - ringCurve) * 140f

                        // Left Hemisphere: R.E.D Tree Emerald Precision Arc
                        drawArc(
                            color = NeonGreenBright.copy(alpha = 0.82f * masterRingAlpha),
                            startAngle = 94f + rotationDeg,
                            sweepAngle = hemisphereSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - outerRingRadius, center.y - outerRingRadius),
                            size = Size(outerRingRadius * 2f, outerRingRadius * 2f),
                            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Right Hemisphere: R.E.D Tree Crimson Precision Arc
                        drawArc(
                            color = NeonRed.copy(alpha = 0.82f * masterRingAlpha),
                            startAngle = 274f + rotationDeg,
                            sweepAngle = hemisphereSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - outerRingRadius, center.y - outerRingRadius),
                            size = Size(outerRingRadius * 2f, outerRingRadius * 2f),
                            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Inner Concentric Sovereign Gold Synchronization Ring
                        val goldSweep = 360f * syncReveal
                        if (goldSweep > 2f) {
                            drawArc(
                                color = NeonYellow.copy(alpha = 0.58f * masterRingAlpha),
                                startAngle = -90f - (1f - syncReveal) * 90f,
                                sweepAngle = goldSweep,
                                useCenter = false,
                                topLeft = Offset(center.x - innerRingRadius, center.y - innerRingRadius),
                                size = Size(innerRingRadius * 2f, innerRingRadius * 2f),
                                style = Stroke(width = 1.15.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Crown & Base Lock-In Nodes when Logo + Ring reach alignment
                        if (syncReveal > 0.65f) {
                            val nodeAlpha = ((syncReveal - 0.65f) / 0.35f).coerceIn(0f, 1f) * postRevealSettle
                            drawCircle(
                                color = NeonYellow.copy(alpha = 0.85f * nodeAlpha),
                                radius = 2.4.dp.toPx(),
                                center = Offset(center.x, center.y - outerRingRadius)
                            )
                            drawCircle(
                                color = NeonYellow.copy(alpha = 0.85f * nodeAlpha),
                                radius = 2.4.dp.toPx(),
                                center = Offset(center.x, center.y + outerRingRadius)
                            )
                        }
                    }

                    // 5. SYNCHRONIZED LOCK-IN HORIZON RIPPLE (Triggers as Logo & Ring complete lock-in)
                    val ripplePhase = ((p - 0.62f) / 0.26f).coerceIn(0f, 1f)
                    if (ripplePhase in 0.01f..0.99f) {
                        val rippleRadius = stageRadius * (0.84f + 0.30f * ripplePhase)
                        val rippleAlpha = sin(ripplePhase * PI).toFloat() * 0.50f
                        drawCircle(
                            color = NeonYellow.copy(alpha = rippleAlpha),
                            radius = rippleRadius,
                            center = center,
                            style = Stroke(width = 1.1.dp.toPx())
                        )
                    }

                    // 6. ORBITAL PERIMETER LIGHT MOTES (Glide smoothly around the sharp logo)
                    orbitalMotes.forEach { mote ->
                        val span = (mote.endFraction - mote.startFraction).coerceAtLeast(0.01f)
                        val localP = ((p - mote.startFraction) / span).coerceIn(0f, 1f)
                        if (localP > 0.01f) {
                            val envAlpha = when {
                                localP < 0.22f -> localP / 0.22f
                                localP > 0.82f -> if (mote.holdsAtCompletion) {
                                    1f - 0.45f * ((localP - 0.82f) / 0.18f).coerceIn(0f, 1f)
                                } else {
                                    (1f - localP) / 0.18f
                                }
                                else -> 1f
                            }
                            if (envAlpha > 0.01f) {
                                val currentAngle = mote.startAngleRad + mote.angularSweepRad * localP
                                val currentRadius = stageRadius * (0.42f + mote.baseOrbitFactor + mote.radialDeltaFactor * localP)
                                val px = center.x + cos(currentAngle) * currentRadius
                                val py = center.y + sin(currentAngle) * currentRadius + (mote.verticalRiseDp * localP * density)
                                val rPx = mote.radiusDp * density

                                drawCircle(
                                    color = mote.color.copy(alpha = 0.85f * envAlpha),
                                    radius = rPx,
                                    center = Offset(px, py)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.90f * envAlpha),
                                    radius = rPx * 0.42f,
                                    center = Offset(px, py)
                                )
                            }
                        }
                    }
                }

                // Official R.E.D TREE Logo — 100% Untouched Asset Synchronized with the Perimeter Ring
                Image(
                    painter = painterResource(id = R.drawable.img_logo_red_tree),
                    contentDescription = "R.E.D Tree Official Logo",
                    modifier = Modifier
                        .size(174.dp)
                        .graphicsLayer {
                            val p = animProgress.value
                            val rawReveal = ((p - 0.34f) / 0.35f).coerceIn(0f, 1f)
                            val syncReveal = SynchronizedRevealEasing.transform(rawReveal)
                            val scaleVal = 0.90f + 0.10f * syncReveal
                            scaleX = scaleVal
                            scaleY = scaleVal
                            alpha = syncReveal
                        }
                        .testTag("logo_red_tree_image"),
                    contentScale = ContentScale.Fit
                )
            }

            // Smooth Reveal of the R.E.D TREE Identity & Corrected Full Form Tagline
            Column(
                modifier = Modifier.graphicsLayer {
                    val p = animProgress.value
                    val textProgress = ((p - 0.70f) / 0.18f).coerceIn(0f, 1f)
                    val eased = SynchronizedRevealEasing.transform(textProgress)
                    alpha = eased
                    translationY = (1f - eased) * 14.dp.toPx()
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = brandAnnotatedTitle,
                    fontSize = 32.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Sleek Expanding Laser Accent Line beneath R.E.D TREE title
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.52f)
                        .height(2.dp)
                ) {
                    val linePhase = ((animProgress.value - 0.74f) / 0.16f).coerceIn(0f, 1f)
                    val halfW = (size.width / 2f) * SynchronizedRevealEasing.transform(linePhase)
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    if (halfW > 1f) {
                        drawLine(
                            brush = dividerLineBrush,
                            start = Offset(cx - halfW, cy),
                            end = Offset(cx + halfW, cy),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Remove  •  Education  •  Distraction",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.testTag("splash_full_form_tagline")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Glowing cinematic progress bar tracking the 5.5s intro timeline
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .drawBehind {
                            val fillWidth = (size.width * animProgress.value).coerceIn(0f, size.width)
                            if (fillWidth > 0f) {
                                drawRoundRect(
                                    brush = progressBarBrush,
                                    size = Size(fillWidth, size.height),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (!isReady) "INITIALIZING..." else "READY",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isReady) NeonGreen else TextMuted,
                    letterSpacing = 1.sp
                )
            }

            // Enter R.E.D Tree button — remains visible and waits for manual user tap (NEVER auto-clicks)
            GlowButton(
                text = "Enter R.E.D Tree →",
                onClick = triggerEnterApp,
                gradient = OrangeYellowGradient,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .testTag("btn_enter_splash")
            )
        }
    }
}
