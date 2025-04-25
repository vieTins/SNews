package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.securescan.data.models.User
import com.example.securescan.viewmodel.NewsViewModel
import com.example.securescan.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val PrimaryBlue = Color(0xFF3674B5)
val SecondaryBlue = Color(0xFF578FCA)
val AccentBlue = Color(0xFF1E88E5)
val LightBlue = Color(0xFFA1E3F9)
val PaleBlue = Color(0xFFD1F8EF)
val DeepBlue = Color(0xFF0D47A1)
val Red = Color(0xFFFF3B30)
val White = Color(0xFFFFFFFF)
val BackgroundColor = Color(0xFFF8FBFF)

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
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top app bar - cố định
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF323232))
            ) {
                Text(
                    text = lastClickMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TopAppBar(user : User) {
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
            // Shield icon với hiệu ứng ánh sáng
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                LightBlue.copy(alpha = 0.8f),
                                LightBlue.copy(alpha = 0.1f)
                            ),
                            radius = 20f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield Icon",
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name
            Text(
                text = user.name,
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
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
        colors = CardDefaults.cardColors(containerColor = White),
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
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Nhập số điện thoại cần kiểm tra",
                color = Color.Gray,
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
        Text(
            text = "Cẩm nang An toàn thông tin",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                                if (isSelected) White else White.copy(alpha = 0.5f)
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
        0 -> listOf(SecondaryBlue, DeepBlue)
        1 -> listOf(Color(0xFF26A69A), Color(0xFF00796B))
        else -> listOf(Color(0xFF5C6BC0), Color(0xFF3949AB))
    }

    val icons = listOf(
        Icons.Default.Security,
        Icons.Default.Lock,
        Icons.Default.Phone
    )

    val titles = listOf(
        "CẨM NANG ĐẢM BẢO\nAN TOÀN THÔNG TIN",
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = White,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Xem ngay",
                            color = gradientColors[0],
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
                        tint = White.copy(alpha = 0.8f)
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
            text = "Chức năng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue
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
                gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("check_phone_bank")
                }
            )
            FunctionItem(
                icon = Icons.Default.Language,
                title = "Kiểm tra Web",
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF388E3C)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("scan")
                }
            )
            FunctionItem(
                icon = Icons.Default.CreditCard,
                title = "Kiểm tra STK",
                gradientColors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00)),
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
                gradientColors = listOf(Color(0xFFEC407A), Color(0xFFC2185B)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("report_data")
                }
            )
            FunctionItem(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = "Báo cáo",
                gradientColors = listOf(Color(0xFF7E57C2), Color(0xFF512DA8)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("report")
                }
            )
            FunctionItem(
                icon = Icons.Default.Info,
                title = "Hướng dẫn",
                gradientColors = listOf(Color(0xFF26A69A), Color(0xFF00796B)),
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
                tint = White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlue,
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
            color = DeepBlue
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Danh sách tin tức
        newsList.forEach { news ->
            NewsItem(
                title = news.title,
                date = news.date,
                accentColor = Color(android.graphics.Color.parseColor(news.tagColor))
            , onClick = {
                    navController.navigate("news_detail/${news.id}")
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun NewsItem(title: String, date: String, accentColor: Color , onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
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
                    color = DeepBlue,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Arrow icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}

