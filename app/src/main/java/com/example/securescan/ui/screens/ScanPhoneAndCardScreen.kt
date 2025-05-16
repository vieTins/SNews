package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.securescan.R
import com.example.securescan.data.models.ScanState
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.ErrorRed
import com.example.securescan.ui.theme.LightBlue
import com.example.securescan.ui.theme.White
import com.example.securescan.utils.ValidationUtils
import com.example.securescan.viewmodel.ScanPhoneCardViewModel
import kotlinx.coroutines.launch

//// Define colors
val DarkBlue = Color(0xFF2A5298)
val Green = Color(0xFF81C784)

@Composable
fun ScanPhoneAndCardScreen(
    viewModel: ScanPhoneCardViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onViewHistory: () -> Unit
) {
    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A5298).copy(alpha = 0.05f),
            Color(0xFF5E7CE2).copy(alpha = 0.02f)
        )
    )

    var selectedTab by remember { mutableStateOf(ScanType.PHONE) }
    val coroutineScope = rememberCoroutineScope()
    val scanState by viewModel.scanState
    val scrollState = rememberScrollState()

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
                title = "Kiểm Tra An Toàn",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = onNavigateBack,
                actionIcon = Icons.Default.History,
                onActionIconClick = onViewHistory
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.news3),
                    contentDescription = "News",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quét ngay để bảo vệ bạn và gia đình khỏi các mối đe dọa lừa đảo.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = DeepBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Type Selection Card
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
                            onClick = { selectedTab = ScanType.PHONE },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == ScanType.PHONE) DeepBlue else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Số Điện Thoại",
                                color = if (selectedTab == ScanType.PHONE) White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { selectedTab = ScanType.CARD },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == ScanType.CARD) DeepBlue else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Số Tài Khoản",
                                color = if (selectedTab == ScanType.CARD) White else MaterialTheme.colorScheme.onSurfaceVariant,
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
                    when (selectedTab) {
                        ScanType.PHONE -> PhoneNumberScanForm(
                            scanState = scanState,
                            onSubmit = { phoneNumber ->
                                coroutineScope.launch {
                                    viewModel.checkPhoneIsPhishing(phoneNumber,
                                        onSuccess = { isPhishing ->
                                            if (isPhishing) {
                                                viewModel.scanState.value = ScanState.Result(
                                                    isMalicious = true,
                                                    message = "Số điện thoại này có dấu hiệu lừa đảo!",
                                                    details = "Cảnh báo: Số điện thoại này có thể là lừa đảo ! Cẩn thận khi giao dịch hoặc mua bán"
                                                )
                                            } else {
                                                viewModel.scanState.value = ScanState.Result(
                                                    isMalicious = false,
                                                    message = "Số điện thoại không có dấu hiệu lừa đảo.",
                                                    details = "Vẫn cẩn thận khi giao dịch và mua bán."
                                                )
                                            }
                                        },
                                        onFailure = { exception ->
                                            viewModel.scanState.value = ScanState.Result(
                                                isMalicious = false,
                                                message = "Quá trình quét thất bại.",
                                                details = exception.message ?: "Lỗi không xác định"
                                            )
                                        }
                                    )
                                }
                            }
                        )
                        ScanType.CARD -> CardNumberScanForm(
                            scanState = scanState,
                            onSubmit = { cardNumber ->
                                coroutineScope.launch {
                                    // Keep existing card scanning logic
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PhoneNumberScanForm(
    scanState: ScanState,
    onSubmit: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(true) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kiểm Tra Số Điện Thoại",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nhập số điện thoại để kiểm tra xem nó có an toàn không",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                isPhoneValid = ValidationUtils.isValidPhoneNumber(it)
                phoneError = if (!isPhoneValid && it.isNotEmpty()) {
                    "Số điện thoại không hợp lệ"
                } else null
            },
            label = { Text("Số điện thoại") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Icon",
                    tint = LightBlue
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isPhoneValid) Color.Gray else ErrorRed,
                focusedBorderColor = if (isPhoneValid) LightBlue else ErrorRed
            ),
            isError = !isPhoneValid,
            supportingText = {
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (phoneNumber.isNotEmpty() && isPhoneValid) {
                    onSubmit(phoneNumber)
                } else {
                    isPhoneValid = false
                    phoneError = "Vui lòng nhập số điện thoại hợp lệ"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepBlue
            ),
            enabled = scanState !is ScanState.Scanning && phoneNumber.isNotEmpty() && isPhoneValid
        ) {
            if (scanState is ScanState.Scanning) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Quét ngay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = scanState is ScanState.Result,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (scanState is ScanState.Result) {
                ScanResultCard(result = scanState)
            }
        }
    }
}

@Composable
fun CardNumberScanForm(
    scanState: ScanState,
    onSubmit: (String) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var isCardValid by remember { mutableStateOf(true) }
    var cardError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kiểm Tra Tài Khoản",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nhập số thẻ để kiểm tra xem nó có an toàn không",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = cardNumber,
            onValueChange = {
                cardNumber = it
                isCardValid = ValidationUtils.isValidCardNumber(it)
                cardError = if (!isCardValid && it.isNotEmpty()) {
                    "Số thẻ không hợp lệ"
                } else null
            },
            label = { Text("Số tài khoản") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "Card Icon",
                    tint = LightBlue
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isCardValid) Color.Gray else ErrorRed,
                focusedBorderColor = if (isCardValid) LightBlue else ErrorRed
            ),
            isError = !isCardValid,
            supportingText = {
                if (cardError != null) {
                    Text(
                        text = cardError!!,
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (cardNumber.isNotEmpty() && isCardValid) {
                    onSubmit(cardNumber)
                } else {
                    isCardValid = false
                    cardError = "Vui lòng nhập số thẻ hợp lệ"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepBlue
            ),
            enabled = scanState !is ScanState.Scanning && cardNumber.isNotEmpty() && isCardValid
        ) {
            if (scanState is ScanState.Scanning) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Quét ngay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = scanState is ScanState.Result,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (scanState is ScanState.Result) {
                ScanResultCard(result = scanState)
            }
        }
    }
}

@Composable
fun ScanResultCard(result: ScanState.Result) {
    val (backgroundColor, iconTint, borderColor) = if (result.isMalicious) {
        Triple(
            Brush.verticalGradient(
                colors = listOf(
                    ErrorRed.copy(alpha = 0.1f),
                    ErrorRed.copy(alpha = 0.05f)
                )
            ),
            ErrorRed,
            ErrorRed.copy(alpha = 0.3f)
        )
    } else {
        Triple(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF81C784).copy(alpha = 0.1f),
                    Color(0xFF81C784).copy(alpha = 0.05f)
                )
            ),
            Color(0xFF81C784),
            Color(0xFF81C784).copy(alpha = 0.3f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = backgroundColor)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (result.isMalicious) {
                                ErrorRed.copy(alpha = 0.1f)
                            } else {
                                Color(0xFF81C784).copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (result.isMalicious) {
                            Icons.Default.Warning
                        } else {
                            Icons.Default.CheckCircle
                        },
                        contentDescription = if (result.isMalicious) "Warning" else "Safe",
                        tint = iconTint,
                        modifier = Modifier.size(48.dp * scale)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = result.message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isMalicious) ErrorRed else Color(0xFF81C784),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = result.details,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

enum class ScanType {
    PHONE, CARD
}