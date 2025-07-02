package com.hootsuite.nachos.tokenizer

import android.text.Editable
import android.text.Spanned
import android.util.Pair
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.Chip

/**
 * Kotlin equivalent of the original Java [ChipTokenizer] interface.
 *  
 * All existing Java implementations can seamlessly implement this interface – the generated
 * byte-code signatures remain identical (regular Java arrays, `android.util.Pair`, etc.).
 */
interface ChipTokenizer {

    /**
     * Applies the supplied [chipConfiguration] to future chips and to any existing chips in
     * [text].
     */
    fun applyConfiguration(@NonNull text: Editable, @NonNull chipConfiguration: ChipConfiguration)

    /** @see android.widget.MultiAutoCompleteTextView.Tokenizer.findTokenStart */
    fun findTokenStart(text: CharSequence, cursor: Int): Int

    /** @see android.widget.MultiAutoCompleteTextView.Tokenizer.findTokenEnd */
    fun findTokenEnd(text: CharSequence, cursor: Int): Int

    /**
     * Finds all unterminated tokens in [text]. Each returned pair is ``(startIndex, endIndex)``.
     */
    @NonNull
    fun findAllTokens(text: CharSequence): List<Pair<Int, Int>>

    /** Terminates the given token, turning it into a chip representation. */
    fun terminateToken(text: CharSequence, @Nullable data: Any?): CharSequence

    /** Terminates **all** unterminated tokens in [text] (may mutate [text]). */
    fun terminateAllTokens(text: Editable)

    /** Finds the start index of [chip] inside [text]. */
    fun findChipStart(chip: Chip, text: Spanned): Int

    /** Finds the (exclusive) end index of [chip] inside [text]. */
    fun findChipEnd(chip: Chip, text: Spanned): Int

    /** Returns all chips within the `[start, end)` window of [text]. */
    @NonNull
    fun findAllChips(start: Int, end: Int, text: Spanned): Array<out Chip>

    /** Reverts [chip] back to plain text inside [text] (mutates [text]). */
    fun revertChipToToken(chip: Chip, text: Editable)

    /** Deletes [chip] from [text] (mutates [text]). */
    fun deleteChip(chip: Chip, text: Editable)

    /** Deletes [chip] **and** any padding inserted around it from [text] (mutates [text]). */
    fun deleteChipAndPadding(chip: Chip, text: Editable)
} 