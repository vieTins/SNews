package com.example.securescan.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

import com.example.securescan.R
import com.example.securescan.data.models.ReportItem
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController


@Composable
fun ReportDataScreen(
    userId: String,
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit,
    navController: NavController
) {
    val reports by viewModel.userReports
    val topTargets by viewModel.topTargets
    val isLoading by viewModel.isLoading
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "Dữ Liệu Báo Cáo",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = onNavigateBack,
                actionIcon = Icons.Default.FilterList,
                onActionIconClick = { showFilterDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Top Targets Section
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
                    Text(
                        text = "Top Lừa Đảo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (topTargets.isEmpty()) {
                        Text(
                            text = "Chưa có dữ liệu",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        topTargets.forEachIndexed { index, (target, count) ->
                            TopTargetItem(
                                rank = index + 1,
                                target = target,
                                count = count,
                                dangerLevel = viewModel.getDangerLevelForCount(count),
                                dangerColor = viewModel.getDangerLevelColorForCount(count)
                            )
                            if (index < topTargets.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                reports.isEmpty() -> {
                    EmptyView()
                }
                else -> {
                    ReportList(reports = reports, navController = navController)
                }
            }
        }

        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onFilterSelected = { type ->
                    if (type.isNullOrEmpty()) {
                        viewModel.loadAllReports()
                    } else {
                        viewModel.filterReportByType(
                            type = type,
                            onSuccess = { },
                            onFailure = {
                                Log.d( "ReportDataScreen", "Error filtering reports: $it")
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
fun ReportList(reports: List<ReportItem>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { report ->
            ReportCard(
                report = report,
                onClick = { navController.navigate("report_detail/${report.id}")
                    Log.d("ReportDataScreen", "Navigating to report detail for ${report.id}") }
            )
        }
    }
}

@Composable
fun ReportCard(
    report: ReportItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (report.check) "Đã xác thực" else "Chưa xác thực",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (report.check) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )

                TypeBadge(type = report.type)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Đối tượng: ${report.target}",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nội dung lừa đảo: ${report.description}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatTimestamp(report.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TypeBadge(type: String) {
    val (backgroundColor, textColor) = when (type) {
        "phone" -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        "url" -> Pair(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        "card" -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Text(
            text = when (type) {
                "phone" -> "Số điện thoại"
                "url" -> "Website"
                else -> "Tài khoản"
            },
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Báo cáo sẽ sớm xuất hiện khi bạn báo cáo thông tin lừa đảo",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                color = MaterialTheme.colorScheme.onSurface
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
                Text(
                    "Bỏ qua",
                    color = MaterialTheme.colorScheme.primary
                )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(text = text)
    }
}

@Composable
private fun TopTargetItem(
    rank: Int,
    target: String,
    count: Int,
    dangerLevel: String,
    dangerColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = when (rank) {
                        1 -> Color(0xFFFFD700) // Gold
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Target Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = target,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count báo cáo",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Danger Level
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = dangerColor.copy(alpha = 0.1f)
        ) {
            Text(
                text = dangerLevel,
                color = dangerColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

