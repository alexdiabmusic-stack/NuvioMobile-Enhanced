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

@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val tokens = MaterialTheme.nuvio
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(NuvioTokens.Radius.md),
        color = if (selected) tokens.colors.overlaySelected else Color.Transparent,
        contentColor = when {
            !enabled -> tokens.colors.textDisabled
            selected -> tokens.colors.textPrimary
            else -> tokens.colors.textMuted
        },
        border = BorderStroke(
            width = tokens.borders.thin,
            color = when {
                selected -> tokens.colors.borderSelected
                enabled -> tokens.colors.borderStrong
                else -> tokens.colors.borderDefault
            },
        ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = NuvioTokens.Space.s12,
                vertical = tokens.components.chipVerticalPadding,
            ),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
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
