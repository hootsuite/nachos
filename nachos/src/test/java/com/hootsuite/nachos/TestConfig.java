package com.hootsuite.nachos;

/**
 * Configuration constants for tests.
 */
public final class TestConfig {
    
    /**
     * SDK version to use for Robolectric tests.
     * This should match the testSdkVersion defined in the root build.gradle.
     */
    public static final int SDK_VERSION = 28;
    
    private TestConfig() {
        // Utility class - no instantiation
    }
} 