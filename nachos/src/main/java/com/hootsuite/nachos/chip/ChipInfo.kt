package com.hootsuite.nachos.chip

/**
 * Immutable container holding the display text and optional data for a chip.
 */
data class ChipInfo(
    val text: CharSequence,
    val data: Any?
) 