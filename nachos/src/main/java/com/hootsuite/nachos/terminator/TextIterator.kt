package com.hootsuite.nachos.terminator

import android.text.Editable

/**
 * A small mutable windowed iterator over an [Editable] that simplifies modifying text while
 * iterating. It mirrors the behaviour of the original Java implementation.
 */
class TextIterator(
    val text: Editable,
    start: Int,
    end: Int
) {

    private var startIndex: Int = start
    private var endIndex: Int = end

    /** Current cursor position inside [text] (initially `start - 1`). */
    var index: Int = startIndex - 1
        private set

    /** Total length of the underlying [text]. */
    fun totalLength(): Int = text.length

    /** Size of the window being iterated (`end - start`). */
    fun windowLength(): Int = endIndex - startIndex

    /** Whether there is another character in the window. */
    fun hasNextCharacter(): Boolean = (index + 1) < endIndex

    /** Returns the next character and advances the iterator. */
    fun nextCharacter(): Char {
        index++
        return text[index]
    }

    /** Deletes the character at the current [index]. */
    fun deleteCharacter(maintainIndex: Boolean) {
        text.replace(index, index + 1, "")
        if (!maintainIndex) {
            index--
        }
        endIndex--
    }

    /**
     * Replaces the text between [replaceStart] (inclusive) and [replaceEnd] (exclusive) with
     * [replacement] and updates internal indices so iteration can continue safely.
     */
    fun replace(replaceStart: Int, replaceEnd: Int, replacement: CharSequence) {
        text.replace(replaceStart, replaceEnd, replacement)
        // Update indices
        val newLength = replacement.length
        val oldLength = replaceEnd - replaceStart
        index = replaceStart + newLength - 1
        endIndex += newLength - oldLength
    }
} 