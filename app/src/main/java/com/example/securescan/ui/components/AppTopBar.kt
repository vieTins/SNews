package com.example.securescan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.securescan.ui.theme.White

@Composable
fun AppTopBar(
    title: String,
    leadingIconUrl: String? = null,
    onLeadingIconClick: (() -> Unit)? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onActionIconClick: (() -> Unit)? = null,
    background: Color = MaterialTheme.colorScheme.primary,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .height(56.dp)
            .padding(8.dp)
    ) {
        // Title luôn ở chính giữa Box
        Text(
            text = title,
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )

        // Các icon trái/phải
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // navigation icon
            navigationIcon?.let {
                IconButton(onClick = { onNavigationClick?.invoke() }) {
                    Icon(
                        imageVector = it,
                        contentDescription = "Navigation Icon",
                        tint = White
                    )
                }
            }

            // leading icon (ảnh đại diện)
            if (leadingIconUrl != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = leadingIconUrl,
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            // Spacer để đẩy icon sang trái, title vẫn ở giữa Box
            Spacer(modifier = Modifier.weight(1f))

            // trailing content hoặc action icon
            if (trailingContent != null) {
                trailingContent()
            } else {
                actionIcon?.let {
                    IconButton(onClick = { onActionIconClick?.invoke() }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = it,
                                contentDescription = "Action Icon",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
