package com.example.securescan.ui.theme
import androidx.compose.ui.graphics.Color

// Màu cơ sở mới
val baseBlue1 = Color(0xFF1E88E5)
val baseBlue2 = Color(0xFF1976D2)
val baseBlue3 = Color(0xFF2746B9).copy(alpha = 0.95f)
val accentBlue1 = Color(0xFF29A9F3)
val accentBlue2 = Color(0xFF2196F3)

// Màu thương hiệu chính
val PrimaryBlue = baseBlue1 // Sử dụng baseBlue1 làm màu chính
val SecondaryBlue = baseBlue2 // Sử dụng baseBlue2 làm màu phụ
val AccentBlue = accentBlue1 // Sử dụng accentBlue1 làm điểm nhấn
val LightBlue = Color(0xFFBBDEFB) // Làm nhẹ hơn để phù hợp với màu cơ sở
val PaleBlue = Color(0xFFE3F2FD) // Làm nhẹ hơn để phù hợp với màu cơ sở
val DeepBlue = Color(0xFF0D47A1) // Màu xanh đậm cho độ tương phản tốt

// Màu cơ bản khác
val White = Color(0xFFFFFFFF)
val BackgroundColor = Color(0xFFF5F9FF) // Làm nhẹ hơn một chút để tạo cảm giác tươi sáng

// Màu đơn sắc thay thế cho gradient
val SolidBlue = baseBlue1

// Màu chức năng
val FunctionBlue = baseBlue1
val FunctionBlueDark = baseBlue2
val FunctionGreen = Color(0xFF43A047) // Điều chỉnh màu xanh lá để hài hòa hơn
val FunctionGreenDark = Color(0xFF2E7D32)
val FunctionOrange = Color(0xFFFF9800) // Làm tươi sáng hơn
val FunctionOrangeDark = Color(0xFFE65100)
val FunctionRed = Color(0xFFF44336) // Làm tươi sáng hơn để dễ thấy
val FunctionRedDark = Color(0xFFD32F2F)
val FunctionPurple = Color(0xFF7B1FA2) // Điều chỉnh để phối màu tốt hơn
val FunctionPurpleDark = Color(0xFF6A1B9A)
val FunctionTeal = Color(0xFF00ACC1) // Điều chỉnh để phối màu tốt hơn với màu xanh
val FunctionTealDark = Color(0xFF00838F)

// Màu trạng thái
val ErrorRed = Color(0xFFF44336) // Điều chỉnh để dễ nhìn
val SuccessTeal = Color(0xFF26A69A)
val InfoBlue = accentBlue2 // Sử dụng accentBlue2 làm màu thông tin
val WarningOrange = Color(0xFFFFA726)

// Màu bề mặt
val SurfaceLight = Color(0xFFFFFFFF) // Trắng đơn giản
val SurfaceDark = Color(0xFF121920) // Màu tối có chút xanh, dễ chịu hơn
val SurfaceVariantLight = Color(0xFFF5F5F5) // Xám nhẹ đơn giản
val SurfaceVariantDark = Color(0xFF252B32) // Màu tối có chút xanh, dễ chịu hơn

// Màu chữ
val TextPrimaryLight = Color(0xFF121212) // Màu đen đơn giản
val TextPrimaryDark = Color(0xDDFFFFFF) // Màu trắng giảm độ sáng (87% opacity)
val TextSecondaryLight = Color(0xFF555555) // Màu xám đơn giản
val TextSecondaryDark = Color(0xAAFFFFFF) // Màu trắng giảm độ sáng nhiều hơn (67% opacity)

// Màu bóng đổ
val CardShadow = Color(0x14000000) // Làm nhẹ hơn để không quá nặng

// Các màu bổ sung
val TealPrimary = Color(0xFF00ACC1) // Điều chỉnh để phối hợp tốt hơn với màu xanh
val IndigoPrimary = Color(0xFF3949AB)
val LightBlueVariant = accentBlue1
val DarkBlueVariant = baseBlue2
val LightGreen = Color(0xFF66BB6A)
val DarkGreen = Color(0xFF388E3C)
val LightOrange = Color(0xFFFFB74D)
val DarkOrange = Color(0xFFF57C00)
val LightPink = Color(0xFFEC407A)
val DarkPink = Color(0xFFC2185B)
val LightPurple = Color(0xFF9575CD) // Làm sáng hơn
val DarkPurple = Color(0xFF5E35B1)
val TealAccent = Color(0xFF26A69A)
val TealDark = Color(0xFF00796B)