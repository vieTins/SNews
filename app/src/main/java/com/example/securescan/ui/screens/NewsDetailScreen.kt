package com.example.securescan.ui.screens

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.White
import com.example.securescan.viewmodel.NewsViewModel
import com.example.securescan.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.lerp
import com.example.securescan.data.models.NewsItem
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import com.example.securescan.utils.TranslationManager
import kotlinx.coroutines.launch
import android.text.Html
import android.text.Spanned

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    viewModel: NewsViewModel,
    onBackPressed: () -> Unit = {},
    navController: NavController,
) {
    val newsItem by viewModel.getNewsById(newsId).collectAsState(initial = null)
    val userLikes by viewModel.userLikes.collectAsState()
    val userBookmarks by viewModel.userBookmarks.collectAsState()
    val isLiked = userLikes[newsId] ?: false
    val isBookmarked = userBookmarks[newsId] ?: false
    var commentText by remember { mutableStateOf("") }
    val comments by viewModel.currentComments.collectAsState()
    val viewModelUser: UserViewModel = viewModel()
    val user by viewModelUser.user
    val allNews by viewModel.allNews.collectAsState()
    
    // Translation states
    var isTranslated by remember { mutableStateOf(false) }
    var translatedContent by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    val translationManager = remember { TranslationManager() }
    val coroutineScope = rememberCoroutineScope()

    val timestampString = newsItem?.date
    val timestamp = timestampString?.toLongOrNull() ?: 0L // Chuyển sang Long (nếu không thành công, mặc định 0L)

    val date = Date(timestamp)

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = formatter.format(date)

    Log.d("NewsDetailScreen", "News item: $newsItem + id  = $newsId")

    if (newsItem == null) {
        Log.d("NewsDetailScreen", "News item is null, showing loading")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(newsId) {
        Log.d("NewsDetailScreen", "Loading comments and checking like status for post $newsId")
        viewModel.loadComments(newsId)
        viewModel.checkUserLikedPost(newsId)
        viewModel.checkUserBookmarkedPost(newsId)
    }

    LaunchedEffect(Unit) {
        viewModel.getNewsById(newsId).collect { updatedNews ->
            if (updatedNews != null) {
                // Cập nhật UI khi có thay đổi
            }
        }
    }

    fun getStyledHtmlContent(content: String): String {
        return """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    font-size: 13px;
                    line-height: 1.6;
                    padding: 7px;
                    margin: 0;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                }
                img {
                    max-width: 100%;
                    height: auto;
                    display: block;
                    margin: 10px auto;
                }
                p {
                    margin: 8px 0;
                    line-height: 1.6;
                }
                h1, h2, h3 {
                    margin: 16px 0 8px 0;
                    line-height: 1.4;
                }
                h1 { font-size: 24px; }
                h2 { font-size: 20px; }
                h3 { font-size: 18px; }
                ul, ol {
                    margin: 8px 0;
                    padding-left: 20px;
                }
                li {
                    margin: 4px 0;
                }
                blockquote {
                    margin: 8px 0;
                    padding: 8px 16px;
                    border-left: 4px solid #5E7CE2;
                    background-color: #f5f5f5;
                }
                a {
                    color: #5E7CE2;
                    text-decoration: none;
                }
                pre {
                    background-color: #f5f5f5;
                    padding: 8px;
                    border-radius: 4px;
                    overflow-x: auto;
                }
                code {
                    font-family: monospace;
                    background-color: #f5f5f5;
                    padding: 2px 4px;
                    border-radius: 4px;
                }
            </style>
        </head>
        <body>
            ${content}
        </body>
        </html>
        """
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Box {
                AsyncImage(
                    model = newsItem!!.imageRes,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(android.graphics.Color.parseColor(newsItem!!.tagColor))
                ) {
                    Text(
                        text = newsItem!!.tag.uppercase(),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = newsItem!!.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Author",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "By ${newsItem!!.createBy}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Read Time",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${newsItem!!.readTime} min read",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Translate",
                        tint = if (isTranslated) Color(0xFF5E7CE2) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    if (isTranslating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF5E7CE2)
                        )
                    } else {
                        Text(
                            text = if (isTranslated) "English" else "Tiếng Việt",
                            fontSize = 14.sp,
                            color = if (isTranslated) Color(0xFF5E7CE2) else Color.Gray,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    if (!isTranslated) {
                                        isTranslating = true
                                        try {
                                            translatedContent = translationManager.translateText(newsItem!!.content)
                                            Log.d("NewsDetailScreen", "Translated content: $translatedContent")
                                            isTranslated = true
                                        } finally {
                                            isTranslating = false
                                        }
                                    } else {
                                        isTranslated = false
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(
                            null,
                            getStyledHtmlContent(if (isTranslated) translatedContent else newsItem!!.content),
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(
                        null,
                        getStyledHtmlContent(if (isTranslated) translatedContent else newsItem!!.content),
                        "text/html",
                        "UTF-8",
                        null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        // Interaction Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Interaction Buttons Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFF5E7CE2).copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Interaction Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Like Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        Log.d("NewsDetailScreen", "Like button clicked for post $newsId")
                                        viewModel.toggleLike(newsId)
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (isLiked) Color(0xFF5E7CE2) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${newsItem!!.likeCount}",
                                    color = if (isLiked) Color(0xFF5E7CE2) else Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            // Comment Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {

                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = "Comment",
                                    tint = Color(0xFF5E7CE2),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${newsItem!!.commentCount}",
                                    color = Color(0xFF5E7CE2),
                                    fontSize = 14.sp
                                )
                            }

                            // Share Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.sharePost(newsItem!!.id) { success ->
                                            if (success) {

                                            }
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color(0xFF5E7CE2),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Share",
                                    color = Color(0xFF5E7CE2),
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.toggleBookmark(newsId)
                                    }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) Color(0xFF5E7CE2) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBookmarked) "Đã lưu" else "Lưu",
                                    color = if (isBookmarked) Color(0xFF5E7CE2) else Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Comments Section
                var isCommentsExpanded by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFF5E7CE2).copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCommentsExpanded = !isCommentsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bình luận ${newsItem!!.commentCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = if (isCommentsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isCommentsExpanded) "Thu gọn" else "Mở rộng",
                                tint = Color(0xFF5E7CE2),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (isCommentsExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Comment Input
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFF5E7CE2).copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, Color(0xFF5E7CE2).copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Small Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0E0E0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (user.profilePic != null) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user.profilePic)
                                                    .crossfade(true)
                                                    .transformations(CircleCropTransformation())
                                                    .build(),
                                                contentDescription = "Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.matchParentSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Error Icon",
                                                tint = Color.DarkGray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = commentText,
                                        onValueChange = { commentText = it },
                                        placeholder = { Text("Viết bình luận...") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent
                                        ),
                                        trailingIcon = {
                                            if (commentText.isNotBlank()) {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.addComment(
                                                            postId = newsItem!!.id,
                                                            content = commentText
                                                        ) { success ->
                                                            if (success) {
                                                                commentText = ""
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Send,
                                                        contentDescription = "Gửi bình luận",
                                                        tint = Color(0xFF5E7CE2)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Comments List
                            comments.forEach { comment ->
                                CommentItem(
                                    username = comment.userName,
                                    content = comment.content,
                                    time = getTimeAgo(comment.timestamp),
                                    likes = 0,
                                    profilePic = comment.profilePic
                                )
                                Divider(
                                    color = Color(0xFF5E7CE2).copy(alpha = 0.1f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(5.dp))
            // Related News Carousel
            val relatedNews = remember(newsItem, allNews) {
                if (newsItem != null) {
                    allNews.filter {
                        it.id != newsItem!!.id && it.tag == newsItem!!.tag
                    }.take(5)
                } else {
                    emptyList()
                }
            }
            Log.d("NewsDetailScreen", "Related news: $relatedNews")
            Log.d("NewsDetailScreen", "All news:  ${viewModel.allNews}")

            if (relatedNews.isNotEmpty()) {
                RelativeCarousel(
                    relatedNews = relatedNews,
                    onNewsClick = { newsId ->
                        navController.navigate("news_detail/$newsId")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CommentItem(
    username: String,
    content: String,
    time: String,
    likes: Int,
    profilePic: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (profilePic != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profilePic)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Error Icon",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = username,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun RelatedNewsItem(title: String, date: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored vertical indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(color)
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DeepBlue,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days ngày trước"
        hours > 0 -> "$hours giờ trước"
        minutes > 0 -> "$minutes phút trước"
        else -> "Vừa xong"
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RelativeCarousel(
    relatedNews: List<NewsItem>,
    onNewsClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { relatedNews.size })

    // Auto-scroll
    LaunchedEffect(pagerState.currentPage) {
        delay(3000L)
        val next = (pagerState.currentPage + 1) % relatedNews.size
        pagerState.animateScrollToPage(next)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Tin tức liên quan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) { page ->
            val news = relatedNews[page]

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable { onNewsClick(news.id) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = news.imageRes,
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 100.dp, height = 100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(android.graphics.Color.parseColor(news.tagColor))
                        ) {
                            Text(
                                text = news.tag.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = news.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${news.readTime} min read",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


