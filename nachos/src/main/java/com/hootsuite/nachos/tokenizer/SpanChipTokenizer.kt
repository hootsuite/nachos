package com.hootsuite.nachos.tokenizer

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.util.Pair
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.Chip
import com.hootsuite.nachos.chip.ChipCreator
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Kotlin port of the original Java [SpanChipTokenizer]. Behaviour is identical.
 */
class SpanChipTokenizer<C : Chip>(
    private val context: Context,
    @NonNull private val chipCreator: ChipCreator<C>,
    @NonNull private val chipClass: Class<C>
) : ChipTokenizer {

    companion object {
        /** Unit-Separator ASCII control char – untypable so we control when a chip is created */
        const val CHIP_SPAN_SEPARATOR: Char = 31.toChar()
        const val AUTOCORRECT_SEPARATOR: Char = ' '
    }

    @Nullable
    private var chipConfiguration: ChipConfiguration? = null

    // Comparator that sorts token index pairs in reverse order of their start index.
    private val reverseTokenIndexesSorter: Comparator<Pair<Int, Int>> = Comparator { lhs, rhs -> rhs.first - lhs.first }

    // -----------------------------------------------------------------------------------------
    // Configuration
    // -----------------------------------------------------------------------------------------
    override fun applyConfiguration(text: Editable, chipConfiguration: ChipConfiguration) {
        this.chipConfiguration = chipConfiguration

        for (chip in findAllChips(0, text.length, text)) {
            // Recreate chip with new configuration
            val chipStart = findChipStart(chip, text)
            deleteChip(chip, text)
            text.insert(chipStart, terminateToken(chipCreator.createChip(context, chip)))
        }
    }

    // -----------------------------------------------------------------------------------------
    // Token boundaries
    // -----------------------------------------------------------------------------------------
    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor
        // Move backwards until we hit the separator
        while (i > 0 && text[i - 1] != CHIP_SPAN_SEPARATOR) {
            i--
        }
        // Skip leading whitespace inside the token
        while (i > 0 && i < text.length && text[i].isWhitespace()) {
            i++
        }
        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val len = text.length
        while (i < len) {
            if (text[i] == CHIP_SPAN_SEPARATOR) {
                return i - 1 // separator is preceded by a space
            }
            i++
        }
        return len
    }

    // -----------------------------------------------------------------------------------------
    // Token + chip search helpers
    // -----------------------------------------------------------------------------------------
    @NonNull
    override fun findAllTokens(text: CharSequence): List<Pair<Int, Int>> {
        val unterminated = ArrayList<Pair<Int, Int>>()
        var insideChip = false
        var idx = text.length - 1
        while (idx >= 0) {
            val ch = text[idx]
            when {
                ch == CHIP_SPAN_SEPARATOR -> insideChip = !insideChip
                ch.isWhitespace() -> { /* skip */ }
                !insideChip -> {
                    val tokenStart = findTokenStart(text, idx)
                    val tokenEnd = findTokenEnd(text, idx)
                    if (tokenEnd - tokenStart >= 1) {
                        unterminated.add(Pair(tokenStart, tokenEnd))
                        idx = tokenStart // jump
                    }
                }
            }
            idx--
        }
        return unterminated
    }

    // -----------------------------------------------------------------------------------------
    // Chipification helpers
    // -----------------------------------------------------------------------------------------
    override fun terminateToken(text: CharSequence, @Nullable data: Any?): CharSequence {
        val trimmed = text.toString().trim()
        return terminateToken(chipCreator.createChip(context, trimmed, data))
    }

    private fun terminateToken(chip: C): CharSequence {
        val chipSeparator = CHIP_SPAN_SEPARATOR.toString()
        val autoSeparator = AUTOCORRECT_SEPARATOR.toString()
        val textWithSeparators = autoSeparator + chipSeparator + chip.text + chipSeparator + autoSeparator
        val spannable = SpannableString(textWithSeparators)

        chipConfiguration?.let { chipCreator.configureChip(chip, it) }
        // attach span across entire string
        spannable.setSpan(chip, 0, textWithSeparators.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    override fun terminateAllTokens(text: Editable) {
        val unterminated = findAllTokens(text)
        // sort reverse so index math safe
        Collections.sort(unterminated, reverseTokenIndexesSorter)
        for (indexes in unterminated) {
            val start = indexes.first
            val end = indexes.second
            val tokenText = text.subSequence(start, end)
            val chipped = terminateToken(tokenText, null)
            text.replace(start, end, chipped)
        }
    }

    // -----------------------------------------------------------------------------------------
    // Chip span operations
    // -----------------------------------------------------------------------------------------
    override fun findChipStart(chip: Chip, text: Spanned): Int = text.getSpanStart(chip)

    override fun findChipEnd(chip: Chip, text: Spanned): Int = text.getSpanEnd(chip)

    @Suppress("UNCHECKED_CAST")
    @NonNull
    override fun findAllChips(start: Int, end: Int, text: Spanned): Array<C> {
        val spans = text.getSpans(start, end, chipClass)
        return spans ?: java.lang.reflect.Array.newInstance(chipClass, 0) as Array<C>
    }

    override fun revertChipToToken(chip: Chip, text: Editable) {
        val chipStart = findChipStart(chip, text)
        val chipEnd = findChipEnd(chip, text)
        text.removeSpan(chip)
        text.replace(chipStart, chipEnd, chip.text)
    }

    override fun deleteChip(chip: Chip, text: Editable) {
        val chipStart = findChipStart(chip, text)
        val chipEnd = findChipEnd(chip, text)
        text.removeSpan(chip)
        if (chipStart != chipEnd) {
            text.delete(chipStart, chipEnd)
        }
    }

    override fun deleteChipAndPadding(chip: Chip, text: Editable) {
        // this implementation adds no extra padding; same as deleteChip
        deleteChip(chip, text)
    }
} 