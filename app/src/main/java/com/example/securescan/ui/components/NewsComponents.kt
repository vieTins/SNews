package com.example.securescan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.securescan.data.models.NewsItem
import com.example.securescan.ui.theme.White
import com.example.securescan.ui.theme.baseBlue3
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewsCard(
    newsItem: NewsItem, 
    onNewsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = formatTimestamp(newsItem.date)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onNewsClick(newsItem.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = newsItem.imageRes,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = newsItem.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(
                        icon = Icons.Rounded.DateRange,
                        text = formattedDate
                    )

                    InfoChip(
                        icon = Icons.Rounded.AccessTime,
                        text = "${newsItem.readTime} phút đọc"
                    )

                    InfoChip(
                        icon = Icons.Rounded.RemoveRedEye,
                        text = "${newsItem.readTime} lượt xem"
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedNewsCard(news: NewsItem, onNewsClick: (String) -> Unit) {
    val formattedDate = formatTimestamp(news.date)

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { onNewsClick(news.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = news.imageRes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 400f
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                NewsTag(tag = news.tag, tagColor = news.tagColor)
                Spacer(modifier = Modifier.height(8.dp))
                NewsTitle(title = news.title)
                Spacer(modifier = Modifier.height(4.dp))
                NewsMetadata(date = formattedDate, readTime = news.readTime)
            }
        }
    }
}

@Composable
fun NewsTag(tag: String, tagColor: String) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = getColorFromString(tagColor)
        )
    ) {
        Text(
            text = tag,
            color = White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun NewsTitle(title: String) {
    Text(
        text = title,
        color = White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun NewsMetadata(date: String, readTime: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = White.copy(alpha = 0.8f),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = date,
            color = White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = White.copy(alpha = 0.8f),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$readTime phút đọc",
            color = White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(10.dp)
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 10.sp
        )
    }
}

@Composable
fun getColorFromString(colorString: String): Color {
    return when (colorString.uppercase()) {
        "RED" -> MaterialTheme.colorScheme.error
        "BLUE" -> baseBlue3
        "YELLOW" -> MaterialTheme.colorScheme.tertiary
        "GREEN" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

fun formatTimestamp(timestamp: String): String {
    return try {
        val timestampLong = timestamp.toLongOrNull() ?: return timestamp
        val date = Date(timestampLong)
        val formatter = SimpleDateFormat("dd-MM, HH:mm", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        timestamp
    }
} 