package com.example.securescan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securescan.R
import com.example.securescan.ui.components.SocialLoginButton
import com.example.securescan.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onFacebookSignInClick: () -> Unit,
    onAppleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val registerSuccess by viewModel.registerSuccess
    val errorMessage by viewModel.errorMessage2

    if (registerSuccess) {
        LaunchedEffect(Unit) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Đăng Ký",
                color = Color(0xFF2D4EC1),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtitle
            Text(
                text = "Tạo tài khoản mới",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Họ và tên") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF2D4EC1),
                    focusedBorderColor = Color(0xFF2D4EC1),
                    unfocusedContainerColor = Color.White
                )
            )

            // Phone field
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("Số điện thoại") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF2D4EC1),
                    focusedBorderColor = Color(0xFF2D4EC1),
                    unfocusedContainerColor = Color.White
                )
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF2D4EC1),
                    focusedBorderColor = Color(0xFF2D4EC1),
                    unfocusedContainerColor = Color.White
                )
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mật khẩu") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = painterResource(
                                id = if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                            ),
                            contentDescription = if (showPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF2D4EC1),
                    focusedBorderColor = Color(0xFF2D4EC1),
                    unfocusedContainerColor = Color.White
                )
            )

            // Register button
            Button(
                onClick = {
                    viewModel.register(email.trim(), password.trim(), name.trim(), phone.trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D4EC1)
                )
            ) {
                Text(
                    text = "Đăng ký",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Login link
            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Đã có tài khoản? Đăng nhập",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Social login section
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hoặc đăng ký bằng",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Social login buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Google
                SocialLoginButton(
                    onClick = onGoogleSignInClick,
                    iconResId = R.drawable.ic_google,
                    contentDescription = "Đăng ký bằng Google"
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Facebook
                SocialLoginButton(
                    onClick = onFacebookSignInClick,
                    iconResId = R.drawable.ic_facebook,
                    contentDescription = "Đăng ký bằng Facebook"
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Apple
                SocialLoginButton(
                    onClick = onAppleSignInClick,
                    iconResId = R.drawable.ic_apple,
                    contentDescription = "Đăng ký bằng Apple"
                )
            }
        }
    }
}

