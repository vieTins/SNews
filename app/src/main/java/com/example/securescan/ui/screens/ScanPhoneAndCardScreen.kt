package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.securescan.R
import com.example.securescan.data.models.ScanState
import com.example.securescan.viewmodel.ScanPhoneCardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//// Define colors
val DarkBlue = Color(0xFF2A5298)
val Green = Color(0xFF81C784)

@Composable
fun ScanPhoneAndCardScreen(
    viewModel: ScanPhoneCardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom App Bar
            ScannerAppBar(
                title = "Kiểm Tra An Toàn",
                onBackClick = onNavigateBack,
                onHistoryClick = onViewHistory
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // show image news5
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
                    color = Color(0xFF2A5298)
                )
            }

            // Tab Selection
            ScanTypeTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Input Form
            when (selectedTab) {
                ScanType.PHONE -> PhoneNumberScanForm(
                    scanState = scanState,
                    onSubmit = { phoneNumber ->
                        coroutineScope.launch {
                            viewModel.checkPhoneIsPhishing(phoneNumber ,
                                onSuccess = { isPhishing ->
                                    // Cập nhật kết quả quét vào scanState
                                    if (isPhishing) {
                                        viewModel.scanState.value = ScanState.Result(
                                            isMalicious = true,
                                            message = "Số điện thoại này có dấu hiệu lừa đảo!",
                                            details = "Cảnh báo: Số điện thoại này có thể là lừa đảo ! Cẩn thận khi giao dịch hoặc mua bán"
                                        )
                                    } else {
                                        viewModel.scanState.value = ScanState.Result(
                                            isMalicious = false,
                                            message = "Số điện thoại không bị phát hiện là phần mềm độc hại.",
                                            details = "Không có dấu hiệu lừa đảo."
                                        )
                                    }
                                },
                                onFailure = { exception ->
                                    // Cập nhật scanState khi có lỗi
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
//                            viewModel.scanCardNumber(cardNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScannerAppBar(
    title: String,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf( DeepBlue, PrimaryBlue)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White
                )
            }

            // App icon with gradient background
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security Icon",
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title
            Text(
                text = title,
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // History button
            IconButton(onClick = onHistoryClick) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Icon",
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

enum class ScanType {
    PHONE, CARD
}

@Composable
fun ScanTypeTabs(
    selectedTab: ScanType,
    onTabSelected: (ScanType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ScanTypeTab(
            title = "Số Điện Thoại",
            icon = Icons.Default.Phone,
            isSelected = selectedTab == ScanType.PHONE,
            onClick = { onTabSelected(ScanType.PHONE) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        ScanTypeTab(
            title = "Số Tài Khoản",
            icon = Icons.Default.CreditCard,
            isSelected = selectedTab == ScanType.CARD,
            onClick = { onTabSelected(ScanType.CARD) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ScanTypeTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(
                LightBlue.copy(alpha = 0.2f),
                LightBlue.copy(alpha = 0.1f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White,
                Color.White
            )
        )
    }

    val textColor = if (isSelected) Color(0xFF2A5298) else Color.Gray

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush = backgroundColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kiểm Tra Số Điện Thoại",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2A5298)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nhập số điện thoại để kiểm tra xem nó có an toàn không",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Phone number input field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                isPhoneValid = it.isEmpty() || it.length >= 10
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
                unfocusedBorderColor = if (isPhoneValid) Color.Gray else Red,
                focusedBorderColor = if (isPhoneValid) LightBlue else Red
            ),
            isError = !isPhoneValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        if (!isPhoneValid) {
            Text(
                text = "Làm ơn nhập một số điện thoại hợp lệ",
                color = Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scan button
        Button(
            onClick = {
                if (phoneNumber.isNotEmpty() && isPhoneValid) {
                    onSubmit(phoneNumber)
                } else {
                    isPhoneValid = false
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

        // Show scan result
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kiểm Tra Tài Khoản",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2A5298)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nhập số thẻ để kiểm tra xem nó có an toàn không",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card number input field
        OutlinedTextField(
            value = cardNumber,
            onValueChange = {
                cardNumber = it
                isCardValid = it.isEmpty() || it.length >= 16
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
                unfocusedBorderColor = if (isCardValid) Color.Gray else Red,
                focusedBorderColor = if (isCardValid) LightBlue else Red
            ),
            isError = !isCardValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        if (!isCardValid) {
            Text(
                text = "Làm ơn nhập một số tài khoản hợp lệ",
                color = Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scan button
        Button(
            onClick = {
                if (cardNumber.isNotEmpty() && isCardValid) {
                    onSubmit(cardNumber)
                } else {
                    isCardValid = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBlue
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

        // Show scan result
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
                    Red.copy(alpha = 0.1f),
                    Red.copy(alpha = 0.05f)
                )
            ),
            Red,
            Red.copy(alpha = 0.3f)
        )
    } else {
        Triple(
            Brush.verticalGradient(
                colors = listOf(
                    Green.copy(alpha = 0.1f),
                    Green.copy(alpha = 0.05f)
                )
            ),
            Green,
            Green.copy(alpha = 0.3f)
        )
    }

    // Pulse animation for the icon
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
                                Red.copy(alpha = 0.1f)
                            } else {
                                Green.copy(alpha = 0.1f)
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
                    color = if (result.isMalicious) Red else Green,
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