package com.hootsuite.nachos.validator

/** Determines whether a character is disallowed while typing into a NachoTextView. */
interface IllegalCharacterIdentifier {
    fun isCharacterIllegal(c: Char): Boolean
} 