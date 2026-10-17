package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.RedOrangeGradient
import com.example.ui.theme.TextSecondary

/**
 * Top Branding Header as seen across the reference screens:
 * Small glowing tree emblem + "R.E.D TREE" (Red, Green, Yellow, Green)
 */
@Composable
fun RedTreeBrandingHeader(
    modifier: Modifier = Modifier,
    showSubtitle: Boolean = true,
    subtitle: String = "Remove • Education • Distraction"
) {
    val brandText = remember {
        buildAnnotatedString {
            withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.Black)) { append("R.") }
            withStyle(SpanStyle(color = NeonGreen, fontWeight = FontWeight.Black)) { append("E.") }
            withStyle(SpanStyle(color = NeonYellow, fontWeight = FontWeight.Black)) { append("D ") }
            withStyle(SpanStyle(color = NeonGreenBright, fontWeight = FontWeight.Black)) { append("TREE") }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Glowing Tree Icon Emblem
            Image(
                painter = painterResource(id = R.drawable.img_logo_red_tree),
                contentDescription = "R.E.D Tree Logo",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Text: R.E.D TREE
            Text(
                text = brandText,
                fontSize = 17.sp,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp
            )
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Ambient glowing leaves background canvas for nature-cyber feel.
 * Rendered cleanly without continuous 60fps full-screen dirty-rect invalidation
 * so touch input and streaming preview remain instant and responsive across all screens.
 */
@Composable
fun FloatingLeavesAtmosphere(
    modifier: Modifier = Modifier,
    isPaused: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val rSmall = 1.8.dp.toPx()
        val rMed = 2.2.dp.toPx()

        // Crisp pinpoint ambient spores in screen corners (no large circular discs or blobs)
        drawCircle(
            color = NeonGreen,
            alpha = 0.28f,
            center = Offset(24.dp.toPx(), 44.dp.toPx()),
            radius = rMed
        )
        drawCircle(
            color = NeonGreen,
            alpha = 0.22f,
            center = Offset(w - 28.dp.toPx(), 72.dp.toPx()),
            radius = rSmall
        )
    }
}

/**
 * Modern Segmented Toggle with glowing active state (e.g. Daily / Weekly / Monthly, Sign Up / Sign In)
 */
@Composable
fun SegmentedNeonTabs(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = NeonYellow,
    activeGradient: Brush? = null
) {
    val defaultGradient = remember(activeColor) {
        Brush.horizontalGradient(listOf(activeColor, activeColor.copy(alpha = 0.8f)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
            .background(BlackSurface.copy(alpha = 0.8f))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                val backgroundBrush = if (isSelected) {
                    activeGradient ?: defaultGradient
                } else null
                val tabTag = "segment_tab_${title.lowercase().replace(" ", "_")}"

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .then(
                            if (backgroundBrush != null) Modifier.background(backgroundBrush)
                            else Modifier.background(Color.Transparent)
                        )
                        .clickable { onSelect(index) }
                        .padding(vertical = 10.dp)
                        .testTag(tabTag),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) {
                            if (activeGradient != null) Color.White else Color.Black
                        } else TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Signature Rounded Glow Button with vibrant gradient as shown in the reference image
 */
@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = RedOrangeGradient,
    textColor: Color = Color.White,
    icon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}
