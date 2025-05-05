package com.example.securescan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.securescan.ui.components.AppTopBar
import com.example.securescan.ui.components.NewsCard
import com.example.securescan.ui.theme.White
import com.example.securescan.ui.theme.baseBlue3
import com.example.securescan.viewmodel.NewsViewModel

@Composable
fun AllNewsScreen(
    navController: NavController,
    viewModel: NewsViewModel = viewModel()
) {
    // State for search
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    // State for filter
    val filterOptions = listOf("Tất cả", "Tin tức", "Cảnh báo", "Cập Nhật" , "Bảo Mật")
    var selectedFilter by remember { mutableIntStateOf(0) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    // State for sort
    val sortOptions = listOf("Mới nhất", "Xem nhiều nhất", "Thời gian đọc")
    var selectedSort by remember { mutableIntStateOf(0) }

    val news by viewModel.allNews.collectAsState()
    val filteredNews = news.filter {
        when (selectedFilter) {
            1 -> it.tag.equals("NEWS", ignoreCase = true)
            2 -> it.tag.equals("WARNING", ignoreCase = true)
            3 -> it.tag.equals("UPDATES", ignoreCase = true)
            4 -> it.tag.equals("SECURITY", ignoreCase = true)
            else -> true
        }
    }.let {
        when (selectedSort) {
            1 -> it.sortedByDescending { n -> n.readCount }
            2 -> it.sortedBy { n -> n.readTime }
            else -> it.sortedByDescending { n -> n.date.toLongOrNull() ?: 0L }
        }
    }.filter {
        if (searchQuery.isBlank()) true
        else it.title.contains(searchQuery, true) || it.content.contains(searchQuery, true)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // AppBar/Search overlay
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        placeholder = { Text("Tìm kiếm tin tức...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Search Icon"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                isSearching = false
                                searchQuery = ""
                            }) {
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
                    title = "Tất cả tin tức",
                    navigationIcon = Icons.Default.ArrowBackIosNew,
                    onNavigationClick = { navController.popBackStack() },
                    actionIcon = Icons.Default.Search,
                    onActionIconClick = { isSearching = true }
                )
            }

            // Filter & Sort Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Dropdown
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .height(40.dp)
                            .clickable(onClick = { filterMenuExpanded = true })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = baseBlue3,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lọc:",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = filterOptions[selectedFilter],
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = filterMenuExpanded,
                        onDismissRequest = { filterMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        filterOptions.forEachIndexed { idx, label ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedFilter = idx
                                    filterMenuExpanded = false
                                },
                                text = {
                                    Text(
                                        text = label,
                                        color = if (selectedFilter == idx) baseBlue3 else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Sort Chips
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    sortOptions.forEachIndexed { idx, label ->
                        SortChip(
                            text = label,
                            selected = selectedSort == idx,
                            onClick = { selectedSort = idx },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            // News List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNews) { newsItem ->
                    NewsCard(
                        newsItem = newsItem,
                        onNewsClick = { 
                            viewModel.incrementReadCount(newsItem.id)
                            navController.navigate("news_detail/${newsItem.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) baseBlue3 else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (selected) 4.dp else 0.dp,
        modifier = modifier
            .height(36.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                color = if (selected) White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}