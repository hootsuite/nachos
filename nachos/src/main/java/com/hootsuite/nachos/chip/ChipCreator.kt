package com.hootsuite.nachos.chip

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.hootsuite.nachos.ChipConfiguration

/**
 * Interface to allow the creation and configuration of chips.
 *
 * @param C The type of [Chip] that the implementation will create/configure.
 */
interface ChipCreator<C : Chip> {
    /**
     * Creates a new chip from raw text.
     */
    fun createChip(@NonNull context: Context, @NonNull text: CharSequence, @Nullable data: Any?): C

    /**
     * Recreates a chip from an existing instance (usually to duplicate configuration).
     */
    fun createChip(@NonNull context: Context, @NonNull existingChip: C): C

    /**
     * Applies UI/behaviour configuration just before inserting the chip into text.
     */
    fun configureChip(@NonNull chip: C, @NonNull chipConfiguration: ChipConfiguration)
} 