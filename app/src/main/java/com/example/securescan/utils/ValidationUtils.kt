package com.example.securescan.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        // Password must be at least 8 characters long and contain at least one uppercase letter,
        // one lowercase letter, one number and one special character
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        // Vietnamese phone number format
        val phonePattern = "^(0|\\+84)([35789])\\d{8}$"
        return phone.matches(phonePattern.toRegex())
    }

    fun isValidUrl(url: String): Boolean {
        val urlPattern = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
        return url.matches(urlPattern.toRegex())
    }

    fun isValidIpAddress(ip: String): Boolean {
        val ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        return ip.matches(ipPattern.toRegex())
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        // Remove spaces and dashes
        val cleanCardNumber = cardNumber.replace("\\s+|-".toRegex(), "")
        
        // Check if it's a valid length (16 digits)
        if (!cleanCardNumber.matches("\\d{16}".toRegex())) {
            return false
        }

        // Luhn algorithm for card number validation
        var sum = 0
        var alternate = false
        for (i in cleanCardNumber.length - 1 downTo 0) {
            var n = cleanCardNumber.substring(i, i + 1).toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    fun isValidFileName(fileName: String): Boolean {
        // Check for invalid characters in filename
        val invalidChars = "[<>:\"/\\\\|?*]"
        return !fileName.matches(invalidChars.toRegex())
    }
}