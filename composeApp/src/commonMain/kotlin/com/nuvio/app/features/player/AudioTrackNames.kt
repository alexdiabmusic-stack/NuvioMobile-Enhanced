package com.nuvio.app.features.player

/**
 * Plain names for audio tracks, the way streaming apps show them: "English", "English – Audio Description",
 * "Spanish (Latin America)", "English – Commentary". The stream's own label ("English DDP 5.1 (5.1(side), E-AC-3)")
 * is kept as the technical detail for anyone who turns that on in Settings.
 */
internal data class FriendlyAudioName(val name: String, val detail: String?)

private val COMMENTARY = Regex("""\bcommentar(y|ies)\b|\bdirector'?s?\b""", RegexOption.IGNORE_CASE)
private val DESCRIPTION = Regex("""\baudio ?desc|\bdescriptive\b|\bdescribed\b|\bvisually impaired\b|(^|[\s\[(])AD($|[\s\])])""", RegexOption.IGNORE_CASE)
private val LATIN_AMERICA = Regex("""\blatin|\blatam\b|\blatino\b|\bes-(419|mx)\b""", RegexOption.IGNORE_CASE)
private val BRAZIL = Regex("""\bbrazil|\bbrasil|\bpt-br\b""", RegexOption.IGNORE_CASE)
private val CANADA = Regex("""\bcanad|\bqu[eé]bec|\bfr-ca\b|\bvfq\b""", RegexOption.IGNORE_CASE)
private val ATMOS = Regex("""\batmos\b""", RegexOption.IGNORE_CASE)
private val CHANNELS = Regex("""\b(7\.1|5\.1|2\.0|1\.0|stereo|mono)\b""", RegexOption.IGNORE_CASE)

/** Language names to look for when a track has a label but no language code, e.g. "English DDP 5.1". */
private val NAMED_LANGUAGES = listOf(
    "english", "french", "spanish", "german", "italian", "portuguese", "japanese", "korean", "chinese", "mandarin",
    "cantonese", "hindi", "arabic", "russian", "turkish", "dutch", "polish", "swedish", "norwegian", "danish",
    "finnish", "greek", "hebrew", "thai", "vietnamese", "indonesian", "czech", "hungarian", "romanian", "ukrainian",
    "tamil", "telugu", "malayalam", "bengali", "persian", "catalan", "croatian", "serbian", "bulgarian",
)

internal fun friendlyAudioName(track: AudioTrack, languageLabel: (String) -> String): FriendlyAudioName {
    val raw = track.label.trim()
    val code = track.language?.trim()?.takeIf { it.isNotBlank() && !it.equals("und", true) && !it.equals("unknown", true) }
    val language = code?.let(languageLabel)?.takeIf { it.isNotBlank() }
        ?: NAMED_LANGUAGES.firstOrNull { raw.contains(it, ignoreCase = true) }?.replaceFirstChar { it.uppercase() }

    val region = when {
        LATIN_AMERICA.containsMatchIn(raw) || code.equals("es-419", true) -> "Latin America"
        BRAZIL.containsMatchIn(raw) || code.equals("pt-BR", true) -> "Brazil"
        CANADA.containsMatchIn(raw) || code.equals("fr-CA", true) -> "Canada"
        else -> null
    }
    val kind = when {
        DESCRIPTION.containsMatchIn(raw) -> "Audio Description"
        COMMENTARY.containsMatchIn(raw) -> "Commentary"
        else -> null
    }

    val base = when {
        language != null && region != null -> "$language ($region)"
        language != null -> language
        else -> null
    }
    val name = when {
        base != null && kind != null -> "$base – $kind"
        base != null -> base
        kind != null -> kind
        else -> "Audio ${track.index + 1}"
    }

    val sound = when {
        ATMOS.containsMatchIn(raw) -> "Dolby Atmos"
        else -> when (CHANNELS.find(raw)?.value?.lowercase()) {
            "7.1" -> "7.1 Surround"
            "5.1" -> "5.1 Surround"
            "2.0", "stereo" -> "Stereo"
            "1.0", "mono" -> "Mono"
            else -> null
        }
    }
    return FriendlyAudioName(name = name, detail = sound)
}

/** Friendly names for a whole list; two tracks that would read the same get their sound format, then a number. */
internal fun friendlyAudioNames(tracks: List<AudioTrack>, languageLabel: (String) -> String): Map<Int, FriendlyAudioName> {
    val names = tracks.associate { it.index to friendlyAudioName(it, languageLabel) }.let { found ->
        // A lone track with no language is simply the film's own audio.
        if (tracks.size == 1) found.mapValues { (_, n) -> if (n.name.startsWith("Audio ")) n.copy(name = "Original") else n } else found
    }
    val clashes = names.values.groupingBy { it.name }.eachCount().filterValues { it > 1 }.keys
    if (clashes.isEmpty()) return names
    val seen = HashMap<String, Int>()
    return names.mapValues { (_, n) ->
        if (n.name !in clashes) return@mapValues n
        val withSound = n.detail?.let { "${n.name} · $it" } ?: n.name
        val count = (seen[withSound] ?: 0) + 1
        seen[withSound] = count
        n.copy(name = if (count > 1) "$withSound ($count)" else withSound)
    }
}
