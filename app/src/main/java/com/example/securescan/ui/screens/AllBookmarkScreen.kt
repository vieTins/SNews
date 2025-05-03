package com.example.securescan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.securescan.R
import com.example.securescan.data.models.NewsItem
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.components.NewsCard
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NewsViewModel

@Composable
fun AllBookmarkScreen(
    viewModel: NewsViewModel,
    navController: NavController
) {
    val bookmarkedNews by viewModel.bookmarkedNews.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                title = "Bài viết đã lưu",
                navigationIcon = Icons.Default.ArrowBackIosNew,
                onNavigationClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BookmarkHeader()

            if (bookmarkedNews.isEmpty()) {
                EmptyBookmarkState()
            } else {

                    BookmarkList(
                        bookmarkedNews = bookmarkedNews,
                        onNewsClick = { newsId ->
                            navController.navigate("news_detail/$newsId")
                        },
                        onDeleteBookmark = { news ->
                            viewModel.toggleBookmark(news.id)
                        }
                    )
            }
        }
    }
}

@Composable
private fun BookmarkHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.news6),
            contentDescription = "News",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Xem lại những tin tức thú vị mà bạn đã lưu lại",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = baseBlue3
        )
    }
}

@Composable
private fun EmptyBookmarkState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "No Bookmarks",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chưa có bài viết nào được lưu",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun BookmarkList(
    bookmarkedNews: List<NewsItem>,
    onNewsClick: (String) -> Unit,
    onDeleteBookmark: (NewsItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bookmarkedNews) { news ->
            BookmarkCard(
                news = news,
                onNewsClick = onNewsClick,
                onDeleteBookmark = onDeleteBookmark
            )
        }
    }
}

@Composable
private fun BookmarkCard(
    news: NewsItem,
    onNewsClick: (String) -> Unit,
    onDeleteBookmark: (NewsItem) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Card item chính
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNewsClick(news.id) }
        ) {
            NewsCard(
                newsItem = news,
                onNewsClick = { /* Bỏ qua vì đã xử lý ở trên */ }
            )

            // Icon bookmark nhỏ ở góc phải trên của card
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "Xóa bookmark",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 13.dp)
                    .size(18.dp)
                    .clickable { showDialog = true }
            )
        }

        // Dialog xác nhận xóa
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "Xóa bookmark",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        "Bạn có chắc muốn xóa bài viết này khỏi danh sách đã lưu?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onDeleteBookmark(news)
                        }
                    ) {
                        Text(
                            "Xóa",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}