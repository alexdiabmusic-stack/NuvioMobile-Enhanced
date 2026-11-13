package com.nuvio.app.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** The app's one chip: the same white-when-chosen pill as [PillChip], kept under this name for existing callers. */
@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val tokens = MaterialTheme.nuvio
    val background by animateColorAsState(
        when {
            selected -> tokens.colors.textPrimary
            else -> tokens.colors.textPrimary.copy(alpha = if (enabled) 0.12f else 0.06f)
        },
    )
    val content by animateColorAsState(
        when {
            !enabled -> tokens.colors.textDisabled
            selected -> tokens.colors.background
            else -> tokens.colors.textPrimary
        },
    )
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = content,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/** A filter pill in Home's button style: solid when chosen (like Play), translucent when not (like My List). */
@Composable
fun PillChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val tokens = MaterialTheme.nuvio
    val background by animateColorAsState(if (selected) tokens.colors.textPrimary else tokens.colors.textPrimary.copy(alpha = 0.12f))
    val content by animateColorAsState(if (selected) tokens.colors.background else tokens.colors.textPrimary)
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = content,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
