package com.example.securescan.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.securescan.data.network.FirebaseAuthService


class AuthViewModel(private val userRepository: FirebaseAuthService) : ViewModel() {

    var loginSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Email and password cannot be empty"
            return
        }
        else {
            userRepository.login(email, password) { success, error ->
                if (success) {
                    loginSuccess.value = true
                    errorMessage.value = null
                } else {
                    loginSuccess.value = false
                    errorMessage.value = error
                }
            }
        }
    }

    var registerSuccess = mutableStateOf(false)
    var errorMessage2 = mutableStateOf<String?>(null)

    fun register(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage2.value = "Email and password cannot be empty"
            return
        }
        else {
            userRepository.register(email, password) { success, error ->
                if (success) {
                    registerSuccess.value = true
                    errorMessage2.value = null
                } else {
                    registerSuccess.value = false
                    errorMessage2.value = error
                }
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.getCurrentUser() != null
    }
    fun logout() {
        userRepository.logout()
        loginSuccess.value = false
    }

}
