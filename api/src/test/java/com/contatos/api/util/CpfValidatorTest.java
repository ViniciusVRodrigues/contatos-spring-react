package com.contatos.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

    @Test
    void shouldValidateValidCpf() {
        assertTrue(CpfValidator.isValid("11144477735"));
        assertTrue(CpfValidator.isValid("12345678909"));
    }

    @Test
    void shouldRejectInvalidCpf() {
        assertFalse(CpfValidator.isValid("11144477736")); // Wrong check digit
        assertFalse(CpfValidator.isValid("12345678908")); // Wrong check digit
    }

    @Test
    void shouldRejectCpfWithSameDigits() {
        assertFalse(CpfValidator.isValid("11111111111"));
        assertFalse(CpfValidator.isValid("00000000000"));
        assertFalse(CpfValidator.isValid("99999999999"));
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertFalse(CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("123"));
        assertFalse(CpfValidator.isValid("123456789012")); // Too long
        assertFalse(CpfValidator.isValid("1234567890a")); // Contains letter
    }
}
