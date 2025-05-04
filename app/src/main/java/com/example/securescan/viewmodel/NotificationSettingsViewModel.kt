package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationSettings(
    val allNotificationsEnabled: Boolean = true,
    val newsEnabled: Boolean = true,
    val warningEnabled: Boolean = true,
    val updateEnabled: Boolean = true,
    val securityEnabled: Boolean = true
)

class NotificationSettingsViewModel : ViewModel() {
    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    fun toggleAllNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _notificationSettings.value = _notificationSettings.value.copy(
                allNotificationsEnabled = enabled,
                newsEnabled = enabled,
                warningEnabled = enabled,
                updateEnabled = enabled,
                securityEnabled = enabled
            )
            // TODO: Save settings to repository or local storage
        }
    }

    fun toggleNotificationType(type: NotificationType, enabled: Boolean) {
        viewModelScope.launch {
            _notificationSettings.value = _notificationSettings.value.copy(
                newsEnabled = if (type == NotificationType.NEWS) enabled else _notificationSettings.value.newsEnabled,
                warningEnabled = if (type == NotificationType.WARNING) enabled else _notificationSettings.value.warningEnabled,
                updateEnabled = if (type == NotificationType.UPDATE) enabled else _notificationSettings.value.updateEnabled,
                securityEnabled = if (type == NotificationType.SECURITY) enabled else _notificationSettings.value.securityEnabled
            )
            // TODO: Save settings to repository or local storage
        }
    }
} 