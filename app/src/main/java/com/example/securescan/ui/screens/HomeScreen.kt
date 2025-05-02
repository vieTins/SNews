package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DataThresholding
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.data.models.User
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.theme.FunctionGreen
import com.example.securescan.ui.theme.FunctionOrange
import com.example.securescan.ui.theme.FunctionOrangeDark
import com.example.securescan.ui.theme.FunctionPurple
import com.example.securescan.ui.theme.FunctionPurpleDark
import com.example.securescan.ui.theme.FunctionTeal
import com.example.securescan.ui.theme.FunctionTealDark
import com.example.securescan.ui.theme.SurfaceLight
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NewsViewModel
import com.example.securescan.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel : UserViewModel = viewModel()
    val user by viewModel.user
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastClickMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(2000)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(user)

            // Main content - có thể kéo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Thêm padding để tránh FAB
                ) {
                    item { SearchBar() }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item { SecurityCarousel() }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item {
                        FunctionsSection(
                            navController = navController,
                            onFunctionClick = { message ->
                                lastClickMessage = message
                                showSnackbar = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item { NewsSection(navController) }
                }
            }

//            BottomNavigation()
        }

        // Hiển thị thông báo click
        AnimatedVisibility(
            visible = showSnackbar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = lastClickMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TopAppBar(user : User) {
    AppTopBar(
        title = user.name,
        leadingIconUrl = user.profilePic,
        background = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun SearchBar() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                // Xử lý click vào search bar
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Tìm kiếm công cụ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SecurityCarousel() {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {

        val pagerState = rememberPagerState(pageCount = { 3 })
        val coroutineScope = rememberCoroutineScope()

        // Auto-scroll carousel
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                SecurityCard(page)
            }

            // Indicator dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityCard(page: Int) {
    val gradientColors = when (page) {
        0 -> listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
        1 -> listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
        else -> listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.primary)
    }

    val icons = listOf(
        Icons.Default.Security,
        Icons.Default.Lock,
        Icons.Default.Phone
    )

    val titles = listOf(
        "CẨM NANG AN TOÀN THÔNG TIN",
        "BẢO VỆ DỮ LIỆU\nCÁ NHÂN",
        "CẢNH BÁO LỪA ĐẢO\nTRỰC TUYẾN"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text content
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .weight(3f)
                ) {
                    Text(
                        text = titles[page],
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Xem ngay",
                            color = baseBlue3,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Icon
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icons[page],
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun FunctionsSection(navController: NavController, onFunctionClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Tính năng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionItem(
                icon = Icons.Default.Call,
                title = "Kiểm tra SĐT",
                gradientColors = listOf(FunctionGreen, FunctionGreen),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("check_phone_bank")
                }
            )
            FunctionItem(
                icon = Icons.Default.Language,
                title = "Kiểm tra Web",
                gradientColors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("scan")
                }
            )
            FunctionItem(
                icon = Icons.Default.CreditCard,
                title = "Kiểm tra STK",
                gradientColors = listOf(FunctionPurple, FunctionPurpleDark),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("check_phone_bank")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionItem(
                icon = Icons.Default.DataThresholding,
                title = "Dữ Liệu Lừa Đảo",
                gradientColors = listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("report_data")
                }
            )
            FunctionItem(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = "Báo cáo",
                gradientColors = listOf(FunctionOrange, FunctionOrangeDark),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("report")
                }
            )
            FunctionItem(
                icon = Icons.Default.Info,
                title = "Hướng dẫn",
                gradientColors = listOf(FunctionTeal, FunctionTealDark),
                modifier = Modifier.weight(1f),
                onClick = { onFunctionClick("Bạn đã click vào Hướng dẫn") }
            )
        }
    }
}

@Composable
fun FunctionItem(
    icon: ImageVector,
    title: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(4.dp, CircleShape)
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NewsSection(navController: NavController) {
    val viewModelNews: NewsViewModel = viewModel()
    val newsList by viewModelNews.allNews.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Tin tức & Cảnh báo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Danh sách tin tức - tôi chỉ muốn 3 tin tức
        val newsToShow = newsList.take(3)
        newsToShow.forEach { news ->
            NewsItem(
                title = news.title,
                date = news.date,
                accentColor = Color(android.graphics.Color.parseColor(news.tagColor)),
                onClick = {
                    navController.navigate("news_detail/${news.id}")
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun NewsItem(title: String, date: String, accentColor: Color, onClick: () -> Unit = {}) {

    val timestamp = date.toLongOrNull() ?: 0L // Chuyển sang Long (nếu không thành công, mặc định 0L)
    val date1 = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = formatter.format(date1)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored vertical indicator
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

