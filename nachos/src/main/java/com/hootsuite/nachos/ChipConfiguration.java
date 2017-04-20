package com.hootsuite.nachos;

import android.content.res.ColorStateList;

public class ChipConfiguration {

    private final int mChipSpacing;
    private final ColorStateList mChipBackground;
    private final int mChipTextColor;
    private final int mChipTextSize;
    private final int mChipHeight;
    private final int mChipVerticalSpacing;
    private final int mMaxAvailableWidth;

    /**
     * Creates a new ChipConfiguration. You can pass in {@code -1} or {@code null} for any of the parameters to indicate that parameter should be
     * ignored.
     *
     * @param chipSpacing         the amount of horizontal space (in pixels) to put between consecutive chips
     * @param chipBackground      the {@link ColorStateList} to set as the background of the chips
     * @param chipTextColor       the color to set as the text color of the chips
     * @param chipTextSize        the font size (in pixels) to use for the text of the chips
     * @param chipHeight          the height (in pixels) of each chip
     * @param chipVerticalSpacing the amount of vertical space (in pixels) to put between chips on consecutive lines
     * @param maxAvailableWidth   the maximum available with for a chip (the width of a full line of text in the text view)
     */
    ChipConfiguration(int chipSpacing,
                      ColorStateList chipBackground,
                      int chipTextColor,
                      int chipTextSize,
                      int chipHeight,
                      int chipVerticalSpacing,
                      int maxAvailableWidth) {
        mChipSpacing = chipSpacing;
        mChipBackground = chipBackground;
        mChipTextColor = chipTextColor;
        mChipTextSize = chipTextSize;
        mChipHeight = chipHeight;
        mChipVerticalSpacing = chipVerticalSpacing;
        mMaxAvailableWidth = maxAvailableWidth;
    }

    public int getChipSpacing() {
        return mChipSpacing;
    }

    public ColorStateList getChipBackground() {
        return mChipBackground;
    }

    public int getChipTextColor() {
        return mChipTextColor;
    }

    public int getChipTextSize() {
        return mChipTextSize;
    }

    public int getChipHeight() {
        return mChipHeight;
    }

    public int getChipVerticalSpacing() {
        return mChipVerticalSpacing;
    }

    public int getMaxAvailableWidth() {
        return mMaxAvailableWidth;
    }
}
