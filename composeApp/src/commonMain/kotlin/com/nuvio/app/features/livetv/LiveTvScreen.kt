package com.nuvio.app.features.livetv

import com.nuvio.app.core.ui.nuvioScreenSideBleed
import com.nuvio.app.core.ui.collapsingTitle
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import com.nuvio.app.core.ui.background.appBackgroundCover
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.nuvio.app.core.ui.LocalDynamicArtworkBackgroundActive
import com.nuvio.app.core.ui.NuvioIconActionButton
import com.nuvio.app.core.ui.NuvioInputField
import com.nuvio.app.core.ui.NuvioScreen
import com.nuvio.app.core.ui.NuvioScreenHeader
import com.nuvio.app.core.ui.NuvioTokens
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.core.ui.nuvioPlatformExtraTopPadding
import com.nuvio.app.features.home.components.HomeEmptyStateCard
import com.nuvio.app.features.sportsfocus.GuideChannelPicker
import com.nuvio.app.features.sportsfocus.GuideChannelStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.action_retry
import nuvio.composeapp.generated.resources.live_tv_button_continue
import nuvio.composeapp.generated.resources.live_tv_categories
import nuvio.composeapp.generated.resources.live_tv_category_channel_count
import nuvio.composeapp.generated.resources.live_tv_empty_message
import nuvio.composeapp.generated.resources.live_tv_empty_title
import nuvio.composeapp.generated.resources.live_tv_filter_choose_category
import nuvio.composeapp.generated.resources.live_tv_filter_favorites
import nuvio.composeapp.generated.resources.live_tv_group_all_channels
import nuvio.composeapp.generated.resources.live_tv_group_uncategorized
import nuvio.composeapp.generated.resources.live_tv_last_watched_title
import nuvio.composeapp.generated.resources.live_tv_load_failed
import nuvio.composeapp.generated.resources.live_tv_no_matching_channels_message
import nuvio.composeapp.generated.resources.live_tv_no_matching_channels_title
import nuvio.composeapp.generated.resources.live_tv_no_playlist_message
import nuvio.composeapp.generated.resources.live_tv_no_playlist_title
import nuvio.composeapp.generated.resources.live_tv_search_placeholder
import nuvio.composeapp.generated.resources.live_tv_title
import nuvio.composeapp.generated.resources.live_tv_action_clear_recents
import nuvio.composeapp.generated.resources.live_tv_action_search
import nuvio.composeapp.generated.resources.live_tv_action_grid_view
import nuvio.composeapp.generated.resources.live_tv_action_list_view
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import nuvio.composeapp.generated.resources.live_tv_filter_recents
import nuvio.composeapp.generated.resources.live_tv_section_channels
import nuvio.composeapp.generated.resources.live_tv_guide_empty_message
import nuvio.composeapp.generated.resources.live_tv_guide_empty_title
import nuvio.composeapp.generated.resources.live_tv_on_demand_empty_message
import nuvio.composeapp.generated.resources.live_tv_on_demand_empty_title
import nuvio.composeapp.generated.resources.live_tv_on_demand_load_failed
import nuvio.composeapp.generated.resources.live_tv_on_demand_movies
import nuvio.composeapp.generated.resources.live_tv_on_demand_search_placeholder
import nuvio.composeapp.generated.resources.live_tv_on_demand_series
import nuvio.composeapp.generated.resources.live_tv_on_demand_unavailable_message
import nuvio.composeapp.generated.resources.live_tv_on_demand_unavailable_title
import nuvio.composeapp.generated.resources.live_tv_sports_empty_message
import nuvio.composeapp.generated.resources.live_tv_sports_empty_title
import nuvio.composeapp.generated.resources.live_tv_sports_filter_live
import nuvio.composeapp.generated.resources.live_tv_sports_load_failed
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import nuvio.composeapp.generated.resources.live_tv_section_guide
import nuvio.composeapp.generated.resources.live_tv_section_on_demand
import nuvio.composeapp.generated.resources.live_tv_section_sports
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiveTvScreen(
    modifier: Modifier = Modifier,
    scrollToTopRequests: Flow<Unit> = emptyFlow(),
    onChannelClick: (LiveTvChannel) -> Unit,
    onVodClick: (LiveTvVodPlayback) -> Unit = {},
    /** Shown under the title, above the channel controls. When set (the Live Hub), it replaces the built-in section tabs. */
    segments: (@Composable () -> Unit)? = null,
) {
    val uiState by remember {
        LiveTvRepository.ensureChannelsLoaded()
        LiveTvRepository.uiState
    }.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val titleCollapse = com.nuvio.app.core.ui.rememberNuvioCollapsingTitleState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var section by rememberSaveable { mutableStateOf(LiveTvSection.Channels) }
    var filterMode by rememberSaveable { mutableStateOf(LiveTvChannelFilterMode.Home) }
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }
    var guideChannelId by rememberSaveable { mutableStateOf<String?>(null) }
    val epgNowMillis = rememberLiveTvEpgNowMillis()
    val guideScrollState = rememberScrollState()
    val guideDensity = LocalDensity.current
    val guideWindowStart = remember(epgNowMillis / LIVE_TV_GUIDE_SLOT_MILLIS) { liveTvGuideWindowStart(epgNowMillis) }
    var sportsFilter by rememberSaveable { mutableStateOf(LIVE_TV_SPORTS_FILTER_ALL) }
    var vodKind by rememberSaveable { mutableStateOf(LiveTvVodKind.Movies) }
    var vodCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var seriesSheetItemId by rememberSaveable { mutableStateOf<String?>(null) }
    val sportsState by LiveTvSportsRepository.uiState.collectAsStateWithLifecycle()
    val onDemandState by LiveTvOnDemandRepository.uiState.collectAsStateWithLifecycle()

    // Playlist categories the profile has turned off are left out of everything on this screen.
    val hiddenGroups by LiveTvChannelPrefs.hiddenGroups.collectAsStateWithLifecycle()
    val shownChannels = remember(uiState.channels, hiddenGroups) {
        if (hiddenGroups.isEmpty()) uiState.channels
        else uiState.channels.filterNot { LiveTvChannelPrefs.groupKey(it.group) in hiddenGroups }
    }
    var showCategoryManager by rememberSaveable { mutableStateOf(false) }
    var showGuideCategories by rememberSaveable { mutableStateOf(false) }
    var showGuideChannelPicker by rememberSaveable { mutableStateOf(false) }
    var guideFilter by rememberSaveable { mutableStateOf<String?>(null) }

    val allChannelsLabel = stringResource(Res.string.live_tv_group_all_channels)
    val favoritesLabel = stringResource(Res.string.live_tv_filter_favorites)
    val uncategorizedLabel = stringResource(Res.string.live_tv_group_uncategorized)
    val chooseCategoryLabel = stringResource(Res.string.live_tv_filter_choose_category)

    val categoryOptions = remember(
        shownChannels,
        allChannelsLabel,
        favoritesLabel,
        uncategorizedLabel,
    ) {
        buildLiveTvCategoryFilterOptions(
            channels = shownChannels,
            allChannelsLabel = allChannelsLabel,
            favoritesLabel = favoritesLabel,
            uncategorizedLabel = uncategorizedLabel,
        )
    }

    val visibleChannels = remember(
        shownChannels,
        uiState.favoriteChannelIds,
        uiState.recentChannelIds,
        filterMode,
        selectedCategoryName,
        searchQuery,
        uncategorizedLabel,
    ) {
        filterLiveTvChannels(
            channels = shownChannels,
            favoriteChannelIds = uiState.favoriteChannelIds,
            recentChannelIds = uiState.recentChannelIds,
            filterMode = filterMode,
            selectedCategoryName = selectedCategoryName,
            searchQuery = searchQuery,
            uncategorizedGroupName = uncategorizedLabel,
        )
    }

    val lastWatchedChannel = remember(shownChannels, uiState.lastWatchedChannelId) {
        uiState.lastWatchedChannelId?.let { channelId ->
            shownChannels.firstOrNull { it.id == channelId }
        }
    }

    val hasRecents = remember(shownChannels, uiState.recentChannelIds) {
        val ids = uiState.recentChannelIds.toSet()
        ids.isNotEmpty() && shownChannels.any { it.id in ids }
    }
    val hasFavorites = remember(shownChannels, uiState.favoriteChannelIds) {
        uiState.favoriteChannelIds.isNotEmpty() &&
            shownChannels.any { it.id in uiState.favoriteChannelIds }
    }
    val categoryChips = remember(categoryOptions, shownChannels, uncategorizedLabel) {
        val hasUncategorized = shownChannels.any { it.group.isNullOrBlank() }
        categoryOptions
            .filter { it.mode == LiveTvChannelFilterMode.Category }
            .filter { hasUncategorized || it.categoryName != uncategorizedLabel }
            .mapNotNull { it.categoryName }
    }

    LaunchedEffect(hasRecents, hasFavorites, filterMode) {
        if ((filterMode == LiveTvChannelFilterMode.Recents && !hasRecents) ||
            (filterMode == LiveTvChannelFilterMode.Favorites && !hasFavorites)
        ) {
            filterMode = LiveTvChannelFilterMode.Home
            selectedCategoryName = null
        }
    }

    val channelChipCounts = remember(
        shownChannels,
        uiState.favoriteChannelIds,
        uiState.recentChannelIds,
        searchQuery,
        uncategorizedLabel,
    ) {
        val searched = filterLiveTvChannels(
            channels = shownChannels,
            favoriteChannelIds = uiState.favoriteChannelIds,
            recentChannelIds = uiState.recentChannelIds,
            filterMode = LiveTvChannelFilterMode.All,
            selectedCategoryName = null,
            searchQuery = searchQuery,
            uncategorizedGroupName = uncategorizedLabel,
        )
        val recentIds = uiState.recentChannelIds.toSet()
        LiveTvChannelChipCounts(
            all = searched.size,
            recents = searched.count { it.id in recentIds },
            favorites = searched.count { it.id in uiState.favoriteChannelIds },
            categories = searched
                .groupingBy { categoryNameForChannel(it, uncategorizedLabel).lowercase() }
                .eachCount(),
        )
    }

    val matchingCategories = remember(categoryChips, searchQuery, channelChipCounts) {
        val normalizedQuery = searchQuery.trim().lowercase()
        if (normalizedQuery.isBlank()) {
            emptyList()
        } else {
            categoryChips
                .filter { it.lowercase().contains(normalizedQuery) }
                .map { it to (channelChipCounts.categories[it.lowercase()] ?: 0) }
                .filter { (_, count) -> count > 0 }
        }
    }

    val playChannel: (LiveTvChannel) -> Unit = { channel ->
        LiveTvRepository.markChannelWatched(channel)
        onChannelClick(channel)
    }

    // Guide ------------------------------------------------------------------------------------
    // The guide is the profile's chosen categories of the curated line-up, plus any channel they added by hand.
    val guideCategoryIds by LiveTvChannelPrefs.guideCategories.collectAsStateWithLifecycle()
    val addedGuideIds by GuideChannelStore.selected.collectAsStateWithLifecycle()
    val guideMatches by produceState(
        initialValue = emptyMap<String, List<LiveTvChannel>>(),
        shownChannels,
        uiState.epgPrograms,
    ) {
        val channels = shownChannels
        val programs = uiState.epgPrograms
        value = withContext(Dispatchers.Default) {
            matchLiveTvGuideCatalog(channels) { !programs[it.id].isNullOrEmpty() }
        }
    }
    val addedGuideChannels = remember(shownChannels, addedGuideIds) {
        if (addedGuideIds.isEmpty()) emptyList() else shownChannels.filter { it.id in addedGuideIds }
    }
    val guideCategoryChips = remember(guideMatches, guideCategoryIds) {
        LIVE_TV_GUIDE_CATEGORIES
            .filter { it.id in guideCategoryIds && !guideMatches[it.id].isNullOrEmpty() }
            .map { Triple(it.id, it.title, guideMatches[it.id].orEmpty().size) }
    }
    LaunchedEffect(guideCategoryChips, addedGuideChannels.isEmpty(), guideFilter) {
        val filter = guideFilter ?: return@LaunchedEffect
        val stillThere = if (filter == LIVE_TV_GUIDE_MINE) addedGuideChannels.isNotEmpty() else guideCategoryChips.any { it.first == filter }
        if (!stillThere && guideMatches.isNotEmpty()) guideFilter = null
    }
    val allGuideChannels = remember(guideMatches, guideCategoryIds, addedGuideChannels) {
        liveTvGuideChannels(guideMatches, guideCategoryIds, addedGuideChannels)
    }
    val guideChannels = remember(guideMatches, guideCategoryIds, addedGuideChannels, guideFilter, searchQuery) {
        val list = liveTvGuideChannels(guideMatches, guideCategoryIds, addedGuideChannels, guideFilter)
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) list else list.filter { it.name.lowercase().contains(query) }
    }
    LaunchedEffect(section, guideWindowStart, guideChannels.isNotEmpty()) {
        if (section != LiveTvSection.Guide || guideChannels.isEmpty()) return@LaunchedEffect
        withTimeoutOrNull(3_000L) {
            snapshotFlow { guideScrollState.maxValue }.first { it in 1 until Int.MAX_VALUE }
        }
        val minutesBeforeNow = ((epgNowMillis - guideWindowStart) / 60_000L - 15L).coerceAtLeast(0L)
        val target = with(guideDensity) { (LiveTvGuideDpPerMinute * minutesBeforeNow.toFloat()).roundToPx() }
        guideScrollState.scrollTo(target)
    }

    // Home ---------------------------------------------------------------------------------------
    val homeShelves = remember(shownChannels, uiState.favoriteChannelIds, uiState.recentChannelIds, guideMatches, guideCategoryIds, addedGuideChannels, uiState.epgPrograms, epgNowMillis / 60_000L) {
        val byId = shownChannels.associateBy { it.id }
        val airingFirst: (List<LiveTvChannel>) -> List<LiveTvChannel> = { list ->
            list.sortedByDescending { uiState.epgPrograms[it.id]?.currentProgram(epgNowMillis) != null }
        }
        buildList {
            val recents = uiState.recentChannelIds.mapNotNull { byId[it] }.take(20)
            if (recents.isNotEmpty()) {
                add(LiveTvHomeShelf("recents", "Recently Watched", recents) {
                    filterMode = LiveTvChannelFilterMode.Recents
                    selectedCategoryName = null
                })
            }
            val favorites = shownChannels.filter { it.id in uiState.favoriteChannelIds }.sortedBy { it.name.lowercase() }
            if (favorites.isNotEmpty()) {
                add(LiveTvHomeShelf("favorites", "Favourites", airingFirst(favorites).take(30)) {
                    filterMode = LiveTvChannelFilterMode.Favorites
                    selectedCategoryName = null
                })
            }
            if (addedGuideChannels.isNotEmpty()) add(LiveTvHomeShelf("added", "Your Picks", airingFirst(addedGuideChannels), null))
            LIVE_TV_GUIDE_CATEGORIES.forEach { category ->
                if (category.id !in guideCategoryIds) return@forEach
                val channels = guideMatches[category.id].orEmpty()
                if (channels.isNotEmpty()) add(LiveTvHomeShelf(category.id, category.title, airingFirst(channels), null))
            }
        }
    }
    val homeHero = remember(lastWatchedChannel, homeShelves, uiState.epgPrograms, epgNowMillis / 60_000L) {
        lastWatchedChannel ?: homeShelves.firstNotNullOfOrNull { shelf ->
            shelf.channels.firstOrNull { uiState.epgPrograms[it.id]?.currentProgram(epgNowMillis) != null }
        }
    }

    // Sports -----------------------------------------------------------------------------------
    LaunchedEffect(section) {
        if (section != LiveTvSection.Sports) return@LaunchedEffect
        while (true) {
            LiveTvSportsRepository.refreshIfStale()
            delay(30_000L)
        }
    }
    val sportsMatches by produceState(
        initialValue = emptyMap<String, List<LiveTvChannel>>(),
        sportsState.events,
        uiState.channels,
        section == LiveTvSection.Sports,
    ) {
        if (section != LiveTvSection.Sports) return@produceState
        value = withContext(Dispatchers.Default) {
            val normalized = uiState.channels.map { it to normalizeSportsName(it.name) }
            sportsState.events.associate { event ->
                "${event.league.id}:${event.id}" to matchSportsChannels(event, normalized)
            }
        }
    }
    val sportsLeagues = remember(sportsState.events) {
        val present = sportsState.events.map { it.league.id }.toSet()
        liveTvSportsLeagues.filter { it.id in present }
    }
    val sportsChipCounts = remember(sportsState.events) {
        buildMap {
            put(LIVE_TV_SPORTS_FILTER_ALL, sportsState.events.size)
            put(LIVE_TV_SPORTS_FILTER_LIVE, sportsState.events.count { it.state == LiveTvSportsState.Live })
            sportsState.events.groupingBy { it.league.id }.eachCount().forEach { (id, count) -> put(id, count) }
        }
    }
    val hasLiveSports = sportsState.events.any { it.state == LiveTvSportsState.Live }
    val visibleSports = remember(sportsState.events, sportsFilter) {
        sportsState.events.filter { event ->
            when (sportsFilter) {
                LIVE_TV_SPORTS_FILTER_ALL -> true
                LIVE_TV_SPORTS_FILTER_LIVE -> event.state == LiveTvSportsState.Live
                else -> event.league.id == sportsFilter
            }
        }
    }
    LaunchedEffect(sportsLeagues, hasLiveSports, sportsFilter) {
        val valid = sportsFilter == LIVE_TV_SPORTS_FILTER_ALL ||
            (sportsFilter == LIVE_TV_SPORTS_FILTER_LIVE && hasLiveSports) ||
            sportsLeagues.any { it.id == sportsFilter }
        if (!valid && sportsState.hasLoaded) sportsFilter = LIVE_TV_SPORTS_FILTER_ALL
    }

    // On Demand --------------------------------------------------------------------------------
    val hasVodProvider = uiState.xtreamSettings.isEnabled && uiState.xtreamSettings.isConfigured
    LaunchedEffect(section, vodKind, hasVodProvider) {
        if (section == LiveTvSection.OnDemand && hasVodProvider) LiveTvOnDemandRepository.ensureLoaded(vodKind)
    }
    val vodCatalog = onDemandState.catalog(vodKind)
    val vodChipCounts = remember(vodCatalog.catalog.items, searchQuery) {
        val searched = filterLiveTvVodItems(vodCatalog.catalog.items, null, searchQuery)
        searched.groupingBy { it.categoryId.orEmpty() }.eachCount() + (LIVE_TV_VOD_COUNT_ALL to searched.size)
    }
    val visibleVod = remember(vodCatalog.catalog.items, vodCategoryId, searchQuery) {
        filterLiveTvVodItems(vodCatalog.catalog.items, vodCategoryId, searchQuery)
    }

    LaunchedEffect(scrollToTopRequests) {
        scrollToTopRequests.collect {
            listState.animateScrollToItem(0)
            titleCollapse.expand()
        }
    }

    LaunchedEffect(searchQuery, filterMode, selectedCategoryName, section, vodKind, vodCategoryId, sportsFilter) {
        listState.scrollToItem(0)
    }

    LaunchedEffect(categoryOptions, filterMode, selectedCategoryName) {
        if (filterMode == LiveTvChannelFilterMode.Category && selectedCategoryName != null) {
            val selectedStillExists = categoryOptions.any { option ->
                option.mode == LiveTvChannelFilterMode.Category && option.categoryName == selectedCategoryName
            }
            if (!selectedStillExists) {
                filterMode = LiveTvChannelFilterMode.Home
                selectedCategoryName = null
            }
        }
    }

    val tokens = MaterialTheme.nuvio
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val screenBackground = if (LocalDynamicArtworkBackgroundActive.current) {
        Color.Transparent
    } else {
        tokens.colors.background
    }
    val listTopPadding = statusBarTop + nuvioPlatformExtraTopPadding

    val density = LocalDensity.current
    val controlsTopPadding by remember(listTopPadding) {
        derivedStateOf {
            val offset = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key == ChannelControlsKey }
                ?.offset
                ?: return@derivedStateOf listTopPadding
            (listTopPadding - with(density) { offset.toDp() }).coerceIn(0.dp, listTopPadding)
        }
    }

    val showChannelControls = !(uiState.isLoading && uiState.channels.isEmpty()) &&
        uiState.hasPlaylist &&
        !(!uiState.errorMessage.isNullOrBlank() && uiState.channels.isEmpty()) &&
        uiState.channels.isNotEmpty()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val gridColumns = ((maxWidth - LiveTvScreenHorizontalPadding * 2) / LiveTvGridMinCellWidth)
        .toInt()
        .coerceIn(3, 8)
    val vodColumns = ((maxWidth - LiveTvScreenHorizontalPadding * 2) / LiveTvVodMinCellWidth)
        .toInt()
        .coerceIn(3, 8)
    val searchAvailable = when (section) {
        LiveTvSection.Channels, LiveTvSection.Guide -> showChannelControls
        LiveTvSection.OnDemand -> hasVodProvider
        LiveTvSection.Sports -> false
    }
        NuvioScreen(
            modifier = Modifier.fillMaxSize().nestedScroll(titleCollapse.connection),
            horizontalPadding = LiveTvScreenHorizontalPadding,
            topPadding = 0.dp,
            listState = listState,
            autoHidesNativeTabBar = true,
        ) {
            stickyHeader(key = ChannelControlsKey) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nuvioScreenSideBleed()
                        .appBackgroundCover(screenBackground)
                        .liveTvBlockPointerPassThrough()
                        .padding(horizontal = LiveTvScreenHorizontalPadding)
                        .padding(top = listTopPadding),
                ) {
                    NuvioScreenHeader(
                        title = stringResource(Res.string.live_tv_title),
                        includeStatusBarPadding = false,
                        modifier = Modifier.collapsingTitle(titleCollapse),
                        actions = {
                            if (searchAvailable) {
                                NuvioIconActionButton(
                                    icon = if (isSearchVisible) Icons.Rounded.Close else Icons.Rounded.Search,
                                    contentDescription = stringResource(Res.string.live_tv_action_search),
                                    onClick = {
                                        if (isSearchVisible) searchQuery = ""
                                        isSearchVisible = !isSearchVisible
                                    },
                                )
                            }
                            if (showChannelControls && section == LiveTvSection.Channels) {
                                NuvioIconActionButton(
                                    icon = if (uiState.isChannelGridEnabled) {
                                        Icons.AutoMirrored.Rounded.ViewList
                                    } else {
                                        Icons.Rounded.GridView
                                    },
                                    contentDescription = stringResource(
                                        if (uiState.isChannelGridEnabled) {
                                            Res.string.live_tv_action_list_view
                                        } else {
                                            Res.string.live_tv_action_grid_view
                                        },
                                    ),
                                    onClick = {
                                        LiveTvRepository.setChannelGridEnabled(!uiState.isChannelGridEnabled)
                                    },
                                )
                            }
                            if (uiState.hasPlaylist) {
                                NuvioIconActionButton(
                                    icon = Icons.Rounded.Refresh,
                                    contentDescription = stringResource(Res.string.action_retry),
                                    onClick = {
                                        when (section) {
                                            LiveTvSection.Channels -> LiveTvRepository.refresh()
                                            LiveTvSection.Guide -> LiveTvRepository.reloadEpg()
                                            LiveTvSection.Sports -> LiveTvSportsRepository.refresh()
                                            LiveTvSection.OnDemand -> LiveTvOnDemandRepository.refresh(vodKind)
                                        }
                                    },
                                )
                            }
                        },
                    )
                    // Once the title has folded away the switch sits level with the profile button, so it makes room.
                    val profileRoom = if (com.nuvio.app.navigation.LocalReserveProfileButton.current) 52.dp * titleCollapse.fraction else 0.dp
                    Box(Modifier.padding(end = profileRoom)) { segments?.invoke() }
                    if (segments == null && uiState.hasPlaylist) {
                        LiveTvSectionTabs(
                            selected = section,
                            onSelected = { selected ->
                                if (selected != section) {
                                    searchQuery = ""
                                    isSearchVisible = false
                                    section = selected
                                }
                            },
                        )
                    }
                    if (showChannelControls && section == LiveTvSection.Guide) {
                        LiveTvGuideControls(
                            searchVisible = isSearchVisible,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            allLabel = allChannelsLabel,
                            allCount = allGuideChannels.size,
                            categories = guideCategoryChips,
                            addedCount = addedGuideChannels.size,
                            selected = guideFilter,
                            onSelected = { guideFilter = it },
                            onAddChannel = { showGuideChannelPicker = true },
                            onCustomize = { showGuideCategories = true },
                        )
                    }
                    if (showChannelControls && section == LiveTvSection.Channels) {
                        LiveTvChannelControls(
                            searchVisible = isSearchVisible,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            filterMode = filterMode,
                            selectedCategoryName = selectedCategoryName,
                            allLabel = allChannelsLabel,
                            favoritesLabel = favoritesLabel,
                            showRecents = hasRecents,
                            showFavorites = hasFavorites,
                            categories = categoryChips,
                            counts = channelChipCounts,
                            onHomeSelected = {
                                filterMode = LiveTvChannelFilterMode.Home
                                selectedCategoryName = null
                            },
                            onAllSelected = {
                                filterMode = LiveTvChannelFilterMode.All
                                selectedCategoryName = null
                            },
                            onRecentsSelected = {
                                filterMode = LiveTvChannelFilterMode.Recents
                                selectedCategoryName = null
                            },
                            onClearRecents = LiveTvRepository::clearRecentChannels,
                            onFavoritesSelected = {
                                filterMode = LiveTvChannelFilterMode.Favorites
                                selectedCategoryName = null
                            },
                            onGroupSelected = { category ->
                                filterMode = LiveTvChannelFilterMode.Category
                                selectedCategoryName = category
                            },
                            onCustomize = { showCategoryManager = true },
                        )
                    }
                    if (section == LiveTvSection.Guide && guideChannels.isNotEmpty()) {
                        LiveTvGuideTimeRuler(
                            scrollState = guideScrollState,
                            windowStart = guideWindowStart,
                            nowMillis = epgNowMillis,
                        )
                    }
                    if (section == LiveTvSection.Sports && sportsState.events.isNotEmpty()) {
                        LiveTvSportsControls(
                            allLabel = allChannelsLabel,
                            liveLabel = stringResource(Res.string.live_tv_sports_filter_live),
                            leagues = sportsLeagues,
                            hasLive = hasLiveSports,
                            selectedFilter = sportsFilter,
                            counts = sportsChipCounts,
                            onFilterSelected = { sportsFilter = it },
                        )
                    }
                    if (section == LiveTvSection.OnDemand && hasVodProvider) {
                        AnimatedVisibility(visible = isSearchVisible) {
                            LiveTvSearchField(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                placeholder = stringResource(Res.string.live_tv_on_demand_search_placeholder),
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                        LiveTvOnDemandControls(
                            kind = vodKind,
                            moviesLabel = stringResource(Res.string.live_tv_on_demand_movies),
                            seriesLabel = stringResource(Res.string.live_tv_on_demand_series),
                            allLabel = allChannelsLabel,
                            categories = vodCatalog.catalog.categories,
                            selectedCategoryId = vodCategoryId,
                            counts = vodChipCounts,
                            onKindSelected = { kind ->
                                if (kind != vodKind) {
                                    vodKind = kind
                                    vodCategoryId = null
                                }
                            },
                            onCategorySelected = { vodCategoryId = it },
                        )
                    }
                }
            }

            if (uiState.hasPlaylist && section == LiveTvSection.Guide && uiState.channels.isNotEmpty()) {
                when {
                    guideChannels.isNotEmpty() -> liveTvGuideRows(
                        channels = guideChannels,
                        epgPrograms = uiState.epgPrograms,
                        scrollState = guideScrollState,
                        windowStart = guideWindowStart,
                        nowMillis = epgNowMillis,
                        onChannelClick = playChannel,
                        onProgramClick = { channel, program ->
                            if (program.isAiringAt(epgNowMillis)) playChannel(channel) else guideChannelId = channel.id
                        },
                    )

                    uiState.isEpgLoading && allGuideChannels.isEmpty() -> liveTvLoadingItem()

                    allGuideChannels.isNotEmpty() -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_no_matching_channels_title),
                            message = "Nothing in your guide matches. Use + to add a channel that isn't in it.",
                        )
                    }

                    else -> item {
                        HomeEmptyStateCard(
                            title = "Your guide is empty",
                            message = "Choose the categories you want to see, or use + to add channels one by one.",
                            actionLabel = "Choose Categories",
                            onActionClick = { showGuideCategories = true },
                        )
                    }
                }
            } else if (uiState.hasPlaylist && section == LiveTvSection.Sports) {
                when {
                    sportsState.events.isEmpty() && (sportsState.isLoading || !sportsState.hasLoaded) -> liveTvLoadingItem()

                    sportsState.events.isEmpty() && sportsState.errorMessage != null -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_sports_load_failed),
                            message = sportsState.errorMessage.orEmpty(),
                            actionLabel = stringResource(Res.string.action_retry),
                            onActionClick = LiveTvSportsRepository::refresh,
                        )
                    }

                    visibleSports.isEmpty() -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_sports_empty_title),
                            message = stringResource(Res.string.live_tv_sports_empty_message),
                        )
                    }

                    else -> liveTvSportsList(
                        events = visibleSports,
                        matchedChannels = sportsMatches,
                        nowMillis = epgNowMillis,
                        onChannelClick = playChannel,
                    )
                }
            } else if (uiState.hasPlaylist && section == LiveTvSection.OnDemand) {
                when {
                    !hasVodProvider -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_on_demand_unavailable_title),
                            message = stringResource(Res.string.live_tv_on_demand_unavailable_message),
                        )
                    }

                    vodCatalog.catalog.items.isEmpty() && (vodCatalog.isLoading || !vodCatalog.hasLoaded) -> liveTvLoadingItem()

                    vodCatalog.catalog.items.isEmpty() && vodCatalog.errorMessage != null -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_on_demand_load_failed),
                            message = vodCatalog.errorMessage.orEmpty(),
                            actionLabel = stringResource(Res.string.action_retry),
                            onActionClick = { LiveTvOnDemandRepository.refresh(vodKind) },
                        )
                    }

                    visibleVod.isEmpty() -> item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_on_demand_empty_title),
                            message = stringResource(Res.string.live_tv_on_demand_empty_message),
                        )
                    }

                    else -> liveTvVodGrid(
                        items = visibleVod,
                        columns = vodColumns,
                        gap = LiveTvGridGap,
                        onItemClick = { item ->
                            when (item.kind) {
                                LiveTvVodKind.Movies -> LiveTvOnDemandRepository.moviePlayback(item)?.let(onVodClick)
                                LiveTvVodKind.Series -> seriesSheetItemId = item.id
                            }
                        },
                    )
                }
            } else when {
                uiState.isLoading && uiState.channels.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.nuvio.colors.accent,
                                strokeWidth = 2.dp,
                            )
                        }
                    }
                }

                !uiState.hasPlaylist -> {
                    item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_no_playlist_title),
                            message = stringResource(Res.string.live_tv_no_playlist_message),
                        )
                    }
                }

                !uiState.errorMessage.isNullOrBlank() && uiState.channels.isEmpty() -> {
                    item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_load_failed),
                            message = uiState.errorMessage.orEmpty(),
                            actionLabel = stringResource(Res.string.action_retry),
                            onActionClick = LiveTvRepository::refresh,
                        )
                    }
                }

                uiState.channels.isEmpty() -> {
                    item {
                        HomeEmptyStateCard(
                            title = stringResource(Res.string.live_tv_empty_title),
                            message = stringResource(Res.string.live_tv_empty_message),
                            actionLabel = stringResource(Res.string.action_retry),
                            onActionClick = LiveTvRepository::refresh,
                        )
                    }
                }

                filterMode == LiveTvChannelFilterMode.Home && searchQuery.isBlank() -> {
                    liveTvHome(
                        hero = homeHero,
                        heroIsResume = homeHero != null && homeHero.id == lastWatchedChannel?.id,
                        shelves = homeShelves,
                        epgPrograms = uiState.epgPrograms,
                        nowMillis = epgNowMillis,
                        favoriteChannelIds = uiState.favoriteChannelIds,
                        totalChannels = shownChannels.size,
                        onPlay = playChannel,
                        onGuide = { channel -> guideChannelId = channel.id },
                        onBrowseAll = {
                            filterMode = LiveTvChannelFilterMode.All
                            selectedCategoryName = null
                        },
                    )
                }

                else -> {
                    if (matchingCategories.isNotEmpty()) {
                        liveTvCategorySearchResults(
                            categories = matchingCategories,
                            showChannelsHeader = visibleChannels.isNotEmpty(),
                            onCategoryClick = { category ->
                                filterMode = LiveTvChannelFilterMode.Category
                                selectedCategoryName = category
                                searchQuery = ""
                                isSearchVisible = false
                            },
                        )
                    }
                    if (visibleChannels.isEmpty()) {
                        if (matchingCategories.isEmpty()) {
                            item {
                                HomeEmptyStateCard(
                                    title = stringResource(Res.string.live_tv_no_matching_channels_title),
                                    message = stringResource(Res.string.live_tv_no_matching_channels_message),
                                )
                            }
                        }
                    } else if (uiState.isChannelGridEnabled) {
                        liveTvChannelGrid(
                            channels = visibleChannels,
                            columns = gridColumns,
                            favoriteChannelIds = uiState.favoriteChannelIds,
                            onFavoriteClick = { channel -> LiveTvRepository.toggleFavoriteChannel(channel.id) },
                            onPlayClick = playChannel,
                            epgPrograms = uiState.epgPrograms,
                            nowMillis = epgNowMillis,
                        )
                    } else {
                        liveTvChannelList(
                            channels = visibleChannels,
                            favoriteChannelIds = uiState.favoriteChannelIds,
                            uncategorizedGroupName = uncategorizedLabel,
                            onFavoriteClick = { channel -> LiveTvRepository.toggleFavoriteChannel(channel.id) },
                            onPlayClick = playChannel,
                            epgPrograms = uiState.epgPrograms,
                            nowMillis = epgNowMillis,
                            onGuideClick = { channel -> guideChannelId = channel.id },
                        )
                    }
                }
            }
        }

        if (showCategoryManager) {
            LiveTvCategoryManagerDialog(
                channels = uiState.channels,
                uncategorizedLabel = uncategorizedLabel,
                onDismiss = { showCategoryManager = false },
            )
        }
        if (showGuideCategories) {
            LiveTvGuideCategoriesDialog(matches = guideMatches, onDismiss = { showGuideCategories = false })
        }
        if (showGuideChannelPicker) {
            GuideChannelPicker(
                channels = shownChannels,
                hasGuide = { !uiState.epgPrograms[it.id].isNullOrEmpty() },
                title = "Add Channels",
                subtitle = "Pick any channel to add it to your guide, next to your categories.",
                confirmLabel = "Done",
                onDismiss = { showGuideChannelPicker = false },
            )
        }

        val guideChannel = guideChannelId?.let { channelId -> uiState.channels.firstOrNull { it.id == channelId } }
        if (guideChannel != null) {
            LiveTvEpgGuideSheet(
                channel = guideChannel,
                programs = uiState.epgPrograms[guideChannel.id].orEmpty(),
                nowMillis = epgNowMillis,
                onPlayClick = {
                    guideChannelId = null
                    playChannel(guideChannel)
                },
                onDismiss = { guideChannelId = null },
            )
        }

        val seriesSheetItem = seriesSheetItemId?.let { id ->
            onDemandState.series.catalog.items.firstOrNull { it.id == id }
        }
        if (seriesSheetItem != null) {
            LiveTvSeriesSheet(
                series = seriesSheetItem,
                onEpisodeClick = { episode ->
                    onVodClick(LiveTvOnDemandRepository.episodePlayback(seriesSheetItem, episode))
                },
                onDismiss = { seriesSheetItemId = null },
            )
        }
    }
}

private fun LazyListScope.liveTvLoadingItem() {
    item(key = "live-tv-loading") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 64.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.nuvio.colors.accent,
                strokeWidth = 2.dp,
            )
        }
    }
}

private const val ChannelControlsKey = "channel-controls"

private fun Modifier.liveTvBlockPointerPassThrough(): Modifier =
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent()
            }
        }
    }

private val LiveTvScreenHorizontalPadding = 16.dp
private val LiveTvGridMinCellWidth = 108.dp
private val LiveTvGridGap = 10.dp

private fun LazyListScope.liveTvChannelGrid(
    channels: List<LiveTvChannel>,
    columns: Int,
    favoriteChannelIds: Set<String>,
    onFavoriteClick: (LiveTvChannel) -> Unit,
    onPlayClick: (LiveTvChannel) -> Unit,
    epgPrograms: Map<String, List<LiveTvEpgProgram>>,
    nowMillis: Long,
) {
    val rows = channels.chunked(columns)
    items(
        count = rows.size,
        key = { index -> "channel-grid:$columns:${rows[index].first().id}" },
    ) { index ->
        val row = rows[index]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LiveTvGridGap),
        ) {
            row.forEach { channel ->
                LiveTvChannelGridCell(
                    channel = channel,
                    isFavorite = channel.id in favoriteChannelIds,
                    currentProgramTitle = epgPrograms[channel.id]
                        ?.firstOrNull { it.isAiringAt(nowMillis) }
                        ?.title,
                    onFavoriteClick = { onFavoriteClick(channel) },
                    onPlayClick = { onPlayClick(channel) },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(columns - row.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LiveTvChannelGridCell(
    channel: LiveTvChannel,
    isFavorite: Boolean,
    currentProgramTitle: String?,
    onFavoriteClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = MaterialTheme.nuvio
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onPlayClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LiveTvLogoPlate(channel, Modifier.fillMaxWidth().aspectRatio(16f / 10f), radius = 12.dp, logoFraction = 0.62f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = if (isFavorite) tokens.colors.warning else Color.White.copy(alpha = 0.55f),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = liveTvDisplayName(channel.name),
                style = MaterialTheme.typography.labelLarge,
                color = tokens.colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = currentProgramTitle?.let { liveTvProgramTitle(it).first }.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LiveTvSectionTabs(
    selected: LiveTvSection,
    onSelected: (LiveTvSection) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 4.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiveTvSection.entries.forEach { item ->
            com.nuvio.app.core.ui.PillChip(
                label = stringResource(item.label),
                selected = item == selected,
                onClick = { onSelected(item) },
            )
        }
    }
}

@Composable
private fun LiveTvChannelControls(
    searchVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterMode: LiveTvChannelFilterMode,
    selectedCategoryName: String?,
    allLabel: String,
    favoritesLabel: String,
    showRecents: Boolean,
    showFavorites: Boolean,
    categories: List<String>,
    counts: LiveTvChannelChipCounts,
    onHomeSelected: () -> Unit,
    onAllSelected: () -> Unit,
    onRecentsSelected: () -> Unit,
    onClearRecents: () -> Unit,
    onFavoritesSelected: () -> Unit,
    onGroupSelected: (String) -> Unit,
    onCustomize: () -> Unit,
) {
    val tokens = MaterialTheme.nuvio
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.listGap),
    ) {
        AnimatedVisibility(visible = searchVisible) {
            LiveTvSearchField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // First in the row, so hiding categories never needs a scroll to the far end of 450 chips.
            LiveTvControlIconButton(Icons.Rounded.Tune, "Hide or show categories", onCustomize, atStart = true)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(NuvioTokens.Space.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiveTvPanelFilterChip(
                    label = "For You",
                    selected = filterMode == LiveTvChannelFilterMode.Home,
                    onClick = onHomeSelected,
                )
                LiveTvPanelFilterChip(
                    label = allLabel,
                    selected = filterMode == LiveTvChannelFilterMode.All,
                    onClick = onAllSelected,
                    count = counts.all,
                )
                if (showRecents) {
                    LiveTvPanelFilterChip(
                        label = stringResource(Res.string.live_tv_filter_recents),
                        selected = filterMode == LiveTvChannelFilterMode.Recents,
                        onClick = onRecentsSelected,
                        count = counts.recents,
                    )
                    if (filterMode == LiveTvChannelFilterMode.Recents) {
                        LiveTvPanelFilterChip(
                            label = stringResource(Res.string.live_tv_action_clear_recents),
                            selected = false,
                            onClick = onClearRecents,
                        )
                    }
                }
                if (showFavorites) {
                    LiveTvPanelFilterChip(
                        label = favoritesLabel,
                        selected = filterMode == LiveTvChannelFilterMode.Favorites,
                        onClick = onFavoritesSelected,
                        count = counts.favorites,
                    )
                }
                categories.forEach { category ->
                    LiveTvPanelFilterChip(
                        label = category,
                        selected = filterMode == LiveTvChannelFilterMode.Category &&
                            selectedCategoryName.equals(category, ignoreCase = true),
                        onClick = { onGroupSelected(category) },
                        count = counts.categories[category.lowercase()] ?: 0,
                    )
                }
            }
        }
    }
}

/** A round button that stays put at the end of a chip row while the chips scroll. */
@Composable
private fun LiveTvControlIconButton(icon: ImageVector, description: String, onClick: () -> Unit, atStart: Boolean = false) {
    val tokens = MaterialTheme.nuvio
    Box(
        modifier = Modifier
            .padding(start = if (atStart) 0.dp else NuvioTokens.Space.s8, end = if (atStart) NuvioTokens.Space.s8 else 0.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(tokens.colors.textPrimary.copy(alpha = 0.12f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = tokens.colors.textPrimary, modifier = Modifier.size(20.dp))
    }
}

/** The Guide's chips: the profile's categories and the channels they added, then + and Customize. */
@Composable
private fun LiveTvGuideControls(
    searchVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    allLabel: String,
    allCount: Int,
    categories: List<Triple<String, String, Int>>,
    addedCount: Int,
    selected: String?,
    onSelected: (String?) -> Unit,
    onAddChannel: () -> Unit,
    onCustomize: () -> Unit,
) {
    val tokens = MaterialTheme.nuvio
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.listGap),
    ) {
        AnimatedVisibility(visible = searchVisible) {
            LiveTvSearchField(query = searchQuery, onQueryChange = onSearchQueryChange)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(NuvioTokens.Space.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiveTvPanelFilterChip(label = "My Guide", selected = selected == null, onClick = { onSelected(null) }, count = allCount)
                categories.forEach { (id, title, count) ->
                    LiveTvPanelFilterChip(label = title, selected = selected == id, onClick = { onSelected(id) }, count = count)
                }
                if (addedCount > 0) {
                    LiveTvPanelFilterChip(
                        label = "Added by Me",
                        selected = selected == LIVE_TV_GUIDE_MINE,
                        onClick = { onSelected(LIVE_TV_GUIDE_MINE) },
                        count = addedCount,
                    )
                }
            }
            LiveTvControlIconButton(Icons.Rounded.Add, "Add a channel to your guide", onAddChannel)
            LiveTvControlIconButton(Icons.Rounded.Tune, "Choose guide categories", onCustomize)
        }
    }
}

@Composable
private fun LiveTvSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(Res.string.live_tv_search_placeholder),
) {
    Box(modifier = modifier.fillMaxWidth()) {
        NuvioInputField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = placeholder,
            trailingContent = {
                if (query.isBlank()) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.nuvio.colors.textMuted,
                    )
                } else {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = MaterialTheme.nuvio.colors.textMuted,
                        )
                    }
                }
            },
        )
    }
}

private fun LazyListScope.liveTvCategorySearchResults(
    categories: List<Pair<String, Int>>,
    showChannelsHeader: Boolean,
    onCategoryClick: (String) -> Unit,
) {
    item(key = "category_results_header") {
        LiveTvSearchResultsHeader(text = stringResource(Res.string.live_tv_categories))
    }
    items(
        items = categories,
        key = { (category, _) -> "category_result:${category.lowercase()}" },
    ) { (category, count) ->
        LiveTvCategoryResultCard(
            categoryName = category,
            channelCount = count,
            onClick = { onCategoryClick(category) },
        )
    }
    if (showChannelsHeader) {
        item(key = "channel_results_header") {
            LiveTvSearchResultsHeader(text = stringResource(Res.string.live_tv_section_channels))
        }
    }
}

@Composable
private fun LiveTvSearchResultsHeader(text: String) {
    val tokens = MaterialTheme.nuvio
    Text(
        text = text,
        modifier = Modifier.padding(top = NuvioTokens.Space.s8, bottom = 2.dp),
        style = MaterialTheme.typography.labelLarge,
        color = tokens.colors.textMuted,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun LiveTvCategoryResultCard(
    categoryName: String,
    channelCount: Int,
    onClick: () -> Unit,
) {
    val tokens = MaterialTheme.nuvio
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = tokens.colors.surface,
        shape = tokens.shapes.compactCard,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(tokens.shapes.avatar)
                    .background(tokens.colors.overlaySelected),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Folder,
                    contentDescription = null,
                    tint = tokens.colors.accent,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = pluralStringResource(Res.plurals.live_tv_category_channel_count, channelCount, channelCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.colors.textMuted,
                    maxLines = 1,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = tokens.colors.textMuted,
            )
        }
    }
}

private fun LazyListScope.liveTvChannelList(
    channels: List<LiveTvChannel>,
    favoriteChannelIds: Set<String>,
    uncategorizedGroupName: String,
    onFavoriteClick: (LiveTvChannel) -> Unit,
    onPlayClick: (LiveTvChannel) -> Unit,
    epgPrograms: Map<String, List<LiveTvEpgProgram>> = emptyMap(),
    nowMillis: Long = 0L,
    onGuideClick: (LiveTvChannel) -> Unit = {},
) {
    items(
        items = channels,
        key = { channel -> "channel:${channel.id}" },
    ) { channel ->
        LiveTvChannelRow(
            channel = channel,
            categoryName = categoryNameForChannel(channel, uncategorizedGroupName),
            isFavorite = channel.id in favoriteChannelIds,
            programs = epgPrograms[channel.id].orEmpty(),
            nowMillis = nowMillis,
            onPlay = { onPlayClick(channel) },
            onFavorite = { onFavoriteClick(channel) },
            onGuide = { onGuideClick(channel) },
        )
    }
}

private enum class LiveTvSection(val label: StringResource) {
    Channels(Res.string.live_tv_section_channels),
    Guide(Res.string.live_tv_section_guide),
    Sports(Res.string.live_tv_section_sports),
    OnDemand(Res.string.live_tv_section_on_demand),
}

private data class LiveTvChannelChipCounts(
    val all: Int,
    val recents: Int,
    val favorites: Int,
    val categories: Map<String, Int>,
)

private enum class LiveTvChannelFilterMode {
    Home,
    All,
    Recents,
    Favorites,
    Category,
}

private data class LiveTvCategoryFilterOption(
    val label: String,
    val mode: LiveTvChannelFilterMode,
    val categoryName: String? = null,
)

private fun buildLiveTvCategoryFilterOptions(
    channels: List<LiveTvChannel>,
    allChannelsLabel: String,
    favoritesLabel: String,
    uncategorizedLabel: String,
): List<LiveTvCategoryFilterOption> {
    val categories = channels
        .mapNotNull { channel -> channel.group?.trim()?.takeIf(String::isNotBlank) }
        .distinctBy { it.lowercase() }
        .filterNot { category ->
            category.equals(allChannelsLabel, ignoreCase = true) ||
                category.equals(favoritesLabel, ignoreCase = true) ||
                category.equals(uncategorizedLabel, ignoreCase = true)
        }
        .sortedBy { it.lowercase() }

    return buildList {
        add(LiveTvCategoryFilterOption(allChannelsLabel, LiveTvChannelFilterMode.All))
        add(LiveTvCategoryFilterOption(favoritesLabel, LiveTvChannelFilterMode.Favorites))
        add(LiveTvCategoryFilterOption(uncategorizedLabel, LiveTvChannelFilterMode.Category, uncategorizedLabel))
        categories.forEach { category ->
            add(LiveTvCategoryFilterOption(category, LiveTvChannelFilterMode.Category, category))
        }
    }
}

private fun filterLiveTvChannels(
    channels: List<LiveTvChannel>,
    favoriteChannelIds: Set<String>,
    recentChannelIds: List<String>,
    filterMode: LiveTvChannelFilterMode,
    selectedCategoryName: String?,
    searchQuery: String,
    uncategorizedGroupName: String,
): List<LiveTvChannel> {
    val normalizedQuery = searchQuery.trim().lowercase()
    val recentOrder = recentChannelIds.withIndex().associate { (index, id) -> id to index }
    val filtered = channels
        .asSequence()
        .filter { channel ->
            when (filterMode) {
                LiveTvChannelFilterMode.Home, LiveTvChannelFilterMode.All -> true
                LiveTvChannelFilterMode.Recents -> channel.id in recentOrder
                LiveTvChannelFilterMode.Favorites -> channel.id in favoriteChannelIds
                LiveTvChannelFilterMode.Category -> selectedCategoryName == null ||
                    categoryNameForChannel(channel, uncategorizedGroupName).equals(selectedCategoryName, ignoreCase = true)
            }
        }
        .filter { channel ->
            normalizedQuery.isBlank() ||
                channel.name.lowercase().contains(normalizedQuery) ||
                categoryNameForChannel(channel, uncategorizedGroupName).lowercase().contains(normalizedQuery) ||
                channel.playlistName.orEmpty().lowercase().contains(normalizedQuery)
        }
    return if (filterMode == LiveTvChannelFilterMode.Recents) {
        filtered.sortedBy { recentOrder[it.id] ?: Int.MAX_VALUE }.toList()
    } else {
        filtered.sortedBy { it.name.lowercase() }.toList()
    }
}

private fun categoryNameForChannel(
    channel: LiveTvChannel,
    uncategorizedGroupName: String,
): String = channel.group?.trim()?.takeIf(String::isNotBlank) ?: uncategorizedGroupName
