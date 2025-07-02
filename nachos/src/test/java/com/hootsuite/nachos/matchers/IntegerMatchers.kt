package com.hootsuite.nachos.matchers

import org.mockito.ArgumentMatcher

object IntegerMatchers {
    @JvmStatic
    fun between(lower: Int, upper: Int): ArgumentMatcher<Int> =
        ArgumentMatcher { arg -> lower <= arg && arg <= upper }
} 