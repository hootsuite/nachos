package com.hootsuite.nachos.terminator

import android.text.Editable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.tokenizer.ChipTokenizer

/**
 * Kotlin equivalent of the Java [ChipTerminatorHandler] interface.  Declared here so that
 * it can be used seamlessly from both Java and Kotlin sources during the migration.
 * All constants are declared as `const val` to preserve Java compatibility
 * (e.g. `ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL`).
 */
interface ChipTerminatorHandler {

    companion object {
        /**
         * Constant for use with [setPasteBehavior] indicating that, on paste, the pre-configured
         * behaviour for each individual terminator character should be used.
         */
        @JvmField
        val PASTE_BEHAVIOR_USE_DEFAULT: Int = -1

        /** When a chip terminator is encountered, chipify **all** existing tokens */
        @JvmField
        val BEHAVIOR_CHIPIFY_ALL: Int = 0

        /** Chipify only the **current** token */
        @JvmField
        val BEHAVIOR_CHIPIFY_CURRENT_TOKEN: Int = 1

        /** Chipify text up **to** the terminator character (may be part of a token) */
        @JvmField
        val BEHAVIOR_CHIPIFY_TO_TERMINATOR: Int = 2
    }

    /**
     * Sets all terminator characters replacing any previously configured ones.
     *
     * @param chipTerminators map of terminator character to behaviour or `null` to clear.
     */
    fun setChipTerminators(@Nullable chipTerminators: Map<Char, Int>?)

    /**
     * Adds/updates a single terminator character with the supplied behaviour.
     */
    fun addChipTerminator(character: Char, behavior: Int)

    /**
     * Customises how paste events are handled.
     */
    fun setPasteBehavior(pasteBehavior: Int)

    /**
     * Scans the `[start, end)` window of `text` for terminator characters and handles them
     * according to their configured behaviours.
     *
     * @param tokenizer    tokenizer used to chipify
     * @param text         full editable text
     * @param start        inclusive start index of the changed window
     * @param end          exclusive end index of the changed window
     * @param isPasteEvent true when invoked as part of a paste event
     *
     * @return the desired new selection index (>= 0) or `-1` if the selection should remain
     *         unchanged.
     */
    fun findAndHandleChipTerminators(
        @NonNull tokenizer: ChipTokenizer,
        @NonNull text: Editable,
        start: Int,
        end: Int,
        isPasteEvent: Boolean
    ): Int
} 