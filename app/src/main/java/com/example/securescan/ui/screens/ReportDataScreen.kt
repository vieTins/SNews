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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDataScreen(
    userId: String,
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit,
    navController: NavController
) {
    val reports by viewModel.filteredReports
    val topTargets by viewModel.topTargets
    val isLoading by viewModel.isLoading
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAdvancedSearch by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    var selectedVerificationStatus by remember { mutableStateOf<Boolean?>(null) }

    // Update search when parameters change
    LaunchedEffect(searchQuery, selectedDateRange, selectedVerificationStatus) {
        viewModel.searchReports(
            query = searchQuery,
            dateRange = selectedDateRange,
            verificationStatus = selectedVerificationStatus
        )
    }

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

            // Search Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Main Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm kiếm báo cáo...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            IconButton(onClick = { showAdvancedSearch = !showAdvancedSearch }) {
                                Icon(
                                    if (showAdvancedSearch) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Advanced search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Advanced Search Options
                if (showAdvancedSearch) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Verification Status
                            Text(
                                text = "Trạng thái xác thực",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FilterChip(
                                    selected = selectedVerificationStatus == null,
                                    onClick = { selectedVerificationStatus = null },
                                    label = { Text("Tất cả") },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                                )
                                FilterChip(
                                    selected = selectedVerificationStatus == true,
                                    onClick = { selectedVerificationStatus = true },
                                    label = { Text("Đã xác thực") },
                                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                                )
                                FilterChip(
                                    selected = selectedVerificationStatus == false,
                                    onClick = { selectedVerificationStatus = false },
                                    label = { Text("Chưa xác thực") },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                                )
                            }

                            // Clear Filters Button
                            TextButton(
                                onClick = {
                                    selectedDateRange = null
                                    selectedVerificationStatus = null
                                    viewModel.clearSearch()
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Xóa bộ lọc")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top Targets Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Top Lừa Đảo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (topTargets.isNotEmpty()) {
                            Text(
                                text = "Xem thêm",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { /* TODO: Handle view more */ }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (topTargets.isEmpty()) {
                        Text(
                            text = "Chưa có dữ liệu",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        topTargets.take(3).forEachIndexed { index, (target, count) ->
                            TopTargetItem(
                                rank = index + 1,
                                target = target,
                                count = count,
                                dangerLevel = viewModel.getDangerLevelForCount(count),
                                dangerColor = viewModel.getDangerLevelColorForCount(count)
                            )
                            if (index < topTargets.take(3).size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 4.dp),
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
                                Log.d("ReportDataScreen", "Error filtering reports: $it")
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
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier
                .size(20.dp)
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
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Target Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = target,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$count báo cáo",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Danger Level
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = dangerColor.copy(alpha = 0.1f)
        ) {
            Text(
                text = dangerLevel,
                color = dangerColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


