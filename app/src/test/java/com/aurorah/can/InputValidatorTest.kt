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
    fun email_NullOrBlank_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail(null))
        assertFalse(InputValidator.isValidEmail(""))
        assertFalse(InputValidator.isValidEmail("   "))
    }

    @Test
    fun invalidEmail_MissingAtSign_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail("volunteer.aurorahcan.org"))
    }

    @Test
    fun password_TooShort_ReturnsFalse() {
        assertFalse(InputValidator.isSecurePassword("Pass1!"))
    }

    @Test
    fun password_NullOrBlank_ReturnsFalse() {
        assertFalse(InputValidator.isSecurePassword(null))
        assertFalse(InputValidator.isSecurePassword(""))
        assertFalse(InputValidator.isSecurePassword("   "))
    }

    @Test
    fun password_MissingRequiredDigitOrSpecialCharacter_ReturnsFalse() {
        assertFalse(InputValidator.isSecurePassword("SecurePassword!"))
        assertFalse(InputValidator.isSecurePassword("SecurePassword1"))
    }

    @Test
    fun password_ValidComplexity_ReturnsTrue() {
        assertTrue(InputValidator.isSecurePassword("SecurePass2026!"))
    }
}
