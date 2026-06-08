package com.campushelper.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    void encode_shouldReturnHashedPassword() {
        String raw = "abc123";
        String encoded = PasswordEncoder.encode(raw);
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    void matches_shouldReturnTrueForCorrectPassword() {
        String raw = "abc123";
        String encoded = PasswordEncoder.encode(raw);
        assertTrue(PasswordEncoder.matches(raw, encoded));
    }

    @Test
    void matches_shouldReturnFalseForWrongPassword() {
        String encoded = PasswordEncoder.encode("correct");
        assertFalse(PasswordEncoder.matches("wrong", encoded));
    }
}
