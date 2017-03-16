package com.hootsuite.nachos.chip;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;

import com.hootsuite.nachos.ChipConfiguration;

public class ChipSpanChipCreator implements ChipCreator<ChipSpan> {

    @Override
    public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
        return new ChipSpan(context, text, null, data);
    }

    @Override
    public ChipSpan createChip(@NonNull Context context, @NonNull ChipSpan existingChip) {
        return new ChipSpan(context, existingChip);
    }

    @Override
    public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
        int chipSpacing = chipConfiguration.getChipSpacing();
        ColorStateList chipBackground = chipConfiguration.getChipBackground();
        int chipTextColor = chipConfiguration.getChipTextColor();
        int chipTextSize = chipConfiguration.getChipTextSize();
        int chipHeight = chipConfiguration.getChipHeight();
        int chipVerticalSpacing = chipConfiguration.getChipVerticalSpacing();
        int maxAvailableWidth = chipConfiguration.getMaxAvailableWidth();

        if (chipSpacing != -1) {
            chip.setLeftMargin(chipSpacing / 2);
            chip.setRightMargin(chipSpacing / 2);
        }
        if (chipBackground != null) {
            chip.setBackgroundColor(chipBackground);
        }
        if (chipTextColor != -1) {
            chip.setTextColor(chipTextColor);
        }
        if (chipTextSize != -1) {
            chip.setTextSize(chipTextSize);
        }
        if (chipHeight != -1) {
            chip.setChipHeight(chipHeight);
        }
        if (chipVerticalSpacing != -1) {
            chip.setChipVerticalSpacing(chipVerticalSpacing);
        }
        if (maxAvailableWidth != -1) {
            chip.setMaxAvailableWidth(maxAvailableWidth);
        }
    }
}
