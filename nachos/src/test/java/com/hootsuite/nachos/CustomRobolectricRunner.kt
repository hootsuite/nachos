package com.hootsuite.nachos

import org.robolectric.RobolectricTestRunner

/**
 * Kotlin replacement for the custom Robolectric test runner used by Nachos unit tests.
 */
class CustomRobolectricRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) 