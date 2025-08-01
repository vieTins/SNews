package com.example.securescan.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.components.NewsCard
import com.example.securescan.ui.theme.FunctionGreen
import com.example.securescan.ui.theme.FunctionOrange
import com.example.securescan.ui.theme.FunctionOrangeDark
import com.example.securescan.ui.theme.FunctionPurple
import com.example.securescan.ui.theme.FunctionPurpleDark
import com.example.securescan.ui.theme.FunctionTeal
import com.example.securescan.ui.theme.FunctionTealDark
import com.example.securescan.ui.theme.SurfaceLight
import com.example.securescan.ui.theme.White
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NewsViewModel
import com.example.securescan.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastClickMessage by remember { mutableStateOf("") }
    var showSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(showSnackBar) {
        if (showSnackBar) {
            delay(2000)
            showSnackBar = false
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "",
                background = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (user.profilePic != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = user.profilePic,
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                            }

                            Text(
                                text = "Xin chào!",
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = user.name,
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount < -50) { // Swipe left threshold
                                navController.navigate("rss_news")
                            }
                        }
                    )
                }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SecurityCarousel(navController) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    FunctionsSection(
                        navController = navController,
                        onFunctionClick = { message ->
                            lastClickMessage = message
                            showSnackBar = true
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { NewsSection(navController) }
            }

            // Featured News Button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate("rss_news") },
                    shape = RoundedCornerShape(16.dp),
                    color = baseBlue3,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Tin nổi bật",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Tin nổi bật",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SecurityCarousel(navController : NavController) {
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
                SecurityCard(page = page, navController = navController)
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
fun SecurityCard(page: Int , navController: NavController) {
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
                        onClick = { 
                            when (page) {
                                0 -> navController.navigate("all_news")
                                1 -> navController.navigate("scan")
                                2 -> navController.navigate("report_data")
                            }
                        },
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
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Tính năng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionItem(
                icon = Icons.Default.Call,
                title = "Kiểm tra SĐT",
                gradientColors = listOf(FunctionGreen, FunctionGreen),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("check_phone_bank") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FunctionItem(
                icon = Icons.Default.Language,
                title = "Kiểm tra Web",
                gradientColors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("scan") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FunctionItem(
                icon = Icons.Default.CreditCard,
                title = "Kiểm tra STK",
                gradientColors = listOf(FunctionPurple, FunctionPurpleDark),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("check_phone_bank") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionItem(
                icon = Icons.Default.DataThresholding,
                title = "Dữ Liệu Lừa Đảo",
                gradientColors = listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("report_data") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FunctionItem(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = "Báo cáo",
                gradientColors = listOf(FunctionOrange, FunctionOrangeDark),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("report") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FunctionItem(
                icon = Icons.Default.Info,
                title = "Hướng dẫn",
                gradientColors = listOf(FunctionTeal, FunctionTealDark),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("about_app/${AboutSection.FAQ}") }
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
            ) { onClick() }
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
            fontSize = 12.sp,
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

    ) {
        Text(
            text = "Tin tức & Cảnh báo",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val newsToShow = newsList.take(3)
        newsToShow.forEach { news ->
            NewsCard(
                newsItem = news,
                onNewsClick = { newsId ->
                    viewModelNews.incrementReadCount(newsId)
                    navController.navigate("news_detail/$newsId")
                }
            )
        }
    }
}

