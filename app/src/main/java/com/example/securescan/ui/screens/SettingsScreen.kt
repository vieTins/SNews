package com.example.securescan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.securescan.data.models.User
import com.example.securescan.viewmodel.AuthViewModel
import com.example.securescan.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user
    var darkModeEnabled by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top app bar - cố định
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
                    // Settings icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(LightBlue.copy(alpha = 0.8f), LightBlue.copy(alpha = 0.1f)),
                                    radius = 20f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title
                    Text(
                        text = "Cài đặt",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // User profile section
            UserProfileSection(navController, user)

            Spacer(modifier = Modifier.height(16.dp))

            // Settings categories
            SettingsCategory(title = "Giao diện") {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Chế độ tối",
                    isChecked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }

            SettingsCategory(title = "Thông tin ứng dụng") {
                SettingsItem(
                    icon = Icons.Default.QuestionAnswer,
                    title = "Câu hỏi thường gặp",
                    onClick = { /* Xử lý khi click vào câu hỏi thường gặp */ }
                )

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Giới thiệu",
                    onClick = { /* Xử lý khi click vào giới thiệu */ }
                )

                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Điều khoản và thỏa thuận sử dụng",
                    onClick = { /* Xử lý khi click vào điều khoản */ }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logout button
            LogoutButton(
                authViewModel = authViewModel,
                onLogout = onLogout
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Version info
            Text(
                text = "Secure Scan v1.0.0",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserProfileSection(navController: NavController , user : User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
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
                        tint = Color.Gray,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = user.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button
            Button(
                onClick = {
                    navController.navigate("edit_profile") // Navigate to edit profile screen
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Quản lý tài khoản",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryBlue
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }

    Divider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = Color(0xFFEEEEEE)
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryBlue
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = PrimaryBlue,
                uncheckedThumbColor = White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }

    Divider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = Color(0xFFEEEEEE)
    )
}

@Composable
fun LogoutButton(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Xác nhận đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onLogout()
                }) {
                    Text("Đăng xuất", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Red
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Đăng xuất",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

