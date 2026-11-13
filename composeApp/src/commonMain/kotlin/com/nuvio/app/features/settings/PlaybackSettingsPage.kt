package com.nuvio.app.features.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ClosedCaption
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvio.app.features.p2p.P2pSettingsRepository
import com.nuvio.app.features.player.PlayerSettingsRepository
import com.nuvio.app.isIos
import org.jetbrains.compose.resources.stringResource

internal fun LazyListScope.playbackSettingsContent(
    isTablet: Boolean,
    page: SettingsPage,
    onPageChange: (SettingsPage) -> Unit,
) {
    item {
        val settings by remember {
            PlayerSettingsRepository.ensureLoaded()
            PlayerSettingsRepository.uiState
        }.collectAsStateWithLifecycle()
        when (page) {
            SettingsPage.Playback -> {
                val sections = visiblePlaybackSections(isIos, settings.androidPlaybackEngine, P2pSettingsRepository.isVisible)
                SettingsGroup(isTablet = isTablet) {
                    sections.forEachIndexed { index, section ->
                        if (index > 0) SettingsGroupDivider(isTablet = isTablet)
                        SettingsNavigationRow(
                            title = stringResource(section.page.titleRes),
                            description = stringResource(section.descriptionRes),
                            icon = section.page.playbackIcon(),
                            isTablet = isTablet,
                            onClick = { onPageChange(section.page) },
                        )
                    }
                }
            }
            SettingsPage.PlaybackPlayer -> PlaybackPlayerSettings(isTablet, settings)
            SettingsPage.PlaybackStreamSelection -> PlaybackStreamSettings(isTablet, settings)
            SettingsPage.PlaybackUpNext -> PlaybackUpNextSettings(isTablet, settings)
            SettingsPage.PlaybackSkipSegments -> PlaybackSkipSettings(isTablet, settings)
            SettingsPage.PlaybackInterface -> PlaybackInterfaceSettings(isTablet, settings)
            SettingsPage.PlaybackAudio -> PlaybackAudioSettings(isTablet, settings)
            SettingsPage.PlaybackSubtitles -> PlaybackSubtitleSettings(isTablet, settings)
            SettingsPage.PlaybackVideo -> PlaybackVideoSettings(isTablet, settings)
            SettingsPage.PlaybackBuffer -> if (!isIos) PlaybackBufferSettings(isTablet, settings)
            SettingsPage.PlaybackP2p -> if (P2pSettingsRepository.isVisible) PlaybackP2pSettings(isTablet)
            else -> Unit
        }
    }
}

/** An icon per Playback page, so the list reads like the main Settings page. */
private fun SettingsPage.playbackIcon(): androidx.compose.ui.graphics.vector.ImageVector? =
    when (this) {
        SettingsPage.PlaybackPlayer -> Icons.Rounded.PlayCircle
        SettingsPage.PlaybackStreamSelection -> Icons.Rounded.Bolt
        SettingsPage.PlaybackUpNext -> Icons.Rounded.SkipNext
        SettingsPage.PlaybackSkipSegments -> Icons.Rounded.FastForward
        SettingsPage.PlaybackInterface -> Icons.Rounded.TouchApp
        SettingsPage.PlaybackAudio -> Icons.Rounded.GraphicEq
        SettingsPage.PlaybackSubtitles -> Icons.Rounded.ClosedCaption
        SettingsPage.PlaybackVideo -> Icons.Rounded.HighQuality
        SettingsPage.PlaybackBuffer -> Icons.Rounded.Memory
        SettingsPage.PlaybackP2p -> Icons.Rounded.Share
        else -> null
    }
