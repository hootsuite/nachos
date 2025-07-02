package com.hootsuite.nachos

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * Kotlin equivalent of the legacy `ChipConfiguration` Java class.
 * All parameters are optional so callers can continue passing "ignore" sentinel values (-1 / null)
 * exactly as before, while now benefiting from Kotlin's default-argument ergonomics.
 */
data class ChipConfiguration(
    val chipHorizontalSpacing: Int = -1,
    val chipBackground: ColorStateList? = null,
    @Dimension val chipCornerRadius: Int = -1,
    @ColorInt val chipTextColor: Int = Color.TRANSPARENT,
    @Dimension val chipTextSize: Int = -1,
    @Dimension val chipHeight: Int = -1,
    val chipVerticalSpacing: Int = -1,
    val maxAvailableWidth: Int = -1,
) 