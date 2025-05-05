package com.example.securescan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
    navController: NavController,
    viewModel: ReportsViewModel
) {
    val scrollState = rememberScrollState()
    val report by viewModel.currentReport
    val isLoading by viewModel.isLoading
    var showZoomedImage by remember { mutableStateOf(false) }
    val reportCount by viewModel.reportCount
    
    LaunchedEffect(reportId) {
        viewModel.getReportById(reportId)
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Chi tiết báo cáo",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                report != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Content Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Status Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusChip(
                                        text = if (report?.check == true) "Đã xác thực" else "Chưa xác thực",
                                        color = if (report?.check == true) baseBlue3 else Color.Red
                                    )
                                    
                                    SeverityChip(
                                        text = viewModel.getDangerLevel(),
                                        color = viewModel.getDangerLevelColor()
                                    )
                                }

                                // Report Count
                                Text(
                                    text = "Số lượng báo cáo: $reportCount",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Description
                                Text(
                                    text = "Mô tả",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = report?.description ?: "",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))

                                // Details Section
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        InfoRow(
                                            icon = Icons.Default.Link,
                                            label = "Địa chỉ",
                                            value = report?.target ?: ""
                                        )
                                        
                                        Divider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                        )
                                        
                                        InfoRow(
                                            icon = Icons.Default.Warning,
                                            label = "Loại báo cáo",
                                            value = when (report?.type) {
                                                "phone" -> "Số điện thoại"
                                                "url" -> "Website"
                                                else -> "Tài khoản"
                                            }
                                        )
                                        
                                        Divider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                        )
                                        
                                        InfoRow(
                                            icon = Icons.Default.Person,
                                            label = "Báo cáo bởi",
                                            value = report?.reportedBy ?: ""
                                        )
                                        
                                        Divider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                        )
                                        
                                        InfoRow(
                                            icon = Icons.Default.DateRange,
                                            label = "Thời gian",
                                            value = formatTimestamp(report?.timestamp ?: 0)
                                        )
                                    }
                                }
                            }
                        }

                        // Image Section at bottom
                        if (!report?.imageUrl.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Hình ảnh báo cáo",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    AsyncImage(
                                        model = report?.imageUrl,
                                        contentDescription = "Hình ảnh báo cáo",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { showZoomedImage = true },
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Không tìm thấy thông tin báo cáo",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Zoomed Image Dialog
    if (showZoomedImage && !report?.imageUrl.isNullOrEmpty()) {
        Dialog(
            onDismissRequest = { showZoomedImage = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = report?.imageUrl,
                        contentDescription = "Hình ảnh phóng to",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    
                    // Close button
                    IconButton(
                        onClick = { showZoomedImage = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SeverityChip(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = "Mức độ: $text",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}