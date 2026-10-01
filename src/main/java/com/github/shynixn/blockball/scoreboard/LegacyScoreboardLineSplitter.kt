package com.github.shynixn.blockball.scoreboard

/**
 * Pure-data, unit-testable helper that splits a long scoreboard line into
 * the (prefix, entry, suffix) tuple required by 1.8.x clients.
 *
 * 1.8 scoreboard packet limitations:
 *   - objective display name: 32 chars max
 *   - team prefix: 16 chars max
 *   - team entry name: 40 chars max (we keep entries <= 16 for readability)
 *   - team suffix: 16 chars max
 *   - total visible per line: prefix(16) + entry(16) + suffix(16) = 48 chars
 *
 * Additional constraints:
 *   - Two teams cannot share the same entry name (server rejects the packet).
 *     Callers should pad identical lines with invisible color-code suffixes.
 *   - Color codes MUST be carried across the prefix -> entry -> suffix
 *     boundary, otherwise the suffix renders with default color.
 *   - Hex (#aabbcc) colors are NOT supported on 1.8; they must be stripped
 *     or downsampled to the nearest legacy color before splitting.
 *
 * This class has ZERO Bukkit dependencies — it operates on plain Strings —
 * which makes it trivially unit-testable.
 */
object LegacyScoreboardLineSplitter {

    /** 1.8 limit: prefix is 16 chars, suffix is 16 chars, entry is 40 (we cap at 16 for safety). */
    const val MAX_PREFIX = 16
    const val MAX_SUFFIX = 16
    const val MAX_ENTRY = 16

    /** Total visible chars per line on 1.8 (prefix + entry + suffix). */
    const val MAX_TOTAL = MAX_PREFIX + MAX_ENTRY + MAX_SUFFIX

    /** Legacy '&' color codes used to carry color across the split. */
    private val LEGACY_COLOR_CODES = setOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    )

    /** Legacy '&' format codes (reset, bold, italic, underline, strike, magic). */
    private val LEGACY_FORMAT_CODES = setOf('r', 'l', 'o', 'n', 'm', 'k')

    /** A character that counts as 0 visual width but is invisible on 1.8 (reset color). */
    private const val INVISIBLE_PADDING = "§r"

    /**
     * Result of splitting one scoreboard line for 1.8.
     *
     * @property prefix 16 chars max; can be empty.
     * @property entry   16 chars max; serves as the team entry name (must be unique per scoreboard).
     * @property suffix  16 chars max; can be empty.
     */
    data class SplitLine(val prefix: String, val entry: String, val suffix: String) {
        init {
            require(prefix.length <= MAX_PREFIX) { "prefix too long: ${prefix.length}" }
            require(entry.length <= MAX_ENTRY) { "entry too long: ${entry.length}" }
            require(suffix.length <= MAX_SUFFIX) { "suffix too long: ${suffix.length}" }
        }
    }

    /**
     * Strips Minecraft hex color codes (`#aabbcc` or `&x&a&a&b&b&c&c` form)
     * from [input] and returns the plain string without them. Use this as a
     * pre-pass before [split] when down-sampling is acceptable.
     *
     * NOTE: this *strips* the color; if you want nearest-legacy down-sampling
     * use [downsampleHex].
     */
    fun stripHex(input: String): String {
        if (input.isEmpty()) return input
        // Strip the long SSS form: &x&a&a&b&b&c&c (BungeeCord-style hex encoding).
        val sss = Regex("""&x(&[0-9a-fA-F]){6}""")
        var out = sss.replace(input) { mr ->
            // Try to convert to nearest legacy color via downsampleHex; if it fails, drop.
            val matches = Regex("""&([0-9a-fA-F])""").findAll(mr.value).map { g -> g.groupValues[1] }.toList()
            if (matches.size != 6) return@replace ""
            val hex = matches.joinToString("")
            downsampleHex("#$hex").let { if (it.isEmpty()) "" else it }
        }
        // Strip the short form: &#aabbcc or #aabbcc (without '&x' prefix).
        val short = Regex("""&#([0-9a-fA-F]{6})""")
        out = short.replace(out) { mr ->
            val hex = mr.groupValues[1]
            downsampleHex("#$hex")
        }
        // Strip any leftover bare '#rrggbb' tokens.
        out = Regex("""#[0-9a-fA-F]{6}""").replace(out) { mr ->
            downsampleHex(mr.value)
        }
        return out
    }

    /**
     * Maps an RGB hex color (`#rrggbb`) to the nearest legacy Minecraft
     * color code (`&0`..`&f`). Returns the legacy code (including the `&`),
     * or empty string if the hex was not parseable.
     *
     * Palette is the standard 16 Minecraft colors (no bold/italic/etc).
     */
    fun downsampleHex(hex: String): String {
        val cleaned = hex.removePrefix("#").removePrefix("&").removePrefix("x")
        if (cleaned.length != 6) return ""
        val r = cleaned.substring(0, 2).toIntOrNull(16) ?: return ""
        val g = cleaned.substring(2, 4).toIntOrNull(16) ?: return ""
        val b = cleaned.substring(4, 6).toIntOrNull(16) ?: return ""
        val nearest = LEGACY_PALETTE.minByOrNull { (_, rgb) ->
            val dr = r - ((rgb shr 16) and 0xFF)
            val dg = g - ((rgb shr 8) and 0xFF)
            val db = b - (rgb and 0xFF)
            dr * dr + dg * dg + db * db
        } ?: return ""
        return "&${nearest.first}"
    }

    /**
     * Truncates [input] to [maxLen] characters, but never cuts inside a
     * `&x` color-code sequence. If truncation would orphan a leading `&`,
     * that `&` is dropped too.
     */
    fun truncatePreservingColor(input: String, maxLen: Int): String {
        if (input.length <= maxLen) return input
        var end = maxLen
        // If we cut right after '&', drop the orphan '&'.
        if (end > 0 && input[end - 1] == '&') end--
        return input.substring(0, end)
    }

    /**
     * Splits [raw] into a 1.8-safe (prefix, entry, suffix) tuple. If [raw]
     * fits within [MAX_ENTRY] chars, the prefix and suffix will be empty.
     *
     * Behavior:
     *   1. Carries the active color/format code forward across each split
     *      so the entry and suffix render with the same color.
     *   2. Truncates each segment to its respective max length.
     *   3. If the whole line is too long (> [MAX_TOTAL] chars), truncates
     *      the suffix (final visible chars are dropped).
     *
     * The caller is responsible for:
     *   - Stripping/down-sampling hex colors before calling (call [stripHex]
     *     or [downsampleHex] first if [stripHexColors] is enabled).
     *   - Padding identical entries so team names remain unique (see
     *     [padForUniqueness]).
     */
    fun split(raw: String): SplitLine {
        if (raw.isEmpty()) {
            // Empty lines must still have a unique entry; caller pads later.
            return SplitLine("", INVISIBLE_PADDING, "")
        }
        if (raw.length <= MAX_ENTRY) {
            // Fits as entry alone; prefix and suffix left empty.
            return SplitLine("", raw, "")
        }
        // Need to use prefix+entry+suffix. Carry the color across each split.
        val prefix = truncatePreservingColor(raw, MAX_PREFIX)
        val entry = truncatePreservingColor(raw.substring(prefix.length).let { carryColor(prefix, it) }, MAX_ENTRY)
        val suffixStart = prefix.length + entry.length - carriedColorLength(entry)
        val suffixRaw = if (suffixStart < raw.length) raw.substring(suffixStart) else ""
        val suffix = truncatePreservingColor(carryColor(entry, suffixRaw), MAX_SUFFIX)
        return SplitLine(prefix, entry, suffix)
    }

    /**
     * Pads [entries] so no two are equal, by appending an invisible color
     * code sequence (`§r`, `§0§r`, `§0§0§r`, ...). The original entries are
     * untouched if they were already unique.
     *
     * Returns a new list aligned with [entries]. Pad sequences grow only
     * long enough to make each entry unique within the set.
     */
    fun padForUniqueness(entries: List<String>): List<String> {
        if (entries.isEmpty()) return entries
        val seen = HashMap<String, Int>()
        return entries.map { e ->
            if (!seen.containsKey(e)) {
                seen[e] = 1
                e
            } else {
                var n = seen[e]!!
                var padded: String
                do {
                    padded = e + (0 until n).joinToString("") { INVISIBLE_PADDING }
                    n++
                } while (seen.containsKey(padded))
                seen[e] = n
                seen[padded] = 1
                padded
            }
        }
    }

    /**
     * Returns the legacy color/format code prefix that should be carried
     * forward from [source] to [target] so [target] renders with the same
     * color as [source] ends with.
     *
     * Implementation: scans [source] left-to-right, picks the last
     * `&x` (color OR format) sequence, and prepends it to [target].
     * If [source] has no color codes, returns [target] unchanged.
     */
    internal fun carryColor(source: String, target: String): String {
        val carried = lastActiveColorPrefix(source) ?: return target
        return carried + target
    }

    /**
     * Returns the length of the color-code prefix that [carryColor] would
     * prepend to a target. Used by [split] to correct the suffix slice
     * starting offset.
     */
    internal fun carriedColorLength(entry: String): Int {
        // entry was produced by carryColor(source, slice); the carried
        // prefix is the leading "&x" sequence (length 2) if present.
        if (entry.length >= 2 && entry[0] == '&' && (entry[1] in LEGACY_COLOR_CODES || entry[1] in LEGACY_FORMAT_CODES)) {
            return 2
        }
        return 0
    }

    /**
     * Returns the last `&x` (color OR format) sequence found in [source],
     * or null if there is none. Reset `&r` carries no color forward (we
     * return `&r` itself so the next segment is reset to default).
     */
    internal fun lastActiveColorPrefix(source: String): String? {
        var i = source.length - 2
        while (i >= 0) {
            if (source[i] == '&') {
                val code = source[i + 1]
                if (code in LEGACY_COLOR_CODES) return "&$code"
                if (code == 'r') return "&r"
                // Format codes (l/o/n/m/k) are NOT colors; keep scanning.
            }
            i--
        }
        return null
    }

    /**
     * Standard 16-color Minecraft palette as (code, RGB) tuples.
     * Used by [downsampleHex] for nearest-color mapping.
     */
    private val LEGACY_PALETTE: List<Pair<Char, Int>> = listOf(
        '0' to 0x000000, // BLACK
        '1' to 0x0000AA, // DARK_BLUE
        '2' to 0x00AA00, // DARK_GREEN
        '3' to 0x00AAAA, // DARK_AQUA
        '4' to 0xAA0000, // DARK_RED
        '5' to 0xAA00AA, // DARK_PURPLE
        '6' to 0xFFAA00, // GOLD
        '7' to 0xAAAAAA, // GRAY
        '8' to 0x555555, // DARK_GRAY
        '9' to 0x5555FF, // BLUE
        'a' to 0x55FF55, // GREEN
        'b' to 0x55FFFF, // AQUA
        'c' to 0xFF5555, // RED
        'd' to 0xFF55FF, // LIGHT_PURPLE
        'e' to 0xFFFF55, // YELLOW
        'f' to 0xFFFFFF, // WHITE
    )
}
