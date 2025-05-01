package com.example.securescan.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securescan.R
import com.example.securescan.ui.theme.ErrorRed
import com.example.securescan.viewmodel.ScanViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Định nghĩa model cho lịch sử quét
data class ScanHistoryItem(
    val target: String,
    val result: Boolean, // true = lừa đảo, false = chưa có trong danh sách
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun ScanScreen(viewModel: ScanViewModel) {
    val context = LocalContext.current

    // State variables
    var url by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedScanType by remember { mutableStateOf("URL") }
    var isScanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<Boolean?>(null) } // null = chưa quét, true = lừa đảo, false = an toàn
    var currentScanTarget by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var scanResultText by remember { mutableStateOf("") }

    val showResults = remember { mutableStateOf(true) }

    // Giả lập lịch sử quét
    var scanHistory by remember { mutableStateOf(listOf<ScanHistoryItem>()) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val filePathFromUri = getFilePathFromUri(context, selectedUri)
            filePathFromUri?.let { path ->
                filePath = path
                selectedFileName = path.split("/").last()
            } ?: run {
                Toast.makeText(context, "Không thể đọc file đã chọn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            ScanTopBar()

            Spacer(modifier = Modifier.height(16.dp))

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
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Type Selection
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chọn đối tượng cần quét",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
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
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScanType == "URL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "URL/Domain/IP",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                selectedScanType = "File"
                                scanComplete = false
                                scanResult = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedScanType == "File") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Tệp tin",
                                color = MaterialTheme.colorScheme.onPrimary,
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (selectedScanType == "URL") {
                        Text(
                            text = "Nhập đường dẫn cần kiểm tra",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = url,
                            onValueChange = {
                                url = it
                                scanComplete = false
                                scanResult = null
                            },
                            placeholder = { Text("URL, domain, IP address...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    } else {
                        Text(
                            text = "Chọn tệp tin cần kiểm tra",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
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
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = { filePickerLauncher.launch("*/*") },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Upload,
                                    contentDescription = "Chọn File",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedScanType == "URL" && url.isNotEmpty()) {
                                isScanning = true
                                currentScanTarget = url
                                viewModel.scanUrl(url) { result ->
                                    if(result.contains("Scan completed")) {
                                        scanComplete = true
                                        isScanning = false
                                        scanResultText = result
                                        val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                                        val match = maliciousRegex.find(result)
                                        val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                                        val isMalicious = maliciousCount > 0
                                        scanResult = isMalicious
                                        val newHistoryItem = ScanHistoryItem(currentScanTarget, isMalicious)
                                        scanHistory = listOf(newHistoryItem) + scanHistory
                                    } else {
                                        scanComplete = false
                                        isScanning = true
                                    }
                                }
                            } else if (selectedScanType == "File" && filePath.isNotEmpty()) {
                                isScanning = true
                                currentScanTarget = selectedFileName.ifEmpty { filePath }
                                viewModel.scanFile(filePath) { result ->
                                    if (result.contains("Scan completed")) {
                                        scanComplete = true
                                        isScanning = false
                                        scanResultText = result
                                        val maliciousRegex = Regex("Malicious:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                                        val match = maliciousRegex.find(result)
                                        val maliciousCount = match?.groups?.get(1)?.value?.toIntOrNull() ?: 0
                                        val isMalicious = maliciousCount > 0
                                        scanResult = isMalicious
                                        val newHistoryItem = ScanHistoryItem(currentScanTarget, isMalicious)
                                        scanHistory = listOf(newHistoryItem) + scanHistory
                                    } else {
                                        scanComplete = false
                                        isScanning = true
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    if (selectedScanType == "URL") "Vui lòng nhập URL/Domain/IP" else "Vui lòng chọn tệp tin",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isScanning &&
                                ((selectedScanType == "URL" && url.isNotEmpty()) ||
                                        (selectedScanType == "File" && filePath.isNotEmpty()))
                    ) {
                        Text(
                            text = "Quét ngay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan result card
            if (isScanning || scanComplete) {
                AnimatedVisibility(
                    visible = isScanning || scanComplete,
                    enter = fadeIn(animationSpec = tween(400)) + expandVertically(animationSpec = tween(400)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kết quả quét",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                IconButton(
                                    onClick = { showResults.value = !showResults.value }
                                ) {
                                    Icon(
                                        imageVector = if (showResults.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Toggle Results",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = showResults.value,
                                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(20.dp))

                                    when {
                                        isScanning -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 16.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(100.dp)
                                                            .clip(RoundedCornerShape(50.dp))
                                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(60.dp),
                                                            strokeWidth = 4.dp
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Text(
                                                        text = "Đang kiểm tra ${if (selectedScanType == "URL") "địa chỉ" else "tệp tin"}...",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        textAlign = TextAlign.Center
                                                    )

                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    LinearProgressIndicator(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier
                                                            .width(200.dp)
                                                            .height(6.dp)
                                                            .clip(RoundedCornerShape(3.dp))
                                                    )
                                                }
                                            }
                                        }

                                        scanComplete -> {
                                            val backgroundColor = if (scanResult == true) ErrorRed.copy(alpha = 0.1f) else Green.copy(alpha = 0.1f)
                                            val iconTint = if (scanResult == true) ErrorRed else Green
                                            val resultIcon = if (scanResult == true) Icons.Default.Warning else Icons.Default.Check
                                            val resultText = if (scanResult == true) "LỪA ĐẢO" else "CHƯA CÓ THÔNG TIN"
                                            val resultColor = if (scanResult == true) ErrorRed else Green

                                            Box(
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(RoundedCornerShape(50.dp))
                                                    .background(backgroundColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = resultIcon,
                                                    contentDescription = "Result Icon",
                                                    tint = iconTint,
                                                    modifier = Modifier.size(50.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = resultText,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = resultColor
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = currentScanTarget,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )

                                            Spacer(modifier = Modifier.height(20.dp))

                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (scanResult == true) ErrorRed.copy(alpha = 0.08f) else Green.copy(alpha = 0.08f)
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = if (scanResult == true) Icons.Default.Info else Icons.Default.Info,
                                                        contentDescription = "Information",
                                                        tint = if (scanResult == true) ErrorRed else Green,
                                                        modifier = Modifier.size(24.dp)
                                                    )

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Text(
                                                        text = if (scanResult == true)
                                                            "Đây là ${if (selectedScanType == "URL") "địa chỉ" else "tệp tin"} lừa đảo. Bạn không nên truy cập hoặc sử dụng nó."
                                                        else
                                                            "${if (selectedScanType == "URL") "Địa chỉ" else "Tệp tin"} này chưa có trong danh sách lừa đảo, nhưng hãy luôn cẩn thận.",
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHistory = !showHistory },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lịch sử quét",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Icon(
                            imageVector = if (showHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle History",
                            tint = MaterialTheme.colorScheme.onSurface
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
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .padding(top = 8.dp)
                            ) {
                                items(scanHistory) { item ->
                                    ScanHistoryItemCard(item)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScanTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "QUÉT AN TOÀN",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScanHistoryItemCard(item: ScanHistoryItem) {
    val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.timestamp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.result) ErrorRed.copy(alpha = 0.05f) else Green.copy(alpha = 0.05f)
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
                        .background(if (item.result) ErrorRed.copy(alpha = 0.2f) else Green.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.result) Icons.Default.Warning else Icons.Default.Check,
                        contentDescription = "Result Icon",
                        tint = if (item.result) ErrorRed else Green,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Địa chỉ website/file/url/IP:",
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
                        text = if (item.result)
                            "Website/IP/domain/file lừa đảo"
                        else
                            "Chưa có trong danh sách lừa đảo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.result) ErrorRed else Green
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Khuyến cáo:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (item.result)
                            "Bạn không nên truy cập vào nó"
                        else
                            "Hãy báo cáo ngay khi có nghi ngờ",
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