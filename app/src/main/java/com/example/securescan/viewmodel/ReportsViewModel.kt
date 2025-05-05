package com.example.securescan.viewmodel

import android.content.Context
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
import androidx.compose.ui.graphics.Color

class ReportsViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _userReports = mutableStateOf<List<ReportItem>>(emptyList())
    val userReports: State<List<ReportItem>> = _userReports

    private val _currentReport = mutableStateOf<ReportItem?>(null)
    val currentReport: State<ReportItem?> = _currentReport

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _reportCount = mutableStateOf(0)
    val reportCount: State<Int> = _reportCount

    private val _topTargets = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val topTargets: State<List<Pair<String, Int>>> = _topTargets

    val error = mutableStateOf<String?>(null)

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    fun loadReportsByUserId(userId: String) {
        _isLoading.value = true
        repository.getReportsByUserId(
            userId,
            onSuccess = {
                _userReports.value = it
                _isLoading.value = false
                error.value = null
            },
            onFailure = {
                error.value = it.message
                _isLoading.value = false
            }
        )
    }

    fun filterReportByType(
        type: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        _isLoading.value = true
        repository.filterReportByType(
            type,
            onSuccess = { filtered ->
                _userReports.value = filtered
                _isLoading.value = false
                error.value = null
                onSuccess()
            },
            onFailure = {
                error.value = it.message
                _isLoading.value = false
                onFailure(it)
            }
        )
    }

    fun submitReport(
        context: Context,
        report: ReportItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
       viewModelScope.launch {
           repository.submitReport(context, report, onSuccess, onFailure)
       }
    }

    fun loadAllReports() {
        _isLoading.value = true
        repository.getAllReports(
            onSuccess = {
                _userReports.value = it
                _isLoading.value = false
                error.value = null
            },
            onFailure = {
                error.value = it.message
                _isLoading.value = false
            }
        )
    }

    fun getReportById(reportId: String) {
        _isLoading.value = true
        repository.getReportById(
            reportId = reportId,
            onSuccess = { report ->
                _currentReport.value = report
                // Get report count for the target
                getReportCountForTarget(report.target)
            },
            onFailure = { error ->
                // Handle error
                _isLoading.value = false
            }
        )
    }

    private fun getReportCountForTarget(target: String) {
        repository.getReportCountByTarget(
            target = target,
            onSuccess = { count ->
                _reportCount.value = count
                _isLoading.value = false
            },
            onFailure = { error ->
                // Handle error
                _isLoading.value = false
            }
        )
    }

    fun getDangerLevel(): String {
        return when (_reportCount.value) {
            0 -> "Thấp"
            in 1..2 -> "Trung bình"
            in 3..5 -> "Cao"
            else -> "Rất cao"
        }
    }

    fun getDangerLevelColor(): Color {
        return when (_reportCount.value) {
            0 -> Color.Green
            in 1..2 -> Color(0xFFFFA500) // Orange
            in 3..5 -> Color.Red
            else -> Color(0xFF8B0000) // Dark Red
        }
    }

    fun loadTopFraudTargets() {
        repository.getTopFraudTargets(
            onSuccess = { targets ->
                _topTargets.value = targets
            },
            onFailure = { error ->
                this.error.value = error
            }
        )
    }

    fun getDangerLevelForCount(count: Int): String {
        return when (count) {
            0 -> "Thấp"
            in 1..2 -> "Trung bình"
            in 3..5 -> "Cao"
            else -> "Rất cao"
        }
    }

    fun getDangerLevelColorForCount(count: Int): Color {
        return when (count) {
            0 -> Color.Green
            in 1..2 -> Color(0xFFFFA500) // Orange
            in 3..5 -> Color.Red
            else -> Color(0xFF8B0000) // Dark Red
        }
    }

    init {
        loadAllReports()
        loadTopFraudTargets()
    }
}

