package com.hootsuite.nachos.terminator

import android.text.Editable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.tokenizer.ChipTokenizer
import java.util.HashMap

/**
 * Kotlin port of the original Java `DefaultChipTerminatorHandler` implementation.
 */
class DefaultChipTerminatorHandler : ChipTerminatorHandler {

    @Nullable
    private var chipTerminators: MutableMap<Char, Int>? = null
    private var pasteBehavior: Int = ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR

    override fun setChipTerminators(@Nullable chipTerminators: Map<Char, Int>?) {
        this.chipTerminators = chipTerminators?.toMutableMap()
    }

    override fun addChipTerminator(character: Char, behavior: Int) {
        if (chipTerminators == null) {
            chipTerminators = HashMap()
        }
        chipTerminators!![character] = behavior
    }

    override fun setPasteBehavior(pasteBehavior: Int) {
        this.pasteBehavior = pasteBehavior
    }

    override fun findAndHandleChipTerminators(
        @NonNull tokenizer: ChipTokenizer,
        @NonNull text: Editable,
        start: Int,
        end: Int,
        isPasteEvent: Boolean
    ): Int {
        // If we don't have any chip terminators, there's nothing to look for
        val terminators = chipTerminators ?: return -1

        val textIterator = TextIterator(text, start, end)
        var selectionIndex = -1

        characterLoop@ while (textIterator.hasNextCharacter()) {
            val ch = textIterator.nextCharacter()
            if (isChipTerminator(ch, terminators)) {
                val behavior = if (isPasteEvent && pasteBehavior != ChipTerminatorHandler.PASTE_BEHAVIOR_USE_DEFAULT) {
                    pasteBehavior
                } else {
                    terminators[ch] ?: ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR
                }
                val newSelection = when (behavior) {
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL -> {
                        selectionIndex = handleChipifyAll(textIterator, tokenizer)
                        break@characterLoop
                    }
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN -> handleChipifyCurrentToken(textIterator, tokenizer)
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR -> handleChipifyToTerminator(textIterator, tokenizer)
                    else -> -1
                }
                if (newSelection != -1) {
                    selectionIndex = newSelection
                }
            }
        }

        return selectionIndex
    }

    // --- Behaviour handlers ------------------------------------------------------------------

    private fun handleChipifyAll(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        textIterator.deleteCharacter(maintainIndex = true)
        tokenizer.terminateAllTokens(textIterator.text)
        return textIterator.totalLength()
    }

    private fun handleChipifyCurrentToken(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        textIterator.deleteCharacter(maintainIndex = true)
        val text = textIterator.text
        val index = textIterator.index
        val tokenStart = tokenizer.findTokenStart(text, index)
        val tokenEnd = tokenizer.findTokenEnd(text, index)
        return if (tokenStart < tokenEnd) {
            val chippedText: CharSequence = tokenizer.terminateToken(text.subSequence(tokenStart, tokenEnd), null)
            textIterator.replace(tokenStart, tokenEnd, chippedText)
            tokenStart + chippedText.length
        } else {
            -1
        }
    }

    private fun handleChipifyToTerminator(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        val text = textIterator.text
        val index = textIterator.index
        if (index > 0) {
            val tokenStart = tokenizer.findTokenStart(text, index)
            if (tokenStart < index) {
                val chippedText: CharSequence = tokenizer.terminateToken(text.subSequence(tokenStart, index), null)
                textIterator.replace(tokenStart, index + 1, chippedText)
            } else {
                textIterator.deleteCharacter(maintainIndex = false)
            }
        } else {
            textIterator.deleteCharacter(maintainIndex = false)
        }
        return -1
    }

    private fun isChipTerminator(character: Char, terminators: Map<Char, Int>): Boolean {
        return terminators.containsKey(character)
    }
} 