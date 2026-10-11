package com.aurorah.can

import com.aurorah.can.util.InputValidator
import org.junit.Assert.*
import org.junit.Test

class InputValidatorTest {

    @Test
    fun validEmail_ReturnsTrue() {
        assertTrue(InputValidator.isValidEmail("volunteer@aurorahcan.org.za"))
    }

    @Test
    fun invalidEmail_MissingDomain_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail("volunteer@aurorahcan"))
    }

    @Test
    fun password_TooShort_ReturnsFalse() {
        assertFalse(InputValidator.isSecurePassword("Pass1!"))
    }

    @Test
    fun password_ValidComplexity_ReturnsTrue() {
        assertTrue(InputValidator.isSecurePassword("SecurePass2026!"))
    }
}
