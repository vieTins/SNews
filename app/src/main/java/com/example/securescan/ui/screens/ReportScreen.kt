package com.example.securescan.ui.screens

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.securescan.R
import com.example.securescan.data.models.ReportItem
import com.example.securescan.viewmodel.ReportsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReportScren(viewModel : ReportsViewModel) {
        // State for form fields
        var reportType by remember { mutableStateOf("url") } // Default to URL reporting
        var inputValue by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        var submitSuccess by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var target by remember { mutableStateOf("") }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: "anonymous"

        // Image picker launcher
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(DeepBlue, PrimaryBlue)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Settings icon
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(LightBlue.copy(alpha = 0.8f), LightBlue.copy(alpha = 0.1f)),
                                        radius = 20f
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = "Report Icon",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Title
                        Text(
                            text = "Báo cáo",
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Report Icon",
                            tint = Color(0xFFE53935), // Red color for warning
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Báo cáo lừa đảo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF1E3A8A) // Deep blue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Giúp chúng tôi bảo vệ cộng đồng bằng cách báo cáo các trường hợp lừa đảo",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Report Type Selection
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Chọn loại báo cáo",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E3A8A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ReportTypeButton(
                                icon = Icons.Default.Language,
                                label = "Website",
                                selected = reportType == "url",
                                onClick = { reportType = "url" }
                            )

                            ReportTypeButton(
                                icon = Icons.Default.Phone,
                                label = "Số điện thoại",
                                selected = reportType == "phone",
                                onClick = { reportType = "phone" }
                            )

                            ReportTypeButton(
                                icon = Icons.Default.CreditCard,
                                label = "Thẻ ngân hàng",
                                selected = reportType == "card",
                                onClick = { reportType = "card" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input Form
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        val inputLabel = when (reportType) {
                            "url" -> "Địa chỉ website"
                            "phone" -> "Số điện thoại"
                            else -> "Số thẻ ngân hàng"
                        }

                        val inputPlaceholder = when (reportType) {
                            "url" -> "Nhập địa chỉ website"
                            "phone" -> "Nhập số điện thoại"
                            else -> "Nhập số thẻ ngân hàng"
                        }

                        Text(
                            text = inputLabel,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF1E3A8A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = target,
                            onValueChange = { target = it },
                            placeholder = { Text(inputPlaceholder) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = when (reportType) {
                                "phone", "card" -> KeyboardOptions(keyboardType = KeyboardType.Number)
                                else -> KeyboardOptions(keyboardType = KeyboardType.Uri)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (reportType) {
                                        "url" -> Icons.Default.Language
                                        "phone" -> Icons.Default.Phone
                                        else -> Icons.Default.CreditCard
                                    },
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6)
                                )
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Mô tả chi tiết",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF1E3A8A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Mô tả về trường hợp lừa đảo bạn gặp phải...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Image Upload Section
                        Text(
                            text = "Ảnh minh họa (nếu có)",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF1E3A8A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray.copy(alpha = 0.2f))
                                .clickable { imagePicker.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Add photo",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Chọn ảnh",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (errorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (validateInput(target, reportType)) {
                                    val report = ReportItem(
                                        type = reportType,
                                        target = target,
                                        description = description,
                                        reportedBy = email,
                                        imageUrl =  selectedImageUri.toString(),
                                        check = false
                                    )
                                    isSubmitting = true
                                    errorMessage = ""
                                    viewModel.submitReport(
                                        report,
                                        onSuccess = {
                                            isSubmitting = false
                                            submitSuccess = true
                                            inputValue = ""
                                            description = ""
                                        },
                                        onFailure = { exception ->
                                            isSubmitting = false
                                            errorMessage = "Lỗi: ${exception.message}"
                                        }
                                    )
                                } else {
                                    errorMessage = when (reportType) {
                                        "url" -> "Vui lòng nhập đúng định dạng địa chỉ website"
                                        "phone" -> "Vui lòng nhập đúng định dạng số điện thoại"
                                        else -> "Vui lòng nhập đúng định dạng số thẻ ngân hàng"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            ),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )

                            } else {
                                Text(
                                    text = "Gửi báo cáo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Success Dialog
            if (submitSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            // Dismiss on background click
                            submitSuccess = false
                            inputValue = ""
                            description = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp)
                            .clickable(enabled = false) { }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Báo cáo thành công!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF1E3A8A)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Cảm ơn bạn đã giúp chúng tôi làm cho cộng đồng trở nên an toàn hơn. Chúng tôi sẽ xem xét báo cáo của bạn sớm nhất có thể.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    submitSuccess = false
                                    target = ""
                                    description = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E3A8A)
                                )
                            ) {
                                Text(
                                    text = "OK",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
}

@Composable
fun ReportTypeButton(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (selected) Color(0xFF3B82F6).copy(alpha = 0.1f) else Color.Transparent)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (selected) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFFE5E7EB))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFF3B82F6) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF3B82F6) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

fun validateInput(input: String, type: String): Boolean {
    return when (type) {
        "url" -> {
            val urlPattern = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
            input.matches(urlPattern.toRegex())
        }
        "phone" -> {
            // Simple Vietnamese phone number validation
            val phonePattern = "^(0|\\+84)(3|5|7|8|9)\\d{8}$"
            input.matches(phonePattern.toRegex())
        }
        "card" -> {
            // Simple credit card validation (checks if it's a valid format, not if it's a real card)
            // example "1234 5678 9012 3456" is valid
            val cardPattern = "^\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}$"
            input.matches(cardPattern.toRegex())
        }
        else -> false
    }
}