package com.example.securescan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.securescan.ui.theme.baseBlue3

enum class AboutSection {
    FAQ,
    ABOUT,
    TERMS,
    RATE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    navController: NavController,
    section: AboutSection = AboutSection.ABOUT
) {
    val scrollState = rememberScrollState()
    val initialExpandedSection = remember { section }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = baseBlue3,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        text = when (section) {
                            AboutSection.FAQ -> "Câu hỏi thường gặp"
                            AboutSection.ABOUT -> "Giới thiệu"
                            AboutSection.TERMS -> "Điều khoản và thỏa thuận"
                            AboutSection.RATE -> "Đánh giá ứng dụng"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Header with gradient background
                AboutHeader(section = section)

                Spacer(modifier = Modifier.height(16.dp))

                // FAQ Section
                ExpandableAboutSection(
                    title = "Câu hỏi thường gặp",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    initiallyExpanded = section == AboutSection.FAQ
                ) {
                    FAQContent()
                }

                // About Section
                ExpandableAboutSection(
                    title = "Giới thiệu",
                    icon = Icons.Default.Info,
                    initiallyExpanded = section == AboutSection.ABOUT
                ) {
                    AboutContent()
                }

                // Terms Section
                ExpandableAboutSection(
                    title = "Điều khoản và thỏa thuận sử dụng",
                    icon = Icons.Default.Description,
                    initiallyExpanded = section == AboutSection.TERMS
                ) {
                    TermsContent()
                }

                // Rate App Section
                ExpandableAboutSection(
                    title = "Đánh giá ứng dụng",
                    icon = Icons.Default.StarRate,
                    initiallyExpanded = section == AboutSection.RATE
                ) {
                    RateAppContent()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App version
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Secure Scan v1.0.0",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutHeader(section: AboutSection) {
    val gradientColors = listOf(
        baseBlue3,
        baseBlue3.copy(alpha = 0.7f),
        baseBlue3.copy(alpha = 0.4f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = gradientColors
                )
            )
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (section) {
                    AboutSection.FAQ -> Icons.AutoMirrored.Filled.HelpOutline
                    AboutSection.ABOUT -> Icons.Default.Info
                    AboutSection.TERMS -> Icons.Default.Description
                    AboutSection.RATE -> Icons.Default.StarRate
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (section) {
                    AboutSection.FAQ -> "Câu hỏi thường gặp"
                    AboutSection.ABOUT -> "Giới thiệu Secure Scan"
                    AboutSection.TERMS -> "Điều khoản và thỏa thuận sử dụng"
                    AboutSection.RATE -> "Đánh giá ứng dụng"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ExpandableAboutSection(
    title: String,
    icon: ImageVector,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section header - clickable to expand/collapse
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = baseBlue3,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationState)
                    )
                }

                // Animated expanding content
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    Column {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun FAQContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        FAQItem(
            question = "SNews là gì?",
            answer = "SNews là ứng dụng tin tức thông minh giúp bạn cập nhật tin tức mới nhất từ nhiều nguồn khác nhau. Ứng dụng cung cấp các tính năng như đọc tin tức, lưu bài viết yêu thích, và nhận thông báo về tin tức mới."
        )

        FAQItem(
            question = "Làm thế nào để đọc tin tức?",
            answer = "Để đọc tin tức, bạn chỉ cần mở ứng dụng và chọn chủ đề tin tức bạn quan tâm. Ứng dụng sẽ hiển thị danh sách các bài viết mới nhất. Bạn có thể vuốt để xem thêm bài viết hoặc nhấn vào bài viết để đọc chi tiết."
        )

        FAQItem(
            question = "Làm thế nào để lưu bài viết yêu thích?",
            answer = "Để lưu bài viết yêu thích, bạn chỉ cần nhấn vào biểu tượng bookmark ở góc phải của bài viết. Bài viết sẽ được lưu vào mục 'Đã lưu' và bạn có thể xem lại bất cứ lúc nào."
        )

        FAQItem(
            question = "Làm thế nào để nhận thông báo tin tức mới?",
            answer = "Bạn có thể bật thông báo trong phần Cài đặt > Thông báo. Tại đây, bạn có thể chọn các chủ đề tin tức mà bạn muốn nhận thông báo."
        )

        FAQItem(
            question = "Tôi có thể tùy chỉnh giao diện ứng dụng không?",
            answer = "Có, bạn có thể tùy chỉnh giao diện ứng dụng trong phần Cài đặt > Giao diện. Tại đây, bạn có thể thay đổi chế độ tối/sáng, kích thước chữ, và các tùy chọn hiển thị khác."
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun AboutContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "SNews - Tin tức thông minh",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SNews là ứng dụng tin tức thông minh được phát triển với mục tiêu mang đến trải nghiệm đọc tin tức tốt nhất cho người dùng. Chúng tôi tập trung vào việc cung cấp nội dung chất lượng và giao diện thân thiện với người dùng.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tầm nhìn của chúng tôi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Chúng tôi tin rằng mọi người đều xứng đáng được tiếp cận thông tin một cách nhanh chóng và chính xác. SNews ra đời với sứ mệnh cung cấp nền tảng tin tức toàn diện, dễ sử dụng và đáng tin cậy cho tất cả người dùng.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tính năng nổi bật",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(start = 8.dp)) {
            FeatureItem("Đọc tin tức từ nhiều nguồn")
            FeatureItem("Lưu bài viết yêu thích")
            FeatureItem("Nhận thông báo tin tức mới")
            FeatureItem("Tùy chỉnh giao diện")
            FeatureItem("Tìm kiếm tin tức thông minh")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liên hệ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Email: support@snews.com\nWebsite: www.snews.com\nĐiện thoại: +84 123 456 789",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            color = baseBlue3,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun TermsContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Điều khoản sử dụng",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bằng việc tải xuống, cài đặt hoặc sử dụng ứng dụng SNews, bạn đồng ý tuân thủ các điều khoản và điều kiện sau đây. Nếu bạn không đồng ý với bất kỳ điều khoản nào, vui lòng không sử dụng ứng dụng của chúng tôi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "1. Cấp phép sử dụng",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "SNews cấp cho bạn giấy phép sử dụng không độc quyền, không thể chuyển nhượng để sử dụng ứng dụng trên thiết bị của bạn. Bạn không được phép sao chép, sửa đổi, phân phối, bán, cho thuê, cho mượn hoặc thực hiện kỹ thuật đảo ngược đối với ứng dụng.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "2. Quyền riêng tư",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Việc sử dụng ứng dụng của bạn tuân theo Chính sách Quyền riêng tư của chúng tôi. Chúng tôi cam kết bảo vệ thông tin cá nhân của bạn và chỉ sử dụng thông tin đó để cải thiện trải nghiệm người dùng và cung cấp dịch vụ tốt hơn.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "3. Nội dung",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "SNews cung cấp nội dung từ nhiều nguồn khác nhau. Chúng tôi không chịu trách nhiệm về nội dung được cung cấp bởi các nguồn bên thứ ba. Người dùng nên tự đánh giá tính chính xác và độ tin cậy của thông tin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "4. Thay đổi điều khoản",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Chúng tôi có thể cập nhật Điều khoản Sử dụng này theo thời gian. Vì vậy, bạn nên xem lại các trang này định kỳ để biết bất kỳ thay đổi nào. Chúng tôi sẽ thông báo cho bạn về bất kỳ thay đổi nào bằng cách đăng Điều khoản Sử dụng mới trên trang này.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cập nhật lần cuối: 01/05/2024",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RateAppContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đánh giá trải nghiệm của bạn",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Phản hồi của bạn giúp chúng tôi cải thiện SNews và phục vụ bạn tốt hơn.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Rating stars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.StarRate,
                    contentDescription = "Sao đánh giá",
                    tint = baseBlue3,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .clickable { /* TODO: Implement rating functionality */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Chia sẻ ý kiến của bạn trên cửa hàng ứng dụng",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(baseBlue3)
                .clickable { /* TODO: Implement store rating */ }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Đánh giá trên Google Play",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cảm ơn bạn đã sử dụng SNews!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

//add review
@Preview(showBackground = true)
@Composable
fun AboutAppScreenPreview() {
    val navController = NavController(context = LocalContext.current)
    AboutAppScreen(navController = navController, section = AboutSection.ABOUT)
}