package com.example.securescan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.securescan.data.models.CommentPost
import com.example.securescan.ui.theme.baseBlue3

@Composable
fun CommentSection(
    comments: List<CommentPost>,
    currentUserId: String,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        // Comments List
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            comments.forEach { comment ->
                key(comment.id) {
                    CommentBubble(
                        comment = comment,
                        isCurrentUser = comment.userId == currentUserId
                    )
                }
            }
        }

        // Comment Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Nhập bình luận...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    trailingIcon = {
                        if (commentText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onSend(commentText)
                                    commentText = ""
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = baseBlue3,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Gửi",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CommentBubble(
    comment: CommentPost,
    isCurrentUser: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            UserAvatar(comment.profilePic)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (isCurrentUser) 16.dp else 4.dp,
                topEnd = if (isCurrentUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .widthIn(max = 260.dp)
            ) {
                if (!isCurrentUser) {
                    Text(
                        text = comment.userName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Text(
                    text = comment.content,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )

                Text(
                    text = getTimeAgo(comment.timestamp),
                    fontSize = 10.sp,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp).align(Alignment.End)
                )
            }
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar(comment.profilePic)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDialog(
    comments: List<CommentPost>,
    currentUserId: String,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(0.9f),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bình luận",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Comment Section
            CommentSection(
                comments = comments,
                currentUserId = currentUserId,
                onSend = onSend,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun UserAvatar(profilePic: String?) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        if (profilePic != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePic)
                    .crossfade(true)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun getTimeAgo(timestamp: Long): String {
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