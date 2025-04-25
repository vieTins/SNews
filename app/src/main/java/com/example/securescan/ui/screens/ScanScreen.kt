//package com.example.securescan.ui.screens
//
//import android.net.Uri
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Upload
//import androidx.compose.runtime.saveable.rememberSaveable
//import com.example.securescan.viewmodel.ScanViewModel
//import java.io.File
//
//@Composable
//fun ScanScreen(viewModel: ScanViewModel) {
//    var url by rememberSaveable { mutableStateOf("") }
//    var filePath by rememberSaveable { mutableStateOf("") }
//    var selectedFileName by rememberSaveable { mutableStateOf("") }
//    var scanResult by remember { mutableStateOf("") }
//    var selectedScanType by rememberSaveable { mutableStateOf("URL") }
//    var isScanning by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    // File picker launcher
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { selectedUri ->
//            // Lấy file path từ uri
//            val filePathFromUri = getFilePathFromUri(context, selectedUri)
//            filePathFromUri?.let { path ->
//                filePath = path
//                // Lấy tên file để hiển thị
//                selectedFileName = path.split("/").last()
//            } ?: run {
//                Toast.makeText(context, "Không thể đọc file đã chọn", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("VirusTotal Scanner", style = MaterialTheme.typography.titleLarge)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Chọn loại quét (File hoặc URL)
//        Row {
//            Button(
//                onClick = { selectedScanType = "File" },
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (selectedScanType == "File")
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.secondary
//                )
//            ) {
//                Text("Scan File")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Button(
//                onClick = { selectedScanType = "URL" },
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (selectedScanType == "URL")
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.secondary
//                )
//            ) {
//                Text("Scan URL")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (selectedScanType == "File") {
//            // File input section
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // Sử dụng OutlinedTextField đơn giản mà không có thuộc tính overflow
//                OutlinedTextField(
//                    value = if (selectedFileName.isNotEmpty()) selectedFileName else filePath,
//                    onValueChange = { newValue ->
//                        filePath = newValue
//                        selectedFileName = ""
//                    },
//                    label = { Text("File Path") },
//                    modifier = Modifier.weight(1f),
//                    maxLines = 1,
//                    singleLine = true
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Nếu biểu tượng Upload không tồn tại, bạn có thể thay thế bằng Button
//                IconButton(
//                    onClick = { filePickerLauncher.launch("*/*") }
//                ) {
//                    Icon(Icons.Filled.Upload, contentDescription = "Upload File")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = {
//                    if (filePath.isNotEmpty()) {
//                        isScanning = true
//                        scanResult = "Preparing to scan file..."
//                        viewModel.scanFile(filePath) { result ->
//                            scanResult = result
//                            isScanning = false
//                        }
//                    } else {
//                        Toast.makeText(context, "Please select or enter file path", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                enabled = !isScanning && filePath.isNotEmpty()
//            ) {
//                Text("Start File Scan")
//            }
//        } else {
//            // URL input section
//            OutlinedTextField(
//                value = url,
//                onValueChange = { url = it },
//                label = { Text("Enter URL, domain, IP address, or shortened URL") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = {
//                    if (url.isNotEmpty()) {
//                        isScanning = true
//                        scanResult = "Preparing to scan URL..."
//                        viewModel.scanUrl(url) { result ->
//                            scanResult = result
//                            isScanning = false
//                        }
//                    } else {
//                        Toast.makeText(context, "Please enter a URL", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                enabled = !isScanning && url.isNotEmpty()
//            ) {
//                Text("Scan URL")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Hiển thị trạng thái quét
//        if (isScanning) {
//            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Scanning in progress... This may take a few moments.")
//        }
//
//        // Kết quả quét
//        if (scanResult.isNotEmpty()) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text("Scan Results:", style = MaterialTheme.typography.titleMedium)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(scanResult)
//                }
//            }
//        }
//    }
//}
//
//// Hàm giúp lấy đường dẫn thực từ Uri
//private fun getFilePathFromUri(context: android.content.Context, uri: Uri): String? {
//    // Đối với các file được chọn từ storage
//    if (uri.scheme == "content") {
//        try {
//            // Tạo file tạm thời
//            val fileName = getFileNameFromUri(context, uri) ?: "temp_file"
//            val file = File(context.cacheDir, fileName)
//            val inputStream = context.contentResolver.openInputStream(uri)
//            inputStream?.use { input ->
//                file.outputStream().use { output ->
//                    input.copyTo(output)
//                }
//            }
//            return file.absolutePath
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        }
//    }
//    // Nếu là file path trực tiếp
//    if (uri.scheme == "file") {
//        return uri.path
//    }
//    return null
//}
//
//// Lấy tên file từ Uri
//private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
//    var fileName: String? = null
//    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//        if (cursor.moveToFirst()) {
//            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
//            if (nameIndex != -1) {
//                fileName = cursor.getString(nameIndex)
//            }
//        }
//    }
//    return fileName
//}