package com.hootsuite.nachos.validator

import android.text.SpannableStringBuilder
import androidx.annotation.NonNull
import com.hootsuite.nachos.tokenizer.ChipTokenizer

/**
 * A [NachoValidator] that marks text invalid when unterminated tokens are present and
 * fixes it by chipifying all of them.
 */
class ChipifyingNachoValidator : NachoValidator {

    override fun isValid(@NonNull chipTokenizer: ChipTokenizer, text: CharSequence): Boolean {
        // Text is valid when there are no unterminated tokens.
        val unterminated = chipTokenizer.findAllTokens(text)
        return unterminated.isEmpty()
    }

    override fun fixText(@NonNull chipTokenizer: ChipTokenizer, invalidText: CharSequence): CharSequence {
        val builder = SpannableStringBuilder(invalidText)
        chipTokenizer.terminateAllTokens(builder)
        return builder
    }
} 