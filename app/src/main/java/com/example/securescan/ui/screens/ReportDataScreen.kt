package com.example.securescan.ui.screens

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securescan.data.models.ReportItem
import com.example.securescan.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.securescan.R
import com.example.securescan.ui.theme.*

@Composable
fun ReportDataScreen(
    userId: String,
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val reports by viewModel.userReports
    val isLoading by viewModel.isLoading
    val error by viewModel.message
    var showFilterDialog by remember { mutableStateOf(false) }

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A5298).copy(alpha = 0.05f),
            Color(0xFF5E7CE2).copy(alpha = 0.02f)
        )
    )

    LaunchedEffect(userId) {
        viewModel.loadReportsByUserId(userId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScanHistoryAppBar(
                title = "Dữ Liệu Báo Cáo",
                onBackClick = onNavigateBack,
                onFilterClick = { showFilterDialog = true }
            )


            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // show image news5
                Image(
                    painter = painterResource(id = R.drawable.news5),
                    contentDescription = "News",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Báo cáo ngay thông tin lừa đảo để bảo vệ bản thân và người khác khỏi những mối đe dọa tiềm ẩn.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2A5298)
                )
            }


            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF5E7CE2)
                        )
                    }
                }
                reports.isEmpty() -> {
                    EmptyView()
                }
                else -> {
                    ReportList(reports = reports)
                }
            }
        }

        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onFilterSelected = { type ->
                    Log.d("type" , " Lọc báo cáo: $type")
                    if (type.isNullOrEmpty()) {
                        viewModel.loadAllReports()
                    }else {
                        viewModel.filterReportByType(
                            type = type ?: "",
                            onSuccess = { /* không cần xử lý thêm */ },
                            onFailure = {
                                Log.e("ReportDataScreen", "Error filtering reports: ${it.message}")
                            }
                        )
                    }
                    showFilterDialog = false
                }
            )
        }
    }
}

@Composable
fun ReportList(reports: List<ReportItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { report ->
            ReportCard(report = report)
        }
    }
}

@Composable
fun ReportCard(report: ReportItem) {
    Log.d("ReportCard", "Report: $report")
    val cardGradient = when (report.type) {
        "phone" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF6772E5).copy(0.05f), Color(0xFF6772E5).copy(0.02f))
        )
        "url" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF5E7CE2).copy(0.05f), Color(0xFF5E7CE2).copy(0.02f))
        )
        "card" -> Brush.verticalGradient(
            colors = listOf(Color(0xFFE57373).copy(0.05f), Color(0xFFE57373).copy(0.02f))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFF9E9E9E).copy(0.05f), Color(0xFF9E9E9E).copy(0.02f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (report.check){
                             "Đã xác thực"
                        } else {
                            "Chưa xác thực"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A5298)
                    )

                    TypeBadge(type = report.type)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Đối tượng : ${report.target}",
                    fontSize = 16.sp,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nội dung lừa đảo : ${report.description}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Báo cáo bởi: ${report.reportedBy}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = formatTimestamp(report.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun TypeBadge(type: String) {
    val (backgroundColor, textColor) = when (type) {
        "phone" -> Pair(Color(0xFF6772E5), Color.White)
        "url" -> Pair(Color(0xFF5E7CE2), Color.White)
        "card" -> Pair(Color(0xFF5E7CE2), Color.White)
        else -> Pair(Color(0xFF9E9E9E), Color.White)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (type == "phone") {
                "Số điện thoại"
            } else if (type == "url") {
                "Website"
            } else {
                "Tài khoản"
            },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Không tìm thấy lịch sử báo cáo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2A5298)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Báo cáo sẽ sớm xuất hiện khi bạn báo cáo thông tin lừa đảo",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ErrorView(error: String?, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading reports",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2A5298)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error ?: "Unknown error occurred",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E7CE2)
                )
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilterSelected: (String?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Lọc Báo Cáo",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A5298)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(text = "Tất cả báo cáo", onClick = { onFilterSelected(null) })
                FilterButton(text = "Số điện thoại", onClick = { onFilterSelected("phone") })
                FilterButton(text = "Website", onClick = { onFilterSelected("url") })
                FilterButton(text = "Tài khoản", onClick = { onFilterSelected("card") })
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bỏ qua", color = Color(0xFF5E7CE2))
            }
        }
    )
}

@Composable
fun FilterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5E7CE2).copy(alpha = 0.1f),
            contentColor = Color(0xFF2A5298)
        )
    ) {
        Text(text = text)
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun ScanHistoryAppBar(
    title: String = "Dữ Liệu Báo Cáo",
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2A5298), LightBlue)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon with gradient background
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
                    imageVector = Icons.Default.History,
                    contentDescription = "History Icon",
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

            // Filter button
            IconButton(onClick = onFilterClick) {
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
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter Icon",
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

