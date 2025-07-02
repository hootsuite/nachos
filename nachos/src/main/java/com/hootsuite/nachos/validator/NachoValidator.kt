package com.hootsuite.nachos.validator

import androidx.annotation.NonNull
import com.hootsuite.nachos.tokenizer.ChipTokenizer

/**
 * Kotlin sibling of the original Java `NachoValidator` interface.
 * 
 * Provides hooks for validating/auto-correcting NachoTextView content.
 */
interface NachoValidator {

    /**
     * Returns `true` when [text] passes validation.
     */
    fun isValid(@NonNull chipTokenizer: ChipTokenizer, text: CharSequence): Boolean

    /**
     * Returns a corrected version of [invalidText] such that [isValid] would return `true`.
     */
    fun fixText(@NonNull chipTokenizer: ChipTokenizer, invalidText: CharSequence): CharSequence
} 