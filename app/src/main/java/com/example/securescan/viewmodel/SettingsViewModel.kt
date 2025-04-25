package com.example.securescan.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.securescan.data.network.FirebaseAuthService

class SettingsViewModel : ViewModel() {
    private val authService = FirebaseAuthService()

    private val _isLoggedOut = mutableStateOf(false)
    val isLoggedOut: State<Boolean> = _isLoggedOut

}