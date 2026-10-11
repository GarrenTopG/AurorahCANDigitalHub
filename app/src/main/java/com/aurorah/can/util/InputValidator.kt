package com.aurorah.can.util

object InputValidator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val passwordRegex = Regex("^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return emailRegex.matches(email)
    }

    fun isSecurePassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        return passwordRegex.matches(password)
    }
}
