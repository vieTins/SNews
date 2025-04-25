package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.ScanHistory
import com.example.securescan.data.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScanHistoryViewModel : ViewModel() {
    private val repository = ScanHistoryRepository()
    private val _scanHistory = MutableStateFlow<List<ScanHistory>>(emptyList())
    val scanHistory: StateFlow<List<ScanHistory>> = _scanHistory

    fun fetchScanHistory(userId: String) {
        viewModelScope.launch {
            _scanHistory.value = repository.getScanHistory(userId)
        }
    }
}
