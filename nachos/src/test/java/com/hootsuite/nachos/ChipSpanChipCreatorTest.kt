package com.hootsuite.nachos

import android.content.Context
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Kotlin port of [ChipSpanChipCreatorTest]. Behaviour identical.
 */
@RunWith(CustomRobolectricRunner::class)
@Config(sdk = [TestConfig.SDK_VERSION])
class ChipSpanChipCreatorTest {

    private lateinit var context: Context
    private lateinit var chipCreator: ChipSpanChipCreator

    companion object {
        private const val SAMPLE_TEXT = "abcde"
    }

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        chipCreator = ChipSpanChipCreator()
    }

    @Test
    fun createChip_fromText() {
        val chipSpan: ChipSpan = chipCreator.createChip(context, SAMPLE_TEXT, null)
        assertThat(chipSpan).isNotNull
        assertThat(chipSpan.text.toString()).isEqualTo(SAMPLE_TEXT)
    }

    @Test
    fun createChip_fromChip() {
        val existing = ChipSpan(context, SAMPLE_TEXT, null, null)
        val chipSpan: ChipSpan = chipCreator.createChip(context, existing)
        assertThat(chipSpan).isNotNull
        assertThat(chipSpan.text.toString()).isEqualTo(SAMPLE_TEXT)
        assertThat(chipSpan.drawable).isNull()
    }

    @Test
    fun createChip_fromTextWithData() {
        val data = Any()
        val chipSpan: ChipSpan = chipCreator.createChip(context, SAMPLE_TEXT, data)
        assertThat(chipSpan).isNotNull
        assertThat(chipSpan.text.toString()).isEqualTo(SAMPLE_TEXT)
        assertThat(chipSpan.data).isSameAs(data)
    }

    @Test
    fun createChip_fromChipWithData() {
        val data = Any()
        val existing = ChipSpan(context, SAMPLE_TEXT, null, data)
        val chipSpan: ChipSpan = chipCreator.createChip(context, existing)
        assertThat(chipSpan).isNotNull
        assertThat(chipSpan.text.toString()).isEqualTo(SAMPLE_TEXT)
        assertThat(chipSpan.drawable).isNull()
        assertThat(chipSpan.data).isSameAs(data)
    }
} 