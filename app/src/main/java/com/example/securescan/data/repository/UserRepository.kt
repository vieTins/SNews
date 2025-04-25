package com.example.securescan.data.repository

import com.example.securescan.data.network.FirebaseAuthService

class UserRepository(private val authService: FirebaseAuthService) {

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authService.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authService.register(email, password, callback)
    }


    fun getCurrentUser() = authService.getCurrentUser()

    fun logout() = authService.logout()
}