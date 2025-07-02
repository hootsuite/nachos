package com.hootsuite.nachos.matchers;

import org.mockito.ArgumentMatcher;

public class IntegerMatchers {

    public static BetweenComparison between(int lowerLimit, int upperLimit) {
        return new BetweenComparison(lowerLimit, upperLimit);
    }

    static class BetweenComparison implements ArgumentMatcher<Integer> {

        private int mLowerLimit;
        private int mUpperLimit;

        public BetweenComparison(int lowerLimit, int upperLimit) {
            mLowerLimit = lowerLimit;
            mUpperLimit = upperLimit;
        }

        @Override
        public boolean matches(Integer argument) {
            return mLowerLimit <= argument && argument <= mUpperLimit;
        }
    }
}
