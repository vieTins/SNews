package com.example.securescan.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.securescan.data.models.User
import com.example.securescan.viewmodel.UserViewModel
import com.example.securescan.ui.theme.BackgroundColor
import com.example.securescan.ui.theme.DeepBlue
import com.example.securescan.ui.theme.PrimaryBlue
import com.example.securescan.ui.theme.White

@Composable
fun EditProfileScreen(
    onSaveClick: () -> Unit
) {
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.user
    var name by remember { mutableStateOf(user.name) }
    var phone by remember { mutableStateOf(user.phone) }
    var email by remember { mutableStateOf(user.email) }
    var city by remember { mutableStateOf(user.city) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val isLoading by userViewModel.isLoading

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
        }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DeepBlue, PrimaryBlue)))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Chỉnh sửa thông tin",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.value != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUri.value)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!user.profilePic.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.profilePic,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = White,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { showImageSourceDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-20).dp, y = 10.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Avatar",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            EditableField("Họ và tên", name) { name = it }
            EditableField("Số điện thoại", phone) { phone = it }
            EditableField("Email", email) { email = it }
            EditableField("Tỉnh/Thành phố", city) { city = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedUser = user.copy(
                        name = name,
                        phone = phone,
                        email = email,
                        city = city,
                        profilePic = imageUri.value?.toString() ?: user.profilePic
                    )
                    userViewModel.updateUser(context , updatedUser)
                    onSaveClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lưu thay đổi", fontWeight = FontWeight.Bold, color = White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val message by userViewModel.message
            val isSuccess by userViewModel.isSuccess

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = if (isSuccess) Color.Green else Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            }
        }

        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text("Chọn ảnh từ") },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showImageSourceDialog = false }) {
                        Text("Đóng")
                    }
                },
                text = {
                    Column {
                        ImageSourceOption(
                            icon = Icons.Default.PhotoLibrary,
                            text = "Thư viện ảnh",
                            onClick = {
                                pickImageLauncher.launch("image/*")
                                showImageSourceDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang cập nhật...", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun ImageSourceOption(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
}