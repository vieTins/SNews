package com.example.securescan.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.securescan.data.network.FirebaseAuthService

class AuthViewModel(private val userRepository: FirebaseAuthService) : ViewModel() {

    var loginSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Email và mật khẩu không được để trống"
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

    fun register(email: String, password: String, name: String, phone: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            errorMessage2.value = "Vui lòng điền đầy đủ thông tin"
            return
        }
        else {
            userRepository.register(email, password, name, phone) { success, error ->
                if (success) {
                    // Tự động đăng nhập sau khi đăng ký thành công
                    login(email, password)
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