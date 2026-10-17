package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.CompanionCharacter

/**
 * Authentic Companion Artwork Renderer for Forest Cutu & Forest Cutie.
 * Strictly renders the approved PNG artwork (forest_cutu.png and forest_cutie.png)
 * preserving 100% exact original colors, face, eyes, body, and proportions with ContentScale.Fit.
 * Zero generic cartoon generation, zero redrawing, and zero distortion.
 */
@Composable
fun ForestCompanionArtwork(
    character: CompanionCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    squishX: Float = 1f,
    squishY: Float = 1f,
    blinkProgress: Float = 0f,
    isHappyBlushing: Boolean = false,
    eyeTilt: Float = 0f
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = character.drawableRes),
            contentDescription = character.displayName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
