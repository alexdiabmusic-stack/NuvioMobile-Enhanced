package com.nuvio.app.features.search

import androidx.compose.foundation.background
import com.nuvio.app.features.home.components.HomeTop10Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nuvio.app.core.ui.NuvioShelfSection
import com.nuvio.app.core.ui.NuvioTokens
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.core.ui.nuvioSafeBottomPadding
import com.nuvio.app.features.home.MetaPreview
import com.nuvio.app.features.home.components.HomePosterCard
import com.nuvio.app.features.home.components.HomeSkeletonRow
import com.nuvio.app.features.home.components.PosterGridRow
import com.nuvio.app.features.home.components.PosterGridSkeletonRow
import com.nuvio.app.features.home.components.posterGridColumnCountForWidth
import com.nuvio.app.features.home.stableKey
import com.nuvio.app.features.watching.application.WatchingState

/** A page opened from the Search home. They stack: Genres opens a genre's list, and back returns to Genres. */
internal sealed interface SearchPage {
    /** A poster list; with more than one option (Movies / Series) it gets a chip for each. */
    data class Grid(val title: String, val options: List<Pair<String, BrowseSpec>>) : SearchPage
    /** The list of franchises (Movie Universes). */
    data object Franchises : SearchPage
    /** One curated collection page: hero art, then titled sections of posters. */
    data class Collection(val id: String) : SearchPage
    data object Genres : SearchPage
    data object Decades : SearchPage
    data class People(val directors: Boolean) : SearchPage
}

internal fun SearchPage.title(): String = when (this) {
    is SearchPage.Grid -> title
    SearchPage.Franchises -> "Movie Universes"
    is SearchPage.Collection -> COLLECTION_DEFS[id]?.title.orEmpty()
    SearchPage.Genres -> "Genres"
    SearchPage.Decades -> "Decades"
    is SearchPage.People -> if (directors) "Directors" else "Actors"
}

private fun movieAndSeries(title: String, movie: BrowseSpec?, tv: BrowseSpec?) =
    SearchPage.Grid(title, listOfNotNull(movie?.let { "Movies" to it }, tv?.let { "Series" to it }))

/** The Search tab's start page: popular searches, collections, Browse All and what is trending this week. */
internal fun LazyListScope.searchHomeSections(
    onOpen: (SearchPage) -> Unit,
    showLiveTv: Boolean,
    onOpenLiveTv: () -> Unit,
    watchedKeys: Set<String>,
    fullyWatchedSeriesKeys: Set<String>,
    onPosterClick: ((MetaPreview) -> Unit)?,
    onPosterLongClick: ((MetaPreview) -> Unit)?,
) {
    // Every section uses Home's shelf: the same heading, spacing and edge-to-edge rows.
    item(key = "sh-popular") {
        NuvioShelfSection(
            title = "Popular searches",
            entries = SEARCH_POPULAR,
            modifier = Modifier.padding(bottom = SECTION_GAP),
            headerHorizontalPadding = 16.dp,
            rowContentPadding = PaddingValues(horizontal = 16.dp),
            itemSpacing = 8.dp,
            key = { it.key },
        ) { spec ->
            PopularChip(spec.title) { onOpen(SearchPage.Grid(spec.title, listOf("" to spec))) }
        }
    }
    item(key = "sh-collections") {
        NuvioShelfSection(
            title = "Explore Collections",
            entries = SearchCollection.entries,
            modifier = Modifier.padding(bottom = SECTION_GAP),
            headerHorizontalPadding = 16.dp,
            rowContentPadding = PaddingValues(horizontal = 16.dp),
            key = { it.name },
        ) { collection ->
            CollectionCard(collection) {
                onOpen(collection.defId?.let { SearchPage.Collection(it) } ?: SearchPage.Franchises)
            }
        }
    }
    item(key = "sh-browse") {
        val tiles = buildList {
            add(BrowseEntry(Icons.Rounded.Theaters, "Genres", Color(0xFFE5484D)) { onOpen(SearchPage.Genres) })
            add(BrowseEntry(Icons.Rounded.Groups, "Actors", Color(0xFF3E7BFA)) { onOpen(SearchPage.People(directors = false)) })
            add(BrowseEntry(Icons.Rounded.Videocam, "Directors", Color(0xFF8E4EC6)) { onOpen(SearchPage.People(directors = true)) })
            add(BrowseEntry(Icons.Rounded.CalendarMonth, "Decades", Color(0xFFE38A1C)) { onOpen(SearchPage.Decades) })
            if (showLiveTv) add(BrowseEntry(Icons.Rounded.Sensors, "Live TV", Color(0xFF2EA66B), onOpenLiveTv))
        }
        NuvioShelfSection(
            title = "Browse All",
            entries = tiles,
            modifier = Modifier.padding(bottom = SECTION_GAP),
            headerHorizontalPadding = 16.dp,
            rowContentPadding = PaddingValues(horizontal = 16.dp),
            key = { it.label },
        ) { BrowseTile(it) }
    }
    item(key = "sh-trending") {
        val trending by produceState<List<MetaPreview>>(emptyList()) { value = SearchBrowseData.trending() }
        if (trending.isEmpty()) {
            HomeSkeletonRow(modifier = Modifier.padding(horizontal = 16.dp))
        } else {
            // Ranked like Home's Top 10 rows.
            HomeTop10Row(
                items = trending.take(10),
                sectionPadding = 16.dp,
                title = "Trending This Week",
                modifier = Modifier.padding(bottom = SECTION_GAP),
                onPosterClick = onPosterClick,
                onPosterLongClick = onPosterLongClick,
            )
        }
    }
}

private val SECTION_GAP = 18.dp

@Composable
internal fun SearchPill(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) =
    com.nuvio.app.core.ui.PillChip(label, selected, onClick, modifier)

@Composable
private fun PopularChip(label: String, onClick: () -> Unit) {
    val tokens = MaterialTheme.nuvio
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(tokens.colors.textPrimary.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = tokens.colors.textMuted, modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = tokens.colors.textPrimary, maxLines = 1)
    }
}

/** A collection card, sized and shaded like Home's wide cards, with the title over the collection's key art. */
@Composable
private fun CollectionCard(collection: SearchCollection, onClick: () -> Unit) {
    val tokens = MaterialTheme.nuvio
    val cover by produceState<String?>(null, collection) {
        value = SearchBrowseData.backdrop(collection.coverTitle, collection.coverYear, collection.coverIsSeries)
    }
    Box(
        modifier = Modifier
            .width(250.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(tokens.colors.surface)
            .clickable(onClick = onClick),
    ) {
        cover?.let { AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.4f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.85f))))
        Text(
            collection.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        )
    }
}

private class BrowseEntry(val icon: ImageVector, val label: String, val tint: Color, val onClick: () -> Unit)

/** A Browse All tile: a soft colour wash with its icon at the top and its name at the bottom. */
@Composable
private fun BrowseTile(entry: BrowseEntry) {
    val tokens = MaterialTheme.nuvio
    Box(
        modifier = Modifier
            .width(132.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(tokens.colors.surface)
            .background(Brush.linearGradient(listOf(entry.tint.copy(alpha = 0.55f), entry.tint.copy(alpha = 0.12f))))
            .clickable(onClick = entry.onClick)
            .padding(12.dp),
    ) {
        Icon(entry.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Text(
            entry.label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

/** The page a Search home tile opens, under a back arrow. */
@Composable
internal fun SearchBrowsePage(
    page: SearchPage,
    onBack: () -> Unit,
    onOpen: (SearchPage) -> Unit,
    watchedKeys: Set<String>,
    fullyWatchedSeriesKeys: Set<String>,
    onPosterClick: ((MetaPreview) -> Unit)?,
    onPosterLongClick: ((MetaPreview) -> Unit)?,
    onPersonClick: (id: Int, name: String, photo: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = MaterialTheme.nuvio
    Column(modifier.fillMaxSize().background(tokens.colors.background)) {
        // A collection page opens with its own key art and a back button over it, so it has no bar of its own.
        if (page !is SearchPage.Collection) {
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().height(48.dp).clickable(onClick = onBack).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.colors.textPrimary)
                Text(
                    page.title(),
                    color = tokens.colors.textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
        when (page) {
            is SearchPage.Grid -> GridPageContent(page, watchedKeys, fullyWatchedSeriesKeys, onPosterClick, onPosterLongClick)
            SearchPage.Franchises -> FranchiseListContent(onOpen)
            is SearchPage.Collection -> COLLECTION_DEFS[page.id]?.let { def ->
                CollectionPageContent(def, onBack, watchedKeys, fullyWatchedSeriesKeys, onPosterClick, onPosterLongClick)
            }
            SearchPage.Genres -> ListPage(SEARCH_GENRES.map { it.name }) { index ->
                val genre = SEARCH_GENRES[index]
                onOpen(movieAndSeries(genre.name, genreSpec(genre, tv = false), genreSpec(genre, tv = true)))
            }
            SearchPage.Decades -> ListPage(SEARCH_DECADES.map { "${it}s" }) { index ->
                val start = SEARCH_DECADES[index]
                onOpen(movieAndSeries("${start}s", decadeSpec(start, tv = false), decadeSpec(start, tv = true)))
            }
            is SearchPage.People -> PeopleContent(page.directors, onPersonClick)
        }
    }
}

@Composable
private fun GridPageContent(
    page: SearchPage.Grid,
    watchedKeys: Set<String>,
    fullyWatchedSeriesKeys: Set<String>,
    onPosterClick: ((MetaPreview) -> Unit)?,
    onPosterLongClick: ((MetaPreview) -> Unit)?,
) {
    val tokens = MaterialTheme.nuvio
    var selected by rememberSaveable(page.title) { mutableIntStateOf(0) }
    val spec = page.options.getOrNull(selected)?.second
    val items by produceState<List<MetaPreview>?>(null, spec?.key) {
        value = null
        value = spec?.let { SearchBrowseData.list(it) }.orEmpty()
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = posterGridColumnCountForWidth(maxWidth)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = nuvioSafeBottomPadding(NuvioTokens.Space.s16)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (page.options.size > 1) {
                item(key = "chips") {
                    Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        page.options.forEachIndexed { index, (label, _) -> SearchPill(label, selected = selected == index, onClick = { selected = index }) }
                    }
                }
            }
            val list = items
            when {
                list == null -> items(2) { PosterGridSkeletonRow(columns = columns, modifier = Modifier.padding(horizontal = 16.dp)) }
                list.isEmpty() -> item(key = "empty") {
                    Text("Nothing to show right now.", style = MaterialTheme.typography.bodyMedium, color = tokens.colors.textMuted, modifier = Modifier.padding(horizontal = 16.dp))
                }
                else -> items(count = (list.size + columns - 1) / columns, key = { "row-$it" }) { rowIndex ->
                    val first = rowIndex * columns
                    PosterGridRow(
                        items = list.subList(first, minOf(first + columns, list.size)),
                        columns = columns,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        watchedKeys = watchedKeys,
                        fullyWatchedSeriesKeys = fullyWatchedSeriesKeys,
                        onPosterClick = onPosterClick,
                        onPosterLongClick = onPosterLongClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListPage(labels: List<String>, onClick: (Int) -> Unit) {
    val tokens = MaterialTheme.nuvio
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = nuvioSafeBottomPadding(NuvioTokens.Space.s16)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(labels.size, key = { "l-$it" }) { index ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tokens.colors.surface).clickable { onClick(index) }.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(labels[index], style = MaterialTheme.typography.titleMedium, color = tokens.colors.textPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = tokens.colors.textMuted)
            }
        }
    }
}

@Composable
private fun PeopleContent(directors: Boolean, onPersonClick: (id: Int, name: String, photo: String?) -> Unit) {
    val tokens = MaterialTheme.nuvio
    val people by produceState<List<PersonHit>?>(null, directors) {
        value = if (directors) SearchBrowseData.directors() else SearchBrowseData.popularActors()
    }
    val list = people
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = nuvioSafeBottomPadding(NuvioTokens.Space.s16)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when {
            list == null -> item(key = "loading") { HomeSkeletonRow() }
            list.isEmpty() -> item(key = "empty") { Text("Nothing to show right now.", style = MaterialTheme.typography.bodyMedium, color = tokens.colors.textMuted) }
            else -> items(list, key = { "p-${it.id}" }) { person ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tokens.colors.surface)
                        .clickable { onPersonClick(person.id, person.name, person.photo) }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(52.dp).clip(CircleShape).background(tokens.colors.surfaceElevated), contentAlignment = Alignment.Center) {
                        if (person.photo != null) AsyncImage(model = person.photo, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Icon(Icons.Rounded.Person, contentDescription = null, tint = tokens.colors.textMuted)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(person.name, style = MaterialTheme.typography.titleSmall, color = tokens.colors.textPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        person.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = tokens.colors.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = tokens.colors.textMuted)
                }
            }
        }
    }
}

/** The Movie Universes page: one wide card per franchise, each opening its own page. */
@Composable
private fun FranchiseListContent(onOpen: (SearchPage) -> Unit) {
    val tokens = MaterialTheme.nuvio
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = nuvioSafeBottomPadding(NuvioTokens.Space.s16)),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(FRANCHISES, key = { it.id }) { franchise ->
            val cover by produceState<String?>(null, franchise.id) {
                value = SearchBrowseData.backdrop(franchise.heroTitle, franchise.heroYear, franchise.heroIsSeries)
            }
            Box(
                Modifier.fillMaxWidth().height(148.dp).clip(RoundedCornerShape(12.dp)).background(tokens.colors.surface)
                    .clickable { onOpen(SearchPage.Collection(franchise.id)) },
            ) {
                cover?.let { AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.78f)))))
                Text(
                    franchise.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 16.dp, vertical = 14.dp),
                )
            }
        }
    }
}

/**
 * A curated collection page: key art across the top with the title on it, then each section under its own heading
 * in a three-across poster grid. Franchises list their films by hand; the other collections fill their sections from
 * TMDB, with a title shown only once so the sections read as different picks.
 */
@Composable
private fun CollectionPageContent(
    def: CollectionDef,
    onBack: () -> Unit,
    watchedKeys: Set<String>,
    fullyWatchedSeriesKeys: Set<String>,
    onPosterClick: ((MetaPreview) -> Unit)?,
    onPosterLongClick: ((MetaPreview) -> Unit)?,
) {
    var chronological by rememberSaveable(def.id) { mutableStateOf(false) }
    val hero by produceState<String?>(null, def.id) {
        value = SearchBrowseData.backdrop(def.heroTitle, def.heroYear, def.heroIsSeries, size = "w1280")
    }
    val discovered by produceState<List<Pair<String, List<MetaPreview>>>>(emptyList(), def.id) {
        if (def.discoverSections.isEmpty()) return@produceState
        val seen = HashSet<String>()
        val acc = ArrayList<Pair<String, List<MetaPreview>>>()
        // In order, so a title shown in one section isn't repeated in the next; each appears as soon as it is ready.
        for (spec in def.discoverSections) {
            val picks = SearchBrowseData.list(spec, limit = 20).filter { seen.add(it.id) }.take(6)
            if (picks.isNotEmpty()) {
                acc += spec.title to picks
                value = acc.toList()
            }
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = posterGridColumnCountForWidth(maxWidth)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = nuvioSafeBottomPadding(NuvioTokens.Space.s16)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item(key = "hero") { CollectionHero(def.title, hero, onBack) }
            if (def.chronological != null) {
                item(key = "order") {
                    Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SearchPill("Release order", selected = !chronological, onClick = { chronological = false })
                        SearchPill("Chronological order", selected = chronological, onClick = { chronological = true })
                    }
                }
            }
            if (def.discoverSections.isNotEmpty()) {
                if (discovered.isEmpty()) {
                    item(key = "loading") { PosterGridSkeletonRow(columns = columns, modifier = Modifier.padding(horizontal = 16.dp)) }
                }
                items(discovered, key = { "d-" + it.first }) { (title, picks) ->
                    PosterSection(title, picks, columns, watchedKeys, fullyWatchedSeriesKeys, onPosterClick, onPosterLongClick)
                }
            } else {
                val sections = if (chronological && def.chronological != null) def.chronological else def.filmSections
                itemsIndexed(sections, key = { index, section -> "f-$index-${section.title}" }) { _, section ->
                    val films by produceState<List<MetaPreview>?>(null, def.id, section.title) { value = SearchBrowseData.films(section.films) }
                    val list = films
                    if (list == null) {
                        PosterGridSkeletonRow(columns = columns, modifier = Modifier.padding(horizontal = 16.dp))
                    } else if (list.isNotEmpty()) {
                        PosterSection(section.title, list, columns, watchedKeys, fullyWatchedSeriesKeys, onPosterClick, onPosterLongClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionHero(title: String, image: String?, onBack: () -> Unit) {
    val tokens = MaterialTheme.nuvio
    Box(Modifier.fillMaxWidth().height(330.dp).background(tokens.colors.surface)) {
        image?.let { AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.0f to Color.Black.copy(alpha = 0.45f),
                    0.35f to Color.Transparent,
                    1.0f to tokens.colors.background,
                ),
            ),
        )
        Box(
            Modifier.statusBarsPadding().padding(start = 12.dp, top = 8.dp).size(40.dp).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f)).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 3,
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}

/** A heading and its posters, three across, a row at a time. */
@Composable
private fun PosterSection(
    title: String,
    items: List<MetaPreview>,
    columns: Int,
    watchedKeys: Set<String>,
    fullyWatchedSeriesKeys: Set<String>,
    onPosterClick: ((MetaPreview) -> Unit)?,
    onPosterLongClick: ((MetaPreview) -> Unit)?,
) {
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.nuvio.colors.textPrimary)
        items.chunked(columns).forEach { row ->
            PosterGridRow(
                items = row,
                columns = columns,
                watchedKeys = watchedKeys,
                fullyWatchedSeriesKeys = fullyWatchedSeriesKeys,
                onPosterClick = onPosterClick,
                onPosterLongClick = onPosterLongClick,
            )
        }
    }
}
