package com.example.securescan.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.ReportItem
import com.example.securescan.data.network.CloudinaryUploader
import com.example.securescan.data.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportsViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _userReports = mutableStateOf<List<ReportItem>>(emptyList())
    val userReports: State<List<ReportItem>> = _userReports

    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess


    fun loadReportsByUserId(userId: String) {
        isLoading.value = true
        repository.getReportsByUserId(
            userId,
            onSuccess = {
                _userReports.value = it
                isLoading.value = false
                error.value = null
            },
            onFailure = {
                error.value = it.message
                isLoading.value = false
            }
        )
    }

    fun filterReportByType(
        type: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        isLoading.value = true
        repository.filterReportByType(
            type,
            onSuccess = { filtered ->
                _userReports.value = filtered
                isLoading.value = false
                error.value = null
                onSuccess()
            },
            onFailure = {
                error.value = it.message
                isLoading.value = false
                onFailure(it)
            }
        )
    }

    fun submitReport(
        report: ReportItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
       repository.submitReport(report, onSuccess, onFailure)
    }

    fun loadAllReports() {
        isLoading.value = true
        repository.getAllReports(
            onSuccess = {
                _userReports.value = it
                isLoading.value = false
                error.value = null
            },
            onFailure = {
                error.value = it.message
                isLoading.value = false
            }
        )
    }

}

