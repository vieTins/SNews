package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ThemeViewModel : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadThemePreference()
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val userDoc = firestore.collection("users").document(userId).get().await()
                _isDarkMode.value = userDoc.getBoolean("isDarkMode") ?: false
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                _isDarkMode.value = !_isDarkMode.value
                firestore.collection("users").document(userId)
                    .update("isDarkMode", _isDarkMode.value)
                    .await()
            } catch (e: Exception) {
                // Revert state if update fails
                _isDarkMode.value = !_isDarkMode.value
            }
        }
    }
}