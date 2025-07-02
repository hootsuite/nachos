package com.hootsuite.nachos.matchers

import org.mockito.ArgumentMatcher

object CharSequenceMatchers {
    @JvmStatic
    fun toStringEq(expected: CharSequence): ArgumentMatcher<CharSequence> =
        ArgumentMatcher { argument -> expected.toString() == argument.toString() }
} 