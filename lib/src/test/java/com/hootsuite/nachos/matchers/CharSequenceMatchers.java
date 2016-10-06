package com.hootsuite.nachos.matchers;

import org.mockito.ArgumentMatcher;

public class CharSequenceMatchers {

    public static ToStringComparison toStringEq(CharSequence expected) {
        return new ToStringComparison(expected);
    }

    static class ToStringComparison extends ArgumentMatcher<CharSequence> {

        private CharSequence mExpected;

        public ToStringComparison(CharSequence expected) {
            mExpected = expected;
        }

        @Override
        public boolean matches(Object argument) {
            return mExpected.toString().equals(argument.toString());
        }
    }
}
