package com.hootsuite.nachos;

import android.content.Context;

import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class ChipSpanChipCreatorTest extends TestCase {

    private static final String SAMPLE_TEXT = "abcde";

    private Context mContext;
    private ChipSpanChipCreator mChipSpanChipCreator;

    @Before
    public void setup() {
        mContext = RuntimeEnvironment.application.getApplicationContext();
        mChipSpanChipCreator = new ChipSpanChipCreator();
    }

    @Test
    public void testCreateChip_fromText() {
        // run
        ChipSpan chipSpan = mChipSpanChipCreator.createChip(mContext, SAMPLE_TEXT, null);

        // verify
        assertThat(chipSpan).isNotNull();
        assertThat(chipSpan.getText().toString()).isEqualTo(SAMPLE_TEXT);
    }

    @Test
    public void testCreateChip_fromChip() {
        // setup
        ChipSpan existingChipSpan = new ChipSpan(mContext, SAMPLE_TEXT, null, null);

        // run
        ChipSpan chipSpan = mChipSpanChipCreator.createChip(mContext, existingChipSpan);

        // verify
        assertThat(chipSpan).isNotNull();
        assertThat(chipSpan.getText().toString()).isEqualTo(SAMPLE_TEXT);
        assertThat(chipSpan.getDrawable()).isNull();
    }

    @Test
    public void testCreateChip_fromTextWithData() {
        // run
        Object data = new Object();
        ChipSpan chipSpan = mChipSpanChipCreator.createChip(mContext, SAMPLE_TEXT, data);

        // verify
        assertThat(chipSpan).isNotNull();
        assertThat(chipSpan.getText().toString()).isEqualTo(SAMPLE_TEXT);
        assertThat(chipSpan.getData()).isSameAs(data);
    }

    @Test
    public void testCreateChip_fromChipWithData() {
        // setup
        Object data = new Object();
        ChipSpan existingChipSpan = new ChipSpan(mContext, SAMPLE_TEXT, null, data);

        // run
        ChipSpan chipSpan = mChipSpanChipCreator.createChip(mContext, existingChipSpan);

        // verify
        assertThat(chipSpan).isNotNull();
        assertThat(chipSpan.getText().toString()).isEqualTo(SAMPLE_TEXT);
        assertThat(chipSpan.getDrawable()).isNull();
        assertThat(chipSpan.getData()).isSameAs(data);
    }
}
