package com.hootsuite.nachos;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

/**
 * Custom Robolectric test runner for Nachos tests.
 * Updated for modern Robolectric version.
 */
public class CustomRobolectricRunner extends RobolectricTestRunner {

    public CustomRobolectricRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}
