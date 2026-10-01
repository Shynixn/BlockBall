package com.github.shynixn.blockball.scoreboard

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for [LegacyScoreboardLineSplitter]. These run on plain JVM (no
 * Bukkit required) so they execute in the standard `test` task.
 */
class LegacyScoreboardLineSplitterTest {

    @Test
    fun `empty string produces empty split`() {
        val s = LegacyScoreboardLineSplitter.split("")
        assertEquals("", s.prefix)
        assertEquals("§r", s.entry)
        assertEquals("", s.suffix)
    }

    @Test
    fun `short line fits entirely in entry`() {
        val s = LegacyScoreboardLineSplitter.split("&6Time:")
        assertEquals("", s.prefix)
        assertEquals("&6Time:", s.entry)
        assertEquals("", s.suffix)
    }

    @Test
    fun `each segment respects its max length`() {
        val long = "&aVery long scoreboard line that needs to be split because it exceeds the 1_8 limit"
        val s = LegacyScoreboardLineSplitter.split(long)
        assertTrue(s.prefix.length <= LegacyScoreboardLineSplitter.MAX_PREFIX, "prefix too long: ${s.prefix.length}")
        assertTrue(s.entry.length <= LegacyScoreboardLineSplitter.MAX_ENTRY, "entry too long: ${s.entry.length}")
        assertTrue(s.suffix.length <= LegacyScoreboardLineSplitter.MAX_SUFFIX, "suffix too long: ${s.suffix.length}")
    }

    @Test
    fun `color is carried across prefix to entry to suffix`() {
        val line = "&cRedTeamDisplayName: &f0 &7(15m)"
        val s = LegacyScoreboardLineSplitter.split(line)
        // The entry should start with the carried color &c.
        assertTrue(s.entry.startsWith("&c") || s.entry.startsWith("§c"),
            "expected entry to carry color from prefix, got: ${s.entry}")
    }

    @Test
    fun `truncate never leaves an orphan ampersand`() {
        val truncated = LegacyScoreboardLineSplitter.truncatePreservingColor("&6Hello &aWorld!", 8)
        // Index 7 is 'a' (after &), so 8 chars would slice "&6Hello " — no orphan &.
        assertEquals("&6Hello ", truncated)
    }

    @Test
    fun `truncate drops orphan ampersand at boundary`() {
        // "&6Hello &" has 8 chars but the last char is '&'; trim it.
        val truncated = LegacyScoreboardLineSplitter.truncatePreservingColor("&6Hello &aWorld", 8)
        assertEquals("&6Hello ", truncated)
    }

    @Test
    fun `padForUniqueness produces unique entries`() {
        val entries = listOf("&6Time:", "&6Time:", "&6Time:", "")
        val padded = LegacyScoreboardLineSplitter.padForUniqueness(entries)
        val distinct = padded.toSet()
        assertEquals(entries.size, distinct.size, "entries not unique: $padded")
    }

    @Test
    fun `stripHex removes bare hash tokens`() {
        val s = LegacyScoreboardLineSplitter.stripHex("&#F57F17Hello #FDD835World")
        assertTrue(!s.contains("#"), "hex not stripped: $s")
    }

    @Test
    fun `downsampleHex picks nearest legacy color`() {
        // #FFAA00 is GOLD -> &6
        val s = LegacyScoreboardLineSplitter.downsampleHex("#FFAA00")
        assertEquals("&6", s)
    }

    @Test
    fun `downsampleHex picks white for white`() {
        val s = LegacyScoreboardLineSplitter.downsampleHex("#FFFFFF")
        assertEquals("&f", s)
    }

    @Test
    fun `downsampleHex picks black for black`() {
        val s = LegacyScoreboardLineSplitter.downsampleHex("#000000")
        assertEquals("&0", s)
    }

    @Test
    fun `downsampleHex returns empty for garbage`() {
        val s = LegacyScoreboardLineSplitter.downsampleHex("xyz")
        assertEquals("", s)
    }
}
