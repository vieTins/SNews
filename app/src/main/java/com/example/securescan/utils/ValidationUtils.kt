package com.example.securescan.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean = email.contains("@") && email.contains(".")
    fun isValidPassword(password: String): Boolean = password.length >= 6
}