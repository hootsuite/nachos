package com.hootsuite.nachos;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;

import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipCreator;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class SpanChipTokenizerTest extends TestCase {

    private static final CharSequence EMPTY_STRING = "";
    private static final CharSequence SINGLE_CHARACTER = "a";
    private static final CharSequence SINGLE_CHARACTER_2 = "b";
    private static final CharSequence SINGLE_TOKEN = "test";
    private static final CharSequence SINGLE_TOKEN_2 = "test2";
    private static final CharSequence SINGLE_TOKEN_3 = "test3";
    private static final CharSequence WHITESPACE = "   ";
    private static final CharSequence[] TEST_TOKENS_ALL_VALID = new CharSequence[] {SINGLE_CHARACTER, "token1", "token2", "token3", "token with spaces", SINGLE_CHARACTER};
    private static final CharSequence[] TEST_CHIP_VALUES_ALL_VALID = new CharSequence[] {SINGLE_CHARACTER_2, "chip1", "chip2", "chip3", "chip with spaces", SINGLE_CHARACTER_2};
    private static final CharSequence[] TEST_TOKENS_ONE_WHITESPACE = new CharSequence[] {SINGLE_CHARACTER, "token1", "token2", "token3", WHITESPACE, "token with spaces", SINGLE_CHARACTER};
    private static final CharSequence[] TEST_CHIP_VALUES_ONE_WHITESPACE = new CharSequence[] {SINGLE_CHARACTER_2, "chip1", "chip2", "chip3", WHITESPACE, "chip with spaces", SINGLE_CHARACTER_2};

    private ChipCreator<Chip> mMockChipCreator;

    private SpanChipTokenizer<Chip> mSpanChipTokenizer;

    private CharSequence singleTokenChipified;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mMockChipCreator = (ChipCreator<Chip>)mock(ChipCreator.class);
        doAnswer(new Answer<Chip>() {
            @Override
            public Chip answer(InvocationOnMock invocation) throws Throwable {
                Chip mockChip = mock(Chip.class);
                CharSequence text = (String)invocation.getArguments()[1];
                when(mockChip.getText()).thenReturn(text);
                when(mockChip.getData()).thenReturn(invocation.getArguments()[2]);
                return mockChip;
            }
        }).when(mMockChipCreator).createChip(any(Context.class), any(CharSequence.class), any());
        doAnswer(new Answer<Chip>() {
            @Override
            public Chip answer(InvocationOnMock invocation) throws Throwable {
                Chip mockChip = mock(Chip.class);
                CharSequence text = ((Chip)invocation.getArguments()[1]).getText();
                Object data = ((Chip)invocation.getArguments()[1]).getData();
                when(mockChip.getText()).thenReturn(text);
                when(mockChip.getData()).thenReturn(data);
                return mockChip;
            }
        }).when(mMockChipCreator).createChip(any(Context.class), any(Chip.class));

        mSpanChipTokenizer = new SpanChipTokenizer<>(RuntimeEnvironment.application.getApplicationContext(), mMockChipCreator, Chip.class);
        singleTokenChipified = createChipText(SINGLE_TOKEN);
    }

    @Test
    public void testApplyConfiguration_emptyString() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(EMPTY_STRING);
        ChipConfiguration testConfiguration = createTestChipConfiguration();

        // run
        mSpanChipTokenizer.applyConfiguration(testText, testConfiguration);

        // verify
        assertThat(testText.toString()).isEmpty();
        verify(mMockChipCreator, never()).createChip(any(Context.class), any(Chip.class));
    }

    @Test
    public void testApplyConfiguration_singleToken() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        ChipConfiguration testConfiguration = createTestChipConfiguration();

        // run
        mSpanChipTokenizer.applyConfiguration(testText, testConfiguration);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN.toString());
        verify(mMockChipCreator, never()).createChip(any(Context.class), any(Chip.class));
    }

    @Test
    public void testApplyConfiguration_singleChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        ChipConfiguration testConfiguration = createTestChipConfiguration();

        // run
        mSpanChipTokenizer.applyConfiguration(testText, testConfiguration);

        // verify
        assertThat(testText.toString()).isEqualTo(singleTokenChipified.toString());
        verify(mMockChipCreator, times(1)).createChip(any(Context.class), any(Chip.class));
    }

    @Test
    public void testApplyConfiguration_multipleChipsAndTokens() {
        // setup
        SpannableStringBuilder testText = createTestText(TEST_TOKENS_ALL_VALID, false, TEST_CHIP_VALUES_ALL_VALID, true);
        SpannableStringBuilder expectedText = new SpannableStringBuilder(testText.toString());
        ChipConfiguration testConfiguration = createTestChipConfiguration();

        // run
        mSpanChipTokenizer.applyConfiguration(testText, testConfiguration);

        // verify
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
        verify(mMockChipCreator, times(TEST_CHIP_VALUES_ALL_VALID.length)).createChip(any(Context.class), any(Chip.class));
    }

    @Test
    public void testFindTokenStart_emptyString() {
        // setup
        int cursor = 0;

        // run
        int tokenStart = mSpanChipTokenizer.findTokenStart(EMPTY_STRING, cursor);

        // verify
        assertThat(tokenStart).isEqualTo(0);
    }

    @Test
    public void testFindTokenStart_singleCharacter() {
        // setup
        int beginningCursor = 0;
        int endCursor = 1;

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(SINGLE_CHARACTER, beginningCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(SINGLE_CHARACTER, endCursor);

        // verify
        assertThat(beginningTokenStart).isEqualTo(0);
        assertThat(endTokenStart).isEqualTo(0);
    }

    @Test
    public void testFindTokenStart_singleToken() {
        // setup
        int beginningCursor = 0;
        int middleCursor = SINGLE_TOKEN.length() / 2;
        int endCursor = SINGLE_TOKEN.length() - 1;

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(SINGLE_TOKEN, beginningCursor);
        int middleTokenStart = mSpanChipTokenizer.findTokenStart(SINGLE_TOKEN, middleCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(SINGLE_TOKEN, endCursor);

        // verify
        assertThat(beginningTokenStart).isEqualTo(0);
        assertThat(middleTokenStart).isEqualTo(0);
        assertThat(endTokenStart).isEqualTo(0);
    }

    @Test
    public void testFindTokenStart_chipThenToken() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(singleTokenChipified);
        text.append(SINGLE_TOKEN);
        int firstIndexOfToken = singleTokenChipified.length();

        int beginningCursor = singleTokenChipified.length();
        int middleCursor = firstIndexOfToken + SINGLE_TOKEN.length() / 2;
        int endCursor = text.length() - 1;

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(text, beginningCursor);
        int middleTokenStart = mSpanChipTokenizer.findTokenStart(text, middleCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(text, endCursor);

        // verify
        assertThat(beginningTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(middleTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(endTokenStart).isEqualTo(firstIndexOfToken);
    }

    @Test
    public void testFindTokenStart_tokenThenChip() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(SINGLE_TOKEN);
        text.append(singleTokenChipified);
        int firstIndexOfToken = 0;

        int beginningCursor = 0;
        int middleCursor = SINGLE_TOKEN.length() / 2;
        int endCursor = SINGLE_TOKEN.length();

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(text, beginningCursor);
        int middleTokenStart = mSpanChipTokenizer.findTokenStart(text, middleCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(text, endCursor);

        // verify
        assertThat(beginningTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(middleTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(endTokenStart).isEqualTo(firstIndexOfToken);
    }

    @Test
    public void testFindTokenStart_tokenBetweenChips() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(singleTokenChipified);
        text.append(SINGLE_TOKEN);
        text.append(singleTokenChipified);
        int firstIndexOfToken = singleTokenChipified.length();

        int beginningCursor = singleTokenChipified.length();
        int middleCursor = firstIndexOfToken + SINGLE_TOKEN.length() / 2;
        int endCursor = firstIndexOfToken + SINGLE_TOKEN.length();

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(text, beginningCursor);
        int middleTokenStart = mSpanChipTokenizer.findTokenStart(text, middleCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(text, endCursor);

        // verify
        assertThat(beginningTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(middleTokenStart).isEqualTo(firstIndexOfToken);
        assertThat(endTokenStart).isEqualTo(firstIndexOfToken);
    }

    @Test
    public void testFindTokenStart_withWhitespace() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(singleTokenChipified);
        text.append(WHITESPACE);
        text.append(SINGLE_TOKEN);
        text.append(singleTokenChipified);
        int firstIndexOfToken = singleTokenChipified.length();

        int beginningCursor = singleTokenChipified.length();
        int middleCursor = firstIndexOfToken + SINGLE_TOKEN.length() / 2;
        int endCursor = firstIndexOfToken + SINGLE_TOKEN.length();

        // run
        int beginningTokenStart = mSpanChipTokenizer.findTokenStart(text, beginningCursor);
        int middleTokenStart = mSpanChipTokenizer.findTokenStart(text, middleCursor);
        int endTokenStart = mSpanChipTokenizer.findTokenStart(text, endCursor);

        // verify
        int expectedTokenStart = firstIndexOfToken + WHITESPACE.length();
        assertThat(beginningTokenStart).isEqualTo(expectedTokenStart);
        assertThat(middleTokenStart).isEqualTo(expectedTokenStart);
        assertThat(endTokenStart).isEqualTo(expectedTokenStart);
    }

    @Test
    public void testFindTokenEnd_emptyString() {
        // setup
        int cursor = 0;

        // run
        int tokenEnd = mSpanChipTokenizer.findTokenEnd(EMPTY_STRING, cursor);

        // verify
        assertThat(tokenEnd).isEqualTo(0);
    }

    @Test
    public void testFindTokenEnd_singleCharacter() {
        // setup
        int beginningCursor = 0;
        int endCursor = 1;

        // run
        int beginningTokenEnd = mSpanChipTokenizer.findTokenEnd(SINGLE_CHARACTER, beginningCursor);
        int endTokenEnd = mSpanChipTokenizer.findTokenEnd(SINGLE_CHARACTER, endCursor);

        // verify
        assertThat(beginningTokenEnd).isEqualTo(1);
        assertThat(endTokenEnd).isEqualTo(1);
    }

    @Test
    public void testFindTokenEnd_singleToken() {
        // setup
        int beginningCursor = 0;
        int middleCursor = SINGLE_TOKEN.length() / 2;
        int endCursor = SINGLE_TOKEN.length() - 1;

        // run
        int beginningTokenEnd = mSpanChipTokenizer.findTokenEnd(SINGLE_TOKEN, beginningCursor);
        int middleTokenEnd = mSpanChipTokenizer.findTokenEnd(SINGLE_TOKEN, middleCursor);
        int endTokenEnd = mSpanChipTokenizer.findTokenEnd(SINGLE_TOKEN, endCursor);

        // verify
        int expectedTokenEnd = SINGLE_TOKEN.length();
        assertThat(beginningTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(middleTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(endTokenEnd).isEqualTo(expectedTokenEnd);
    }

    @Test
    public void testFindTokenEnd_chipThenToken() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(singleTokenChipified);
        text.append(SINGLE_TOKEN);
        int firstIndexOfToken = singleTokenChipified.length();

        int beginningCursor = singleTokenChipified.length();
        int middleCursor = firstIndexOfToken + SINGLE_TOKEN.length() / 2;
        int endCursor = text.length() - 1;

        // run
        int beginningTokenEnd = mSpanChipTokenizer.findTokenEnd(text, beginningCursor);
        int middleTokenEnd = mSpanChipTokenizer.findTokenEnd(text, middleCursor);
        int endTokenEnd = mSpanChipTokenizer.findTokenEnd(text, endCursor);

        // verify
        int expectedTokenEnd = text.length();
        assertThat(beginningTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(middleTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(endTokenEnd).isEqualTo(expectedTokenEnd);
    }

    @Test
    public void testFindTokenEnd_tokenThenChip() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(SINGLE_TOKEN);
        text.append(singleTokenChipified);
        int firstIndexOfToken = 0;

        int beginningCursor = 0;
        int middleCursor = SINGLE_TOKEN.length() / 2;
        int endCursor = SINGLE_TOKEN.length();

        // run
        int beginningTokenEnd = mSpanChipTokenizer.findTokenEnd(text, beginningCursor);
        int middleTokenEnd = mSpanChipTokenizer.findTokenEnd(text, middleCursor);
        int endTokenEnd = mSpanChipTokenizer.findTokenEnd(text, endCursor);

        // verify
        int expectedTokenEnd = firstIndexOfToken + SINGLE_TOKEN.length();
        assertThat(beginningTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(middleTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(endTokenEnd).isEqualTo(expectedTokenEnd);
    }

    @Test
    public void testFindTokenEnd_tokenBetweenChips() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(singleTokenChipified);
        text.append(SINGLE_TOKEN);
        text.append(singleTokenChipified);
        int firstIndexOfToken = singleTokenChipified.length();

        int beginningCursor = singleTokenChipified.length();
        int middleCursor = firstIndexOfToken + SINGLE_TOKEN.length() / 2;
        int endCursor = firstIndexOfToken + SINGLE_TOKEN.length();

        // run
        int beginningTokenEnd = mSpanChipTokenizer.findTokenEnd(text, beginningCursor);
        int middleTokenEnd = mSpanChipTokenizer.findTokenEnd(text, middleCursor);
        int endTokenEnd = mSpanChipTokenizer.findTokenEnd(text, endCursor);

        // verify
        int expectedTokenEnd = firstIndexOfToken + SINGLE_TOKEN.length();
        assertThat(beginningTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(middleTokenEnd).isEqualTo(expectedTokenEnd);
        assertThat(endTokenEnd).isEqualTo(expectedTokenEnd);
    }

    @Test
    public void testTerminateToken() {
        // run
        CharSequence terminatedToken = mSpanChipTokenizer.terminateToken(SINGLE_TOKEN, null);

        // verify
        assertThat(terminatedToken.toString()).isEqualTo(manualCreateChipText(SINGLE_TOKEN).toString());
    }

    @Test
    public void testTerminateToken_withData() {
        // run
        Object data = new Object();
        CharSequence terminatedToken = mSpanChipTokenizer.terminateToken(SINGLE_TOKEN, data);

        // verify
        assertThat(terminatedToken.toString()).isEqualTo(manualCreateChipText(SINGLE_TOKEN).toString());
        assertThat(getSingleChip(new SpannableString(terminatedToken)).getData()).isSameAs(data);
    }

    @Test
    public void testTerminateToken_extraWhitespace() {
        // setup
        SpannableStringBuilder text = new SpannableStringBuilder(SINGLE_TOKEN);
        text.insert(0, WHITESPACE);
        text.append(WHITESPACE);

        // run
        CharSequence terminatedToken = mSpanChipTokenizer.terminateToken(SINGLE_TOKEN, null);

        // verify
        assertThat(terminatedToken.toString()).isEqualTo(manualCreateChipText(SINGLE_TOKEN).toString());
    }

    @Test
    public void testFindAllTokens() {
        // setup
        CharSequence testText = createTestText(TEST_TOKENS_ALL_VALID, false, TEST_CHIP_VALUES_ALL_VALID, true);

        // run
        List<Pair<Integer, Integer>> tokenIndexes = mSpanChipTokenizer.findAllTokens(testText);

        // verify
        assertThat(tokenIndexes).isNotNull();
        assertThat(tokenIndexes).hasSize(TEST_TOKENS_ALL_VALID.length);
    }

    @Test
    public void testFindAllTokens_emptyString() {
        // run
        List<Pair<Integer, Integer>> tokenIndexes = mSpanChipTokenizer.findAllTokens(EMPTY_STRING);

        // verify
        assertThat(tokenIndexes).isNotNull();
        assertThat(tokenIndexes).hasSize(0);
    }

    @Test
    public void testFindAllTokens_withWhitespace() {
        // setup
        CharSequence testText = createTestText(TEST_TOKENS_ONE_WHITESPACE, false, TEST_CHIP_VALUES_ONE_WHITESPACE, true);

        // run
        List<Pair<Integer, Integer>> tokenIndexes = mSpanChipTokenizer.findAllTokens(testText);

        // verify
        assertThat(tokenIndexes).isNotNull();
        assertThat(tokenIndexes).hasSize(TEST_TOKENS_ONE_WHITESPACE.length - 1);
    }

    @Test
    public void testTerminateAllTokens() {
        // setup
        Editable testText = createTestText(TEST_TOKENS_ALL_VALID, false, TEST_CHIP_VALUES_ALL_VALID, true);

        // run
        mSpanChipTokenizer.terminateAllTokens(testText);

        // verify
        Editable expectedText = createTestText(TEST_TOKENS_ALL_VALID, true, TEST_CHIP_VALUES_ALL_VALID, true);
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
    }

    @Test
    public void testFindChipStart_singleChip() {
        // setup
        Spanned testText = new SpannableStringBuilder(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        int chipStart = mSpanChipTokenizer.findChipStart(chip, testText);

        // verify
        assertThat(chipStart).isEqualTo(0);
    }

    @Test
    public void testFindChipStart_chipThenToken() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        testText.append(SINGLE_TOKEN);
        Chip chip = getSingleChip(testText);

        // run
        int chipStart = mSpanChipTokenizer.findChipStart(chip, testText);

        // verify
        assertThat(chipStart).isEqualTo(0);
    }

    @Test
    public void testFindChipStart_tokenThenChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        int chipStart = mSpanChipTokenizer.findChipStart(chip, testText);

        // verify
        assertThat(chipStart).isEqualTo(SINGLE_TOKEN.length());
    }

    @Test
    public void testFindChipStart_chipBetweenTokens() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(singleTokenChipified);
        testText.append(SINGLE_TOKEN);
        Chip chip = getSingleChip(testText);

        // run
        int chipStart = mSpanChipTokenizer.findChipStart(chip, testText);

        // verify
        assertThat(chipStart).isEqualTo(SINGLE_TOKEN.length());
    }

    @Test
    public void testFindChipEnd_singleChip() {
        // setup
        Spanned testText = new SpannableStringBuilder(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        int chipEnd = mSpanChipTokenizer.findChipEnd(chip, testText);

        // verify
        assertThat(chipEnd).isEqualTo(testText.length());
    }

    @Test
    public void testFindChipEnd_chipThenToken() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        testText.append(SINGLE_TOKEN);
        Chip chip = getSingleChip(testText);

        // run
        int chipEnd = mSpanChipTokenizer.findChipEnd(chip, testText);

        // verify
        assertThat(chipEnd).isEqualTo(singleTokenChipified.length());
    }

    @Test
    public void testFindChipEnd_tokenThenChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        int chipEnd = mSpanChipTokenizer.findChipEnd(chip, testText);

        // verify
        assertThat(chipEnd).isEqualTo(testText.length());
    }

    @Test
    public void testFindChipEnd_chipBetweenTokens() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(singleTokenChipified);
        testText.append(SINGLE_TOKEN);
        Chip chip = getSingleChip(testText);

        // run
        int chipStart = mSpanChipTokenizer.findChipEnd(chip, testText);

        // verify
        assertThat(chipStart).isEqualTo(SINGLE_TOKEN.length() + singleTokenChipified.length());
    }

    @Test
    public void testFindAllChips() {
        // setup
        Spanned testText = createTestText(TEST_TOKENS_ALL_VALID, false, TEST_CHIP_VALUES_ALL_VALID, true);

        // run
        Chip[] chips = mSpanChipTokenizer.findAllChips(0, testText.length(), testText);

        // run
        assertThat(chips).isNotNull();
        assertThat(chips).hasSize(TEST_CHIP_VALUES_ALL_VALID.length);
    }

    @Test
    public void testFindAllChips_emptyString() {
        // setup
        Spanned testText = new SpannableStringBuilder(EMPTY_STRING);

        // run
        Chip[] chips = mSpanChipTokenizer.findAllChips(0, 0, testText);

        // verify
        assertThat(chips).isNotNull();
        assertThat(chips).isEmpty();
    }

    @Test
    public void testFindAllChips_singleToken() {
        // setup
        Spanned testText = new SpannableStringBuilder(SINGLE_TOKEN);

        // run
        Chip[] chips = mSpanChipTokenizer.findAllChips(0, SINGLE_TOKEN.length(), testText);

        // verify
        assertThat(chips).isNotNull();
        assertThat(chips).isEmpty();
    }

    @Test
    public void testRevertChipToToken_singleChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.revertChipToToken(chip, testText);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN.toString());
    }

    @Test
    public void testRevertChipToToken_tokenThenChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN_2);
        testText.append(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.revertChipToToken(chip, testText);

        // verify
        CharSequence expectedText = SINGLE_TOKEN_2.toString() + SINGLE_TOKEN.toString();
        assertThat(testText.toString()).isEqualTo(expectedText);
    }

    @Test
    public void testRevertChipToToken_chipThenToken() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        testText.append(SINGLE_TOKEN_2);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.revertChipToToken(chip, testText);

        // verify
        CharSequence expectedText = SINGLE_TOKEN.toString() + SINGLE_TOKEN_2.toString();
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
    }

    @Test
    public void testRevertChipToToken_chipBetweenTokens() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN_2);
        testText.append(singleTokenChipified);
        testText.append(SINGLE_TOKEN_3);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.revertChipToToken(chip, testText);

        // verify
        CharSequence expectedText = SINGLE_TOKEN_2.toString() + SINGLE_TOKEN.toString() + SINGLE_TOKEN_3.toString();
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
    }

    @Test
    public void testDeleteChip_singleChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.deleteChip(chip, testText);

        // verify
        assertThat(testText).isEmpty();
    }

    @Test
    public void testDeleteChip_tokenThenChip() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN_2);
        testText.append(singleTokenChipified);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.deleteChip(chip, testText);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN_2.toString());
    }

    @Test
    public void testDeleteChip_chipThenToken() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(singleTokenChipified);
        testText.append(SINGLE_TOKEN_2);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.deleteChip(chip, testText);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN_2.toString());
    }

    @Test
    public void testDeleteChip_chipBetweenTokens() {
        // setup
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN_2);
        testText.append(singleTokenChipified);
        testText.append(SINGLE_TOKEN_3);
        Chip chip = getSingleChip(testText);

        // run
        mSpanChipTokenizer.deleteChip(chip, testText);

        // verify
        CharSequence expectedText = SINGLE_TOKEN_2.toString() + SINGLE_TOKEN_3.toString();
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
    }

    private ChipConfiguration createTestChipConfiguration() {
        return new ChipConfiguration(-1, null, -1, -1, -1, -1, -1, -1);
    }

    private SpannableStringBuilder createTestText(CharSequence[] evens, boolean chipifyEvens, CharSequence[] odds, boolean chipifyOdds) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        for (int i = 0; i < evens.length; i++) {
            text.append(chipifyEvens ? createChipText(evens[i]) : evens[i]);
            text.append(chipifyOdds ? createChipText(odds[i]) : odds[i]);
        }
        return text;
    }

    private Chip getSingleChip(Spanned text) {
        return text.getSpans(0, text.length(), Chip.class)[0];
    }

    private CharSequence createChipText(CharSequence text) {
        return mSpanChipTokenizer.terminateToken(text, null);
    }

    private static CharSequence manualCreateChipText(CharSequence text) {
        return " " + SpanChipTokenizer.CHIP_SPAN_SEPARATOR + text + SpanChipTokenizer.CHIP_SPAN_SEPARATOR + " ";
    }
}
