package com.hootsuite.nachos;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.terminator.DefaultChipTerminatorHandler;
import com.hootsuite.nachos.tokenizer.ChipTokenizer;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;

import junit.framework.TestCase;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

import static com.hootsuite.nachos.matchers.CharSequenceMatchers.toStringEq;
import static com.hootsuite.nachos.matchers.IntegerMatchers.between;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DefaultChipTerminatorHandlerTest extends TestCase {

    private static final char CHIPIFY_ALL_CHAR = '\n';
    private static final char CHIPIFY_CURRENT_TOKEN_CHAR = ';';
    private static final char CHIPIFY_TO_TERMINATOR_CHAR = ' ';

    private static final CharSequence EMPTY_STRING = "";
    private static final CharSequence SINGLE_TOKEN = "token";
    private static final CharSequence SINGLE_TOKEN_2 = "token2";
    private static final CharSequence SINGLE_TOKEN_3 = "token3";
    private static final CharSequence SINGLE_TOKEN_CHIPIFIED = manualCreateChipText(SINGLE_TOKEN);
    private static final CharSequence SINGLE_TOKEN_2_CHIPIFIED = manualCreateChipText(SINGLE_TOKEN_2);
    private static final CharSequence SINGLE_TOKEN_3_CHIPIFIED = manualCreateChipText(SINGLE_TOKEN_3);
    private static final CharSequence CHIP = manualCreateChipText("chip");
    private static final CharSequence CHIP_2 = manualCreateChipText("chip2");
    private static final CharSequence CHIP_3 = manualCreateChipText("chip3");

    private ChipTokenizer mChipTokenizer;
    private DefaultChipTerminatorHandler mDefaultChipTerminatorHandler;

    @Before
    public void setup() {
        mChipTokenizer = Mockito.mock(ChipTokenizer.class);
        mDefaultChipTerminatorHandler = new DefaultChipTerminatorHandler();
    }

    private void setupTerminators() {
        mDefaultChipTerminatorHandler.addChipTerminator(CHIPIFY_ALL_CHAR, ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
        mDefaultChipTerminatorHandler.addChipTerminator(CHIPIFY_CURRENT_TOKEN_CHAR, ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
        mDefaultChipTerminatorHandler.addChipTerminator(CHIPIFY_TO_TERMINATOR_CHAR, ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
    }

    private void setupTokenizerForToken(ChipTokenizer chipTokenizerMock, CharSequence token, final CharSequence chipifiedToken) {
        when(chipTokenizerMock.findTokenStart(any(CharSequence.class), anyInt())).thenReturn(0);
        when(chipTokenizerMock.findTokenEnd(any(CharSequence.class), anyInt())).thenReturn(token.length());
        when(chipTokenizerMock.terminateToken(argThat(toStringEq(token)), any())).thenReturn(chipifiedToken);
    }

    @Test
    public void testFindAndHandleChipTerminators_emptyString() {
        // setup
        setupTerminators();
        SpannableStringBuilder testText = new SpannableStringBuilder(EMPTY_STRING);

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(mChipTokenizer, testText, 0, 0, false);

        // verify
        assertThat(testText).isEmpty();
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_singleTokenSingleCharChipifyAll() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);
        setupTokenizerForToken(chipTokenizer, SINGLE_TOKEN, SINGLE_TOKEN_CHIPIFIED);

        int singleTokenLength = SINGLE_TOKEN.length();
        SpannableStringBuilder testText1 = new SpannableStringBuilder(SINGLE_TOKEN);
        testText1.append(CHIPIFY_ALL_CHAR);

        SpannableStringBuilder testText2 = new SpannableStringBuilder(SINGLE_TOKEN);
        testText2.insert(0, Character.toString(CHIPIFY_ALL_CHAR));

        SpannableStringBuilder testText3 = new SpannableStringBuilder(SINGLE_TOKEN);
        int middleIndex = (SINGLE_TOKEN.length() / 2);
        testText3.insert(middleIndex, Character.toString(CHIPIFY_ALL_CHAR));

        // run
        int selection1 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText1, singleTokenLength, singleTokenLength + 1, false);
        int selection2 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText2, 0, 1, false);
        int selection3 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText3, middleIndex, middleIndex + 1, false);

        // verify
        verify(chipTokenizer, times(3)).terminateAllTokens(any(Editable.class));
        assertThat(selection1).isEqualTo(testText1.length());
        assertThat(selection2).isEqualTo(testText2.length());
        assertThat(selection3).isEqualTo(testText3.length());
    }

    @Test
    public void testFindAndHandleChipTerminators_singleTokenSingleCharChipifyCurrentToken() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);
        setupTokenizerForToken(chipTokenizer, SINGLE_TOKEN, SINGLE_TOKEN_CHIPIFIED);

        int singleTokenLength = SINGLE_TOKEN.length();
        SpannableStringBuilder testText1 = new SpannableStringBuilder(SINGLE_TOKEN);
        testText1.append(CHIPIFY_CURRENT_TOKEN_CHAR);

        SpannableStringBuilder testText2 = new SpannableStringBuilder(SINGLE_TOKEN);
        testText2.insert(0, Character.toString(CHIPIFY_CURRENT_TOKEN_CHAR));

        SpannableStringBuilder testText3 = new SpannableStringBuilder(SINGLE_TOKEN);
        int middleIndex = (SINGLE_TOKEN.length() / 2);
        testText3.insert(middleIndex, Character.toString(CHIPIFY_CURRENT_TOKEN_CHAR));

        // run
        int selection1 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText1, singleTokenLength, singleTokenLength + 1, false);
        int selection2 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText2, 0, 1, false);
        int selection3 = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText3, middleIndex, middleIndex + 1, false);

        // verify
        assertThat(testText1.toString()).isEqualTo(SINGLE_TOKEN_CHIPIFIED.toString());
        assertThat(testText2.toString()).isEqualTo(SINGLE_TOKEN_CHIPIFIED.toString());
        assertThat(testText3.toString()).isEqualTo(SINGLE_TOKEN_CHIPIFIED.toString());
        assertThat(selection1).isEqualTo(testText1.length());
        assertThat(selection2).isEqualTo(testText2.length());
        assertThat(selection3).isEqualTo(testText3.length());
    }

    @Test
    public void testFindAndHandleChipTerminators_singleTokenSingleCharChipifyToTerminatorAtBeginning() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);
        setupTokenizerForToken(chipTokenizer, SINGLE_TOKEN, SINGLE_TOKEN_CHIPIFIED);

        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.insert(0, Character.toString(CHIPIFY_TO_TERMINATOR_CHAR));

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, 0, 1, false);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN);
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_singleTokenSingleCharChipifyToTerminatorAtEnd() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);
        setupTokenizerForToken(chipTokenizer, SINGLE_TOKEN, SINGLE_TOKEN_CHIPIFIED);

        int singleTokenLength = SINGLE_TOKEN.length();
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(Character.toString(CHIPIFY_TO_TERMINATOR_CHAR));

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, singleTokenLength, singleTokenLength + 1, false);

        // verify
        assertThat(testText.toString()).isEqualTo(SINGLE_TOKEN_CHIPIFIED);
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_singleTokenSingleCharChipifyToTerminatorAtMiddle() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);
        int middleIndex = (SINGLE_TOKEN.length() / 2);
        CharSequence partialToken = SINGLE_TOKEN.subSequence(0, middleIndex);
        CharSequence partialChip = manualCreateChipText(partialToken);
        CharSequence remainingToken = SINGLE_TOKEN.subSequence(middleIndex, SINGLE_TOKEN.length());
        setupTokenizerForToken(chipTokenizer, partialToken, partialChip);

        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.insert(middleIndex, Character.toString(CHIPIFY_TO_TERMINATOR_CHAR));

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, middleIndex, middleIndex + 1, false);

        // verify
        CharSequence expectedText = partialChip.toString() + remainingToken.toString();
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_multipleTokensSingleCharChipifyAll() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);

        SpannableStringBuilder testText = createTokenAndChipTestText();
        testText.append(CHIPIFY_ALL_CHAR);

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, testText.length() - 1, testText.length(), false);

        // verify
        verify(chipTokenizer, times(1)).terminateAllTokens(any(Editable.class));
        assertThat(selection).isEqualTo(testText.length());
    }

    @Test
    public void testFindAndHandleChipTerminators_multipleTokensSingleCharChipifyCurrentToken() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);

        SpannableStringBuilder testText = createTokenAndChipTestText();
        testText.insert(0, Character.toString(CHIPIFY_CURRENT_TOKEN_CHAR));

        int firstTokenStart = 0;
        int firstTokenEnd = SINGLE_TOKEN.length();

        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(Matchers.lessThanOrEqualTo(firstTokenEnd)))).thenReturn(firstTokenStart);
        when(chipTokenizer.findTokenEnd(any(CharSequence.class), intThat(Matchers.lessThanOrEqualTo(firstTokenEnd)))).thenReturn(firstTokenEnd);
        when(chipTokenizer.terminateToken(argThat(toStringEq(SINGLE_TOKEN)), any())).thenReturn(SINGLE_TOKEN_CHIPIFIED);

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, 0, 1, false);

        // verify
        SpannableStringBuilder expectedText = new SpannableStringBuilder(SINGLE_TOKEN_CHIPIFIED);
        expectedText.append(CHIP);
        expectedText.append(SINGLE_TOKEN_2);
        expectedText.append(CHIP_2);
        expectedText.append(SINGLE_TOKEN_3);
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
        assertThat(selection).isEqualTo(SINGLE_TOKEN_CHIPIFIED.length());
    }

    @Test
    public void testFindAndHandleChipTerminators_multipleTokensSingleCharChipifyToTerminator() {
        // setup
        setupTerminators();

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);

        SpannableStringBuilder testText = createTokenAndChipTestText();
        int token2Start = SINGLE_TOKEN.length() + CHIP.length();
        int token2End = token2Start + SINGLE_TOKEN_2.length();
        int middleOfToken2 = SINGLE_TOKEN.length() + CHIP.length() + (SINGLE_TOKEN_2.length() / 2);

        CharSequence partialToken = testText.subSequence(token2Start, middleOfToken2);
        CharSequence partialChip = manualCreateChipText(partialToken);
        CharSequence remainingToken = testText.subSequence(middleOfToken2, token2End);

        testText.insert(middleOfToken2, Character.toString(CHIPIFY_TO_TERMINATOR_CHAR));

        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(between(token2Start, token2End)))).thenReturn(token2Start);
        when(chipTokenizer.findTokenEnd(any(CharSequence.class), intThat(between(token2Start, token2End)))).thenReturn(token2End);
        when(chipTokenizer.terminateToken(argThat(toStringEq(partialToken)), any())).thenReturn(partialChip);

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, middleOfToken2, middleOfToken2 + 1, false);

        // verify
        SpannableStringBuilder expectedText = new SpannableStringBuilder(SINGLE_TOKEN);
        expectedText.append(CHIP);
        expectedText.append(partialChip);
        expectedText.append(remainingToken);
        expectedText.append(CHIP_2);
        expectedText.append(SINGLE_TOKEN_3);
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_noTerminators() {
        // setup
        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);

        SpannableStringBuilder testText = createTokenAndChipTestText();

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, 0, testText.length(), false);

        // verify
        verify(chipTokenizer, never()).terminateToken(any(CharSequence.class), any());
        verify(chipTokenizer, never()).terminateAllTokens(any(Editable.class));
        assertThat(selection).isLessThan(0);
    }

    @Test
    public void testFindAndHandleChipTerminators_pasteEvent() {
        // setup
        setupTerminators();
        mDefaultChipTerminatorHandler.setPasteBehavior(ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);

        ChipTokenizer chipTokenizer = mock(ChipTokenizer.class);

        SpannableStringBuilder testText = createTestPasteText();

        int token1Start = 0;
        int token1End = SINGLE_TOKEN.length();
        int token2Start = SINGLE_TOKEN_CHIPIFIED.length();
        int token2End = token2Start + SINGLE_TOKEN_2.length();
        int token3Start = token2Start + SINGLE_TOKEN_2_CHIPIFIED.length();
        int token3End = token3Start + SINGLE_TOKEN_3.length();
        int afterToken3 = token3Start + SINGLE_TOKEN_3_CHIPIFIED.length();

        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(Matchers.lessThanOrEqualTo(token1End)))).thenReturn(token1Start);
        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(between(token2Start, token2End)))).thenReturn(token2Start);
        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(between(token3Start, token3End)))).thenReturn(token3Start);
        when(chipTokenizer.findTokenStart(any(CharSequence.class), intThat(Matchers.greaterThanOrEqualTo(afterToken3)))).thenReturn(afterToken3);
        doAnswer(new Answer<CharSequence>() {

            @Override
            public CharSequence answer(InvocationOnMock invocation) throws Throwable {
                return manualCreateChipText((CharSequence) invocation.getArguments()[0]);
            }
        }).when(chipTokenizer).terminateToken(any(CharSequence.class), any());

        // run
        int selection = mDefaultChipTerminatorHandler.findAndHandleChipTerminators(chipTokenizer, testText, 0, testText.length(), true);

        // verify
        SpannableStringBuilder expectedText = new SpannableStringBuilder(SINGLE_TOKEN_CHIPIFIED);
        expectedText.append(SINGLE_TOKEN_2_CHIPIFIED);
        expectedText.append(SINGLE_TOKEN_3_CHIPIFIED);
        assertThat(testText.toString()).isEqualTo(expectedText.toString());
        assertThat(selection).isLessThan(0);
    }

    private static SpannableStringBuilder createTestPasteText() {
        SpannableStringBuilder testText = new SpannableStringBuilder();
        testText.append(CHIPIFY_ALL_CHAR);
        testText.append(CHIPIFY_ALL_CHAR);
        testText.append(SINGLE_TOKEN);
        testText.append(CHIPIFY_ALL_CHAR);
        testText.append(SINGLE_TOKEN_2);
        testText.append(CHIPIFY_CURRENT_TOKEN_CHAR);
        testText.append(SINGLE_TOKEN_3);
        testText.append(CHIPIFY_TO_TERMINATOR_CHAR);
        testText.append(CHIPIFY_ALL_CHAR);
        testText.append(CHIPIFY_ALL_CHAR);
        return testText;
    }

    private static SpannableStringBuilder createTokenAndChipTestText() {
        SpannableStringBuilder testText = new SpannableStringBuilder(SINGLE_TOKEN);
        testText.append(CHIP);
        testText.append(SINGLE_TOKEN_2);
        testText.append(CHIP_2);
        testText.append(SINGLE_TOKEN_3);
        return testText;
    }

    private static CharSequence manualCreateChipText(CharSequence text) {
        return " " + SpanChipTokenizer.CHIP_SPAN_SEPARATOR + text + SpanChipTokenizer.CHIP_SPAN_SEPARATOR + " ";
    }
}
