package com.hootsuite.nachos.chip

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.hootsuite.nachos.ChipConfiguration

open class ChipSpanChipCreator : ChipCreator<ChipSpan> {

    override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
        return ChipSpan(context, text, null, data)
    }

    override fun createChip(context: Context, existingChip: ChipSpan): ChipSpan {
        return ChipSpan(context, existingChip)
    }

    override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
        val chipHorizontalSpacing = chipConfiguration.chipHorizontalSpacing
        val chipBackground: ColorStateList? = chipConfiguration.chipBackground
        val chipCornerRadius = chipConfiguration.chipCornerRadius
        val chipTextColor = chipConfiguration.chipTextColor
        val chipTextSize = chipConfiguration.chipTextSize
        val chipHeight = chipConfiguration.chipHeight
        val chipVerticalSpacing = chipConfiguration.chipVerticalSpacing
        val maxAvailableWidth = chipConfiguration.maxAvailableWidth

        if (chipHorizontalSpacing != -1) {
            chip.leftMarginPx = chipHorizontalSpacing / 2
            chip.rightMarginPx = chipHorizontalSpacing / 2
        }
        chipBackground?.let { chip.backgroundColor = it }
        if (chipCornerRadius != -1) chip.cornerRadius = chipCornerRadius
        if (chipTextColor != Color.TRANSPARENT) chip.textColor = chipTextColor
        if (chipTextSize != -1) chip.textSize = chipTextSize
        if (chipHeight != -1) chip.chipHeight = chipHeight
        if (chipVerticalSpacing != -1) chip.chipVerticalSpacing = chipVerticalSpacing
        if (maxAvailableWidth != -1) chip.maxAvailableWidth = maxAvailableWidth
    }
} 