package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright

/**
 * Exact 9 Stages of Tree Growth as defined by the user:
 * Seed → Seedling → Baby Plant → Plant → Big Plant → Tree → Giant Tree → Old Tree → Legendary R.E.D Tree
 */
enum class TreeGrowthStage(
    val stageNumber: Int,
    val level: Int,
    val stageName: String,
    val requiredXp: Int,
    val maxXp: Int,
    val xpRangeText: String,
    val requiredMinutes: Int,
    val subtitle: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val auraColor: Color,
    val drawableResId: Int
) {
    SEED(
        stageNumber = 1,
        level = 1,
        stageName = "Seed",
        requiredXp = 0,
        maxXp = 50,
        xpRangeText = "0 - 50 XP",
        requiredMinutes = 0,
        subtitle = "Golden Dormant Nucleus",
        description = "A radiant golden seed resting on fertile soil, surrounded by a luminous golden ring of light and sparkling embers.",
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFFFFA000),
        auraColor = Color(0xFFFFE082),
        drawableResId = R.drawable.stage_1_seed
    ),
    SEEDLING(
        stageNumber = 2,
        level = 2,
        stageName = "Seedling",
        requiredXp = 51,
        maxXp = 150,
        xpRangeText = "51 - 150 XP",
        requiredMinutes = 5,
        subtitle = "Emergent Sprout",
        description = "A slender luminous green shoot opening into twin radiant cotyledon leaves in a vibrant V-shape.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF76FF03),
        auraColor = Color(0xFF69F0AE),
        drawableResId = R.drawable.stage_2_seedling
    ),
    BABY_PLANT(
        stageNumber = 3,
        level = 3,
        stageName = "Baby Plant",
        requiredXp = 151,
        maxXp = 300,
        xpRangeText = "151 - 300 XP",
        requiredMinutes = 15,
        subtitle = "Four Glowing Leaves",
        description = "Taller green stalk branching with four distinct glowing emerald leaves and active bioluminescent spores.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF00C853),
        auraColor = Color(0xFF76FF03),
        drawableResId = R.drawable.stage_3_baby_plant
    ),
    PLANT(
        stageNumber = 4,
        level = 4,
        stageName = "Plant",
        requiredXp = 301,
        maxXp = 550,
        xpRangeText = "301 - 550 XP",
        requiredMinutes = 25,
        subtitle = "Branching Foliage",
        description = "Sturdy central stalk with multiple pairs of lush vibrant leaves topped with a fresh apical shoot.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF69F0AE),
        auraColor = Color(0xFF00E5FF),
        drawableResId = R.drawable.stage_4_plant
    ),
    BIG_PLANT(
        stageNumber = 5,
        level = 5,
        stageName = "Big Plant",
        requiredXp = 551,
        maxXp = 900,
        xpRangeText = "551 - 900 XP",
        requiredMinutes = 40,
        subtitle = "Dense Bushy Dome",
        description = "A dense, spherical emerald shrub with overlapping leaf clusters, short woody stem, and floating firefly spores.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF1B5E20),
        auraColor = Color(0xFFAEEA00),
        drawableResId = R.drawable.stage_5_big_plant
    ),
    TREE(
        stageNumber = 6,
        level = 6,
        stageName = "Tree",
        requiredXp = 901,
        maxXp = 1400,
        xpRangeText = "901 - 1400 XP",
        requiredMinutes = 60,
        subtitle = "Spreading Mature Canopy",
        description = "Sculpted brown wood trunk with golden bark channels, supporting a lush umbrella canopy of emerald leaves.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFFB300),
        auraColor = Color(0xFFFFD54F),
        drawableResId = R.drawable.stage_6_tree
    ),
    GIANT_TREE(
        stageNumber = 7,
        level = 7,
        stageName = "Giant Tree",
        requiredXp = 1401,
        maxXp = 2100,
        xpRangeText = "1401 - 2100 XP",
        requiredMinutes = 90,
        subtitle = "Ancient Expansive Crown",
        description = "Massive ancient trunk coursing with golden energy veins, holding a magnificent spreading green crown.",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFFA000),
        auraColor = Color(0xFF76FF03),
        drawableResId = R.drawable.stage_7_tree
    ),
    OLD_TREE(
        stageNumber = 8,
        level = 8,
        stageName = "Old Tree",
        requiredXp = 2101,
        maxXp = 3000,
        xpRangeText = "2101 - 3000 XP",
        requiredMinutes = 120,
        subtitle = "Golden Wisdom",
        description = "Venerable ancient tree whose canopy radiates a breathtaking blend of emerald green and glowing golden-yellow leaves.",
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFF00C853),
        auraColor = Color(0xFFFFA000),
        drawableResId = R.drawable.stage_8__tree
    ),
    LEGENDARY_RED_TREE(
        stageNumber = 9,
        level = 9,
        stageName = "Legendary R.E.D Tree",
        requiredXp = 3001,
        maxXp = 5000,
        xpRangeText = "3001+ XP",
        requiredMinutes = 180,
        subtitle = "Cosmic Fire Vortex",
        description = "The ultimate mythical transformation: a fiery crimson and golden ancient tree enclosed within a roaring circular halo of flame and embers.",
        primaryColor = Color(0xFFFF1744),
        secondaryColor = Color(0xFFFF6D00),
        auraColor = Color(0xFFFF2A4B),
        drawableResId = R.drawable.stage_9_legendry_red_tree
    );

    companion object {
        fun fromXp(xp: Int): TreeGrowthStage {
            return entries.lastOrNull { xp >= it.requiredXp } ?: SEED
        }

        fun fromMinutes(minutes: Int): TreeGrowthStage {
            return entries.lastOrNull { minutes >= it.requiredMinutes } ?: SEED
        }
    }
}

/**
 * Master 9-Stage Tree Growth Section on Study Screen.
 */
@Composable
fun StudySessionTreeGrowthSection(
    currentXp: Int,
    sessionSecondsStudied: Int,
    isTimerRunning: Boolean,
    totalSessionSeconds: Int = 30 * 60,
    modifier: Modifier = Modifier
) {
    val activeStage = TreeGrowthStage.fromXp(currentXp)

    var inspectedStage by remember { mutableStateOf<TreeGrowthStage?>(null) }
    val displayStage = inspectedStage ?: activeStage

    val nextStageIndex = activeStage.ordinal + 1
    val nextStage = if (nextStageIndex < TreeGrowthStage.entries.size) {
        TreeGrowthStage.entries[nextStageIndex]
    } else null

    val stageProgress = remember(currentXp, activeStage, nextStage) {
        if (nextStage == null) {
            1f
        } else {
            val range = (nextStage.requiredXp - activeStage.requiredXp).coerceAtLeast(1)
            val currentOffset = (currentXp - activeStage.requiredXp).coerceAtLeast(0)
            (currentOffset.toFloat() / range.toFloat()).coerceIn(0f, 1f)
        }
    }

    val animatedStageProgress by animateFloatAsState(
        targetValue = stageProgress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "stage_progress_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F151F),
                        Color(0xFF080C14)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        displayStage.primaryColor.copy(alpha = 0.55f),
                        displayStage.auraColor.copy(alpha = 0.40f),
                        displayStage.secondaryColor.copy(alpha = 0.55f)
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
            .testTag("study_tree_growth_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(displayStage.primaryColor.copy(alpha = 0.18f))
                            .border(1.dp, displayStage.primaryColor.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (displayStage.stageNumber) {
                                1 -> Icons.Outlined.Spa
                                2 -> Icons.Outlined.Eco
                                3 -> Icons.Outlined.LocalFlorist
                                4, 5 -> Icons.Outlined.Spa
                                6, 7, 8 -> Icons.Outlined.Park
                                else -> Icons.Outlined.Star
                            },
                            contentDescription = "Stage icon",
                            tint = displayStage.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "STAGE ${displayStage.stageNumber}/9 • LEVEL ${displayStage.level}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = displayStage.auraColor,
                                letterSpacing = 1.sp
                            )
                            if (displayStage == activeStage) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreenBright
                                    )
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayStage.stageName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayStage.xpRangeText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                if (inspectedStage != null && inspectedStage != activeStage) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, displayStage.auraColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { inspectedStage = null }
                            .padding(horizontal = 9.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Sync,
                                contentDescription = "Sync",
                                tint = displayStage.auraColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live Stage",
                                fontSize = 10.5.sp,
                                color = displayStage.auraColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (isTimerRunning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF072412))
                            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(NeonGreenBright)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GROWING",
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreenBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "“${displayStage.subtitle}” • ${displayStage.description}",
                fontSize = 11.5.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 9-STAGE UPLOADED PNG ARTWORK CONTAINER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                displayStage.auraColor.copy(alpha = 0.20f),
                                Color(0xFF080D16),
                                Color(0xFF050810)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        displayStage.primaryColor.copy(alpha = 0.35f),
                        RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                LivingTreeGrowthViewer(
                    targetStage = displayStage,
                    isTimerRunning = isTimerRunning,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // XP & PROGRESS BAR TOWARDS NEXT STAGE
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (displayStage == activeStage) {
                            if (nextStage != null) {
                                "$currentXp / ${nextStage.requiredXp} XP"
                            } else {
                                "$currentXp XP • MAX STAGE"
                            }
                        } else {
                            "Requires ${displayStage.requiredXp} XP (${displayStage.requiredMinutes}m study)"
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = displayStage.auraColor
                    )

                    if (displayStage == activeStage && nextStage != null) {
                        Text(
                            text = "Next: ${nextStage.stageName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF131822))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (displayStage == activeStage) animatedStageProgress else 1f)
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        displayStage.primaryColor,
                                        displayStage.auraColor,
                                        displayStage.secondaryColor
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Session Focus: ${sessionSecondsStudied / 60}m ${sessionSecondsStudied % 60}s",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "+${sessionSecondsStudied * 2} XP Earned",
                        fontSize = 10.sp,
                        color = NeonGreenBright,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 9-STAGE INTERACTIVE TIMELINE SELECTOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TreeGrowthStage.entries.forEach { stage ->
                    val isCurrentInspected = displayStage == stage
                    val isReached = currentXp >= stage.requiredXp

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                when {
                                    isCurrentInspected -> stage.auraColor
                                    isReached -> stage.primaryColor.copy(alpha = 0.45f)
                                    else -> Color.White.copy(alpha = 0.1f)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .background(
                                when {
                                    isCurrentInspected -> stage.auraColor.copy(alpha = 0.22f)
                                    isReached -> Color(0xE60D121B)
                                    else -> Color(0x66080B10)
                                }
                            )
                            .clickable {
                                inspectedStage = stage
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("stage_pill_${stage.stageNumber}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isReached) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(stage.primaryColor)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(9.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stage.stageNumber}. ${stage.stageName}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isCurrentInspected) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isCurrentInspected -> stage.auraColor
                                    isReached -> Color.White
                                    else -> Color(0xFF64748B)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Living Tree Growth Viewer using the 9 uploaded stage PNGs.
 * Optimized to read animation state strictly inside graphicsLayer so zero recompositions
 * or idle 60fps loops occur when viewing the Study or Tree Growth screens.
 */
@Composable
fun LivingTreeGrowthViewer(
    targetStage: TreeGrowthStage,
    isTimerRunning: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentRenderStage by remember { mutableStateOf(targetStage) }
    var previousRenderStage by remember { mutableStateOf<TreeGrowthStage?>(null) }
    val growthTransition = remember { Animatable(1f) }

    LaunchedEffect(targetStage) {
        if (targetStage != currentRenderStage) {
            previousRenderStage = currentRenderStage
            currentRenderStage = targetStage
            growthTransition.snapTo(0f)
            growthTransition.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 480,
                    easing = FastOutSlowInEasing
                )
            )
            previousRenderStage = null
        }
    }

    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 1.04f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isTapped = false },
        label = "living_tree_tap"
    )

    val containerBgColor = Color(0xFF070B14)
    val auraBrush = remember(currentRenderStage) {
        Brush.radialGradient(
            colors = listOf(
                currentRenderStage.auraColor.copy(alpha = 0.24f),
                currentRenderStage.primaryColor.copy(alpha = 0.08f),
                Color.Transparent
            )
        )
    }

    val horizontalVignetteBrush = remember {
        Brush.horizontalGradient(
            0.00f to containerBgColor,
            0.04f to Color.Transparent,
            0.96f to Color.Transparent,
            1.00f to containerBgColor
        )
    }

    val verticalVignetteBrush = remember {
        Brush.verticalGradient(
            0.91f to Color.Transparent,
            1.00f to containerBgColor.copy(alpha = 0.80f)
        )
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isTapped = true
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. Soft ambient aura glow behind the stage image
        Box(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .aspectRatio(1.025f)
                .background(auraBrush, shape = CircleShape)
        )

        // 2. Crisp, large, centered framing box for the 9 uploaded PNG stage images
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1.025f)
                .clipToBounds()
                .drawWithContent {
                    drawContent()
                    drawRect(brush = horizontalVignetteBrush)
                    drawRect(brush = verticalVignetteBrush)
                },
            contentAlignment = Alignment.Center
        ) {
            val prev = previousRenderStage

            if (prev != null) {
                Image(
                    painter = painterResource(id = prev.drawableResId),
                    contentDescription = prev.stageName,
                    contentScale = ContentScale.Crop,
                    alignment = BiasAlignment(-0.0102f, 0f),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val progress = growthTransition.value
                            val prevScale = (1f - 0.04f * progress) * tapScale
                            scaleX = prevScale
                            scaleY = prevScale
                            alpha = (1f - progress).coerceIn(0f, 1f)
                            transformOrigin = TransformOrigin(0.5f, 0.92f)
                        }
                )
            }

            Image(
                painter = painterResource(id = currentRenderStage.drawableResId),
                contentDescription = "${currentRenderStage.stageName} (Stage ${currentRenderStage.stageNumber}/9)",
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(-0.0102f, 0f),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val progress = growthTransition.value
                        val isGrowing = prev != null && progress < 1f
                        val sY = if (isGrowing) (0.92f + 0.08f * progress) * tapScale else tapScale
                        val sX = if (isGrowing) (0.94f + 0.06f * progress) * tapScale else tapScale
                        scaleX = sX
                        scaleY = sY
                        alpha = if (isGrowing) progress.coerceIn(0f, 1f) else 1f
                        transformOrigin = TransformOrigin(0.5f, 0.92f)
                    }
            )
        }
    }
}

@Composable
fun TreeStageArtwork(
    stage: TreeGrowthStage,
    isTimerRunning: Boolean = false,
    modifier: Modifier = Modifier
) {
    LivingTreeGrowthViewer(
        targetStage = stage,
        isTimerRunning = isTimerRunning,
        modifier = modifier
    )
}
