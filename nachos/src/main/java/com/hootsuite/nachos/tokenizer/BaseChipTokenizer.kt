package com.hootsuite.nachos.tokenizer

import android.text.Editable
import android.text.Spanned
import android.util.Pair
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.Chip

/**
 * Base implementation of [ChipTokenizer] that performs no actions and returns default values.
 * Extend this class when you only need to override a subset of the interface methods.
 */
abstract class BaseChipTokenizer : ChipTokenizer {

    override fun applyConfiguration(text: Editable, chipConfiguration: ChipConfiguration) {
        /* no-op */
    }

    override fun findTokenStart(text: CharSequence, cursor: Int): Int = 0

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int = 0

    @NonNull
    override fun findAllTokens(text: CharSequence): List<Pair<Int, Int>> = emptyList()

    override fun terminateToken(text: CharSequence, @Nullable data: Any?): CharSequence = text

    override fun terminateAllTokens(text: Editable) {
        /* no-op */
    }

    override fun findChipStart(chip: Chip, text: Spanned): Int = 0

    override fun findChipEnd(chip: Chip, text: Spanned): Int = 0

    @NonNull
    override fun findAllChips(start: Int, end: Int, text: Spanned): Array<out Chip> = emptyArray()

    override fun revertChipToToken(chip: Chip, text: Editable) {
        /* no-op */
    }

    override fun deleteChip(chip: Chip, text: Editable) {
        /* no-op */
    }

    override fun deleteChipAndPadding(chip: Chip, text: Editable) {
        /* no-op */
    }
} 