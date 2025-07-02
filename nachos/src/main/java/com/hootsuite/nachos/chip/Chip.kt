package com.hootsuite.nachos.chip;

import androidx.annotation.Nullable;

interface Chip {

    /**
     * @return the text represented by this Chip
     */
    val text: CharSequence

    /**
     * @return the data associated with this Chip or null if no data is associated with it
     */
    val data: Any?

    /**
     * @return the width of the Chip or -1 if the Chip hasn't been given the chance to calculate its width
     */
    val width: Int

    /**
     * Sets the UI state.
     *
     * @param stateSet one of the state constants in {@link android.view.View}
     */
    fun setState(stateSet: IntArray)
}
