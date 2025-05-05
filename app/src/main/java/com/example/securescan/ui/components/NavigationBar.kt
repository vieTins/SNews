package com.example.securescan.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigation(
    onNavigate: (String) -> Unit = {} // Callback function for navigation
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        var selectedItem by remember { mutableStateOf(0) } // Default to Home selected

        // Main navigation bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 1: Home
                IconNavigationItem(
                    icon = Icons.Rounded.Home,
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        onNavigate("home")
                    }
                )

                // Item 2: News/Article
                IconNavigationItem(
                    icon = Icons.Rounded.Article,
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        onNavigate("article")
                    }
                )

                // Placeholder for the center item (Globe)
                Spacer(modifier = Modifier.width(56.dp))

                // Item 4: Notifications
                IconNavigationItem(
                    icon = Icons.Rounded.Notifications,
                    selected = selectedItem == 3,
                    onClick = {
                        selectedItem = 3
                        onNavigate("notifications")
                    }
                )

                // Item 5: Settings
                IconNavigationItem(
                    icon = Icons.Rounded.Settings,
                    selected = selectedItem == 4,
                    onClick = {
                        selectedItem = 4
                        onNavigate("settings")
                    }
                )
            }
        }

        // Animated Center Globe Button
        AnimatedGlobeButton(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp),
            onClick = {
                onNavigate("scan")
                selectedItem = 2
            }
        )
    }
}

@Composable
fun AnimatedGlobeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Interaction source to detect press state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animation for press effect
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press"
    )

    // Rotation animation for globe
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutQuart),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Shimmer effect
    val shimmerOffsetX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Color animation
    val baseBlue1 = Color(0xFF1E88E5)
    val baseBlue2 = Color(0xFF1976D2)
    val accentBlue1 = Color(0xFF29A9F3)
    val accentBlue2 = Color(0xFF2196F3)

    val primaryColor by infiniteTransition.animateColor(
        initialValue = accentBlue1,
        targetValue = accentBlue2,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "primary_color"
    )

    val secondaryColor by infiniteTransition.animateColor(
        initialValue = baseBlue1,
        targetValue = baseBlue2,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondary_color"
    )

    Box(
        modifier = modifier
            .scale(pressScale)
            .shadow(12.dp, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }, // Call the onClick callback
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(pulseScale)
                .alpha(0.35f)
                .blur(radius = 4.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main button background
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor,
                            secondaryColor
                        ),
                        start = Offset(shimmerOffsetX, 0f),
                        end = Offset(shimmerOffsetX + 100f, 100f)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.8f)
                        ),
                        start = Offset(shimmerOffsetX, 0f),
                        end = Offset(shimmerOffsetX + 100f, 100f)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Light overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.3f, size.height * 0.3f),
                        radius = size.width * 0.5f
                    )
                )
            }

            // Rotating globe effect
            Canvas(modifier = Modifier
                .size(44.dp)
                .rotate(rotation)
            ) {
                // Draw orbit lines
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = size.width * 0.35f,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = size.width * 0.4f,
                    style = Stroke(width = 0.5f, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f))
                )
            }

            // Use a scan icon instead of globe icon for scan functionality
            Icon(
                imageVector = Icons.Rounded.QrCodeScanner, // Changed to QR scanner icon
                contentDescription = "Scan QR Code",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun IconNavigationItem(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFFB4C1E8),
        animationSpec = tween(300),
        label = "color"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = animatedColor,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
        )
    }
}