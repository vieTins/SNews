package com.example.securescan.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.data.models.NewsItem
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.components.FeaturedNewsCard
import com.example.securescan.ui.components.NewsCard
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NewsViewModel

@Composable
fun NewsScreen(
    onNavigateToNewsDetail: (String) -> Unit = {},
    navController: NavController
) {
    val viewModel: NewsViewModel = viewModel()
    val newsList by viewModel.allNews.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val filteredNews = if (searchQuery.isBlank()) {
        newsList
    } else {
        newsList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true) ||
                    it.createBy.contains(searchQuery, ignoreCase = true) ||
                    it.tag.contains(searchQuery , ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NewsTopAppBar(
                onSearchClicked = { isSearching = true },
                isSearching = isSearching,
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                onCloseSearch = {
                    isSearching = false
                    searchQuery = ""
                },
                onBookmarkClick = {
                    navController.navigate("bookmarks")
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (!isSearching) {
                    item {
                        NewsCarousel(newsList.take(5), onNewsClick = { newsId ->
                            viewModel.incrementReadCount(newsId)
                            onNavigateToNewsDetail(newsId)
                        })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    NewsListHeader(
                        title = if (isSearching) "Kết quả tìm kiếm" else "Tất cả tin tức",
                        onViewAllClick = {
                            navController.navigate("all_news")
                        }
                    )
                }

                if (filteredNews.isEmpty() && isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Không tìm thấy kết quả phù hợp",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredNews) { newsItem ->
                        NewsCard(
                            newsItem = newsItem, 
                            onNewsClick = { newsId ->
                                viewModel.incrementReadCount(newsId)
                                onNavigateToNewsDetail(newsId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsTopAppBar(
    onSearchClicked: () -> Unit,
    isSearching: Boolean,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseSearch: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    if (isSearching) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Tìm kiếm tin tức...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onCloseSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Search"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    } else {
        AppTopBar(
            title = "Tin tức & Cảnh báo",
            navigationIcon = Icons.Default.Bookmarks,
            onNavigationClick = onBookmarkClick,
            actionIcon = Icons.Outlined.Search,
            onActionIconClick = onSearchClicked,
        )
    }
}

@Composable
fun NewsCarousel(featuredNews: List<NewsItem>, onNewsClick: (String) -> Unit) {
    val filteredFeaturedNews = featuredNews.filter { it.isFeatured }

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        Text(
            text = "Tin nổi bật",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(filteredFeaturedNews) { news ->
                FeaturedNewsCard(news = news, onNewsClick = onNewsClick)
            }
        }
    }
}

@Composable
fun NewsListHeader(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Xem tất cả",
            fontSize = 14.sp,
            color = baseBlue3,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onViewAllClick)
        )
    }
}