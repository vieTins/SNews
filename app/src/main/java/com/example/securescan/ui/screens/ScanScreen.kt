package com.example.securescan.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.securescan.R
import com.example.securescan.data.models.ScanHistory
import com.example.securescan.data.models.ScanResult
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.ErrorRed
import com.example.securescan.ui.theme.White
import com.example.securescan.utils.ValidationUtils
import com.example.securescan.viewmodel.ScanViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScanScreen(viewModel: ScanViewModel, navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A5298).copy(alpha = 0.05f),
            Color(0xFF5E7CE2).copy(alpha = 0.02f)
        )
    )

    // State variables
    var url by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedScanType by remember { mutableStateOf("URL") }
    var isScanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<Boolean?>(null) }
    var currentScanTarget by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var scanResultText by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val filePathFromUri = getFilePathFromUri(context, selectedUri)
            filePathFromUri?.let { path ->
                filePath = path
                selectedFileName = path.split("/").last()
                inputError = null
            } ?: run {
                Toast.makeText(context, "Không thể đọc file đã chọn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load scan history when screen is first displayed
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            viewModel.loadScanHistory(userId)
        }
    }

    // Collect scan history from ViewModel
    val scanHistory by viewModel.scanHistory.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom App Bar
            AppTopBar(
                title = "Quét an toàn",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.navigateUp() },
                actionIcon = Icons.Default.History,
                onActionIconClick = { showHistory = !showHistory }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.news4),
                    contentDescription = "News",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quét ngay địa chỉ website, tệp tin hoặc IP để bảo vệ bạn khỏi các mối đe dọa lừa đảo.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = DeepBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Type Selection
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chọn đối tượng cần quét",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = DeepBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                selectedScanType = "URL"
                                scanComplete = false
                                scanResult = null
                                url = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScanType == "URL") DeepBlue else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "URL/Domain/IP",
                                color = if (selectedScanType == "URL") White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                selectedScanType = "File"
                                scanComplete = false
                                scanResult = null
                                filePath = ""
                                selectedFileName = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScanType == "File") DeepBlue else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Tệp tin",
                                color = if (selectedScanType == "File") White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (selectedScanType == "URL") {
                        Text(
                            text = "Nhập đường dẫn cần kiểm tra",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = DeepBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = url,
                            onValueChange = {
                                url = it
                                scanComplete = false
                                scanResult = null
                                inputError = if (!ValidationUtils.isValidUrl(it) && !ValidationUtils.isValidIpAddress(it) && it.isNotEmpty()) {
                                    "URL/Domain/IP không hợp lệ"
                                } else null
                            },
                            placeholder = { Text("URL, domain, IP address...") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                if (url.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        url = ""
                                        scanComplete = false
                                        scanResult = null
                                        inputError = null
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = DeepBlue
                                        )
                                    }
                                }
                            },
                            isError = inputError != null,
                            supportingText = {
                                if (inputError != null) {
                                    Text(
                                        text = inputError!!,
                                        color = ErrorRed
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                cursorColor = DeepBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    } else {
                        Text(
                            text = "Chọn tệp tin cần kiểm tra",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = DeepBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedFileName.ifEmpty { filePath },
                                onValueChange = { newValue ->
                                    filePath = newValue
                                    selectedFileName = ""
                                    scanComplete = false
                                    scanResult = null
                                },
                                placeholder = { Text("Đường dẫn đến file...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                singleLine = true,
                                trailingIcon = {
                                    if (filePath.isNotEmpty() || selectedFileName.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                filePath = ""
                                                selectedFileName = ""
                                                scanComplete = false
                                                scanResult = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = DeepBlue
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    cursorColor = DeepBlue,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = { filePickerLauncher.launch("*/*") },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = DeepBlue
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Upload,
                                    contentDescription = "Chọn File",
                                    tint = White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scan button with result overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (scanComplete) {
                            // Result overlay
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (scanResult) {
                                        true -> ErrorRed.copy(alpha = 0.1f)
                                        false -> Color(0xFF81C784).copy(alpha = 0.1f)
                                        null -> Color(0xFFFFA726).copy(alpha = 0.1f)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (scanResult) {
                                                true -> Icons.Default.Warning
                                                false -> Icons.Default.Check
                                                null -> Icons.Default.Warning
                                            },
                                            contentDescription = "Result Icon",
                                            tint = when (scanResult) {
                                                true -> ErrorRed
                                                false -> Color(0xFF81C784)
                                                null -> Color(0xFFFFA726)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = when (scanResult) {
                                                true -> "LỪA ĐẢO - KHÔNG NÊN TRUY CẬP"
                                                false -> "CHƯA CÓ THÔNG TIN"
                                                null -> "NGHI NGỜ - CẦN THẬN TRỌNG"
                                            },
                                            color = when (scanResult) {
                                                true -> ErrorRed
                                                false -> Color(0xFF81C784)
                                                null -> Color(0xFFFFA726)
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            scanComplete = false
                                            scanResult = null
                                            if (selectedScanType == "URL") {
                                                url = ""
                                            } else {
                                                filePath = ""
                                                selectedFileName = ""
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Result",
                                            tint = DeepBlue
                                        )
                                    }
                                }
                            }
                        } else {
                            // Normal scan button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    
                                    if (currentUser == null) {
                                        Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    
                                    if (selectedScanType == "URL") {
                                        if (url.isEmpty()) {
                                            inputError = "Vui lòng nhập URL/Domain/IP"
                                            return@Button
                                        }
                                        
                                        if (!ValidationUtils.isValidUrl(url) && !ValidationUtils.isValidIpAddress(url)) {
                                            inputError = "URL/Domain/IP không hợp lệ"
                                            return@Button
                                        }
                                        
                                        isScanning = true
                                        currentScanTarget = url
                                        viewModel.scanUrl(url, currentUser.uid) { result ->
                                            if(result.contains("Scan completed")) {
                                                scanComplete = true
                                                isScanning = false
                                                scanResultText = result
                                                val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                                                val match = maliciousRegex.find(result)
                                                val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                                                val isMalicious = maliciousCount > 0
                                                scanResult = if (maliciousCount > 0) true else if (maliciousCount == 0) false else null
                                                
                                                // Refresh scan history after successful scan
                                                viewModel.loadScanHistory(currentUser.uid)
                                                showHistory = true // Automatically show history after scan
                                            } else {
                                                scanComplete = false
                                                isScanning = true
                                            }
                                        }
                                    } else if (selectedScanType == "File") {
                                        if (filePath.isEmpty()) {
                                            inputError = "Vui lòng chọn tệp tin"
                                            return@Button
                                        }

                                        val file = File(filePath)
                                        if (!file.exists()) {
                                            inputError = "File không tồn tại"
                                            return@Button
                                        }

                                        if (file.length() > 32 * 1024 * 1024) { // 32MB limit
                                            inputError = "File quá lớn (giới hạn 32MB)"
                                            return@Button
                                        }

                                        if (!ValidationUtils.isValidFileName(file.name)) {
                                            inputError = "Tên file không hợp lệ"
                                            return@Button
                                        }
                                        
                                        isScanning = true
                                        currentScanTarget = selectedFileName.ifEmpty { filePath }
                                        viewModel.scanFile(filePath, currentUser.uid) { result ->
                                            if (result.contains("Scan completed")) {
                                                scanComplete = true
                                                isScanning = false
                                                scanResultText = result
                                                val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                                                val match = maliciousRegex.find(result)
                                                val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                                                val isMalicious = maliciousCount > 0
                                                scanResult = if (maliciousCount > 0) true else if (maliciousCount == 0) false else null
                                                
                                                // Refresh scan history after successful scan
                                                viewModel.loadScanHistory(currentUser.uid)
                                                showHistory = true // Automatically show history after scan
                                            } else {
                                                scanComplete = false
                                                isScanning = true
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepBlue
                                ),
                                enabled = !isScanning &&
                                        ((selectedScanType == "URL" && url.isNotEmpty()) ||
                                                (selectedScanType == "File" && filePath.isNotEmpty()))
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(
                                        color = White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Quét ngay",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lịch sử quét
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                showHistory = !showHistory
                                if (showHistory) {
                                    currentUser?.uid?.let { userId ->
                                        viewModel.loadScanHistory(userId)
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lịch sử quét",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = DeepBlue
                        )

                        Icon(
                            imageVector = if (showHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle History",
                            tint = DeepBlue
                        )
                    }

                    AnimatedVisibility(
                        visible = showHistory,
                        enter = expandVertically(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300))
                    ) {
                        if (scanHistory.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Chưa có lịch sử quét nào",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 500.dp)
                                    .verticalScroll(rememberScrollState())
                                    .padding(top = 8.dp)
                            ) {
                                scanHistory.forEach { item ->
                                    ScanHistoryItemCard(item)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ScanHistoryItemCard(item: ScanHistory) {
    val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.timestamp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (item.result) {
                ScanResult.FRAUD.name -> ErrorRed.copy(alpha = 0.05f)
                ScanResult.DANGEROUS.name -> ErrorRed.copy(alpha = 0.05f)
                ScanResult.SUSPICIOUS.name -> Color(0xFFFFA726).copy(alpha = 0.05f)
                else -> Color(0xFF81C784).copy(alpha = 0.05f)
            }
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (item.result) {
                                ScanResult.FRAUD.name -> ErrorRed.copy(alpha = 0.2f)
                                ScanResult.DANGEROUS.name -> ErrorRed.copy(alpha = 0.2f)
                                ScanResult.SUSPICIOUS.name -> Color(0xFFFFA726).copy(alpha = 0.2f)
                                else -> Color(0xFF81C784).copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.result) {
                            ScanResult.FRAUD.name, ScanResult.DANGEROUS.name -> Icons.Default.Warning
                            ScanResult.SUSPICIOUS.name -> Icons.Default.Warning
                            else -> Icons.Default.Check
                        },
                        contentDescription = "Result Icon",
                        tint = when (item.result) {
                            ScanResult.FRAUD.name, ScanResult.DANGEROUS.name -> ErrorRed
                            ScanResult.SUSPICIOUS.name -> Color(0xFFFFA726)
                            else -> Color(0xFF81C784)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Loại quét: ${item.type}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = item.target,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kết quả:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = when (item.result) {
                            ScanResult.FRAUD.name -> "Lừa đảo"
                            ScanResult.DANGEROUS.name -> "Nguy hiểm"
                            ScanResult.SUSPICIOUS.name -> "Nghi ngờ"
                            else -> "Không có thông tin"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (item.result) {
                            ScanResult.FRAUD.name, ScanResult.DANGEROUS.name -> ErrorRed
                            ScanResult.SUSPICIOUS.name -> Color(0xFFFFA726)
                            else -> Color(0xFF81C784)
                        }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Khuyến cáo:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = when (item.result) {
                            ScanResult.FRAUD.name, ScanResult.DANGEROUS.name -> "Không nên truy cập"
                            ScanResult.SUSPICIOUS.name -> "Cần thận trọng"
                            else -> "Có thể truy cập"
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Text(
                text = "Thời gian: $formattedDate",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Hàm giúp lấy đường dẫn thực từ Uri
private fun getFilePathFromUri(context: android.content.Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        try {
            val fileName = getFileNameFromUri(context, uri) ?: "temp_file"
            val file = File(context.cacheDir, fileName)
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    if (uri.scheme == "file") {
        return uri.path
    }
    return null
}

private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
    }
    return fileName
}
