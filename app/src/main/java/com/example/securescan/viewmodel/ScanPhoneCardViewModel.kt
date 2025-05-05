package com.example.securescan.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.securescan.data.models.ScanState
import com.example.securescan.data.repository.ScanPhoneCardRepository

class ScanPhoneCardViewModel : ViewModel() {
    private val scanPhoneRepository = ScanPhoneCardRepository()

    // Khởi tạo scanState với trạng thái Idle ban đầu
    val scanState = mutableStateOf<ScanState>(ScanState.Idle)

    fun getScanPhoneCards() {
        scanState.value = ScanState.Scanning // Đang quét
        scanPhoneRepository.getScanPhone(
            onSuccess = { scanPhoneCards ->
                // Cập nhật với kết quả quét
                scanState.value = ScanState.Result(
                    isMalicious = false, // Giả sử là không độc hại
                    message = "Quá trình quét thành công.",
                    details = "Không tìm thấy phần mềm độc hại."
                )
            },
            onFailure = {
                scanState.value = ScanState.Result(
                    isMalicious = false,
                    message = "Quá trình quét thất bại.",
                    details = it.message ?: "Lỗi không xác định"
                )
            }
        )
    }

    fun checkPhoneIsPhishing(
        phone: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        scanState.value = ScanState.Scanning // Đang quét
        scanPhoneRepository.checkPhoneIsPhishing(phone, onSuccess, onFailure)
    }
}

