package com.hootsuite.nachos.matchers;

import org.mockito.ArgumentMatcher;

public class IntegerMatchers {

    public static BetweenComparison between(int lowerLimit, int upperLimit) {
        return new BetweenComparison(lowerLimit, upperLimit);
    }

    static class BetweenComparison extends ArgumentMatcher<Integer> {

        private int mLowerLimit;
        private int mUpperLimit;

        public BetweenComparison(int lowerLimit, int upperLimit) {
            mLowerLimit = lowerLimit;
            mUpperLimit = upperLimit;
        }

        @Override
        public boolean matches(Object argument) {
            int argInt = (int)argument;
            return mLowerLimit <= argInt && argInt <= mUpperLimit;
        }
    }
}
