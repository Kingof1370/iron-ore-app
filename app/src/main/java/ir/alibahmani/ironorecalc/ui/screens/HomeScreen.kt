package ir.alibahmani.ironorecalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.ui.navigation.Screen
import ir.alibahmani.ironorecalc.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val totalCount by viewModel.totalCount.collectAsState(0)
    val todayCount  by viewModel.todayCount.collectAsState(0)

    Scaffold { paddingValues ->
        // Use a single LazyVerticalGrid for the whole screen so there is no
        // nested-scroll conflict.  Header rows use a full-width span.
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Hero Banner (full-width) ─────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B3A5A))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 32.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFFE8722A),
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    "محاسبه‌گر سنگ آهن صنعتی",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "جداسازی مغناطیسی خشک دو مرحله‌ای",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                "مجموع محاسبات",
                                totalCount.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                "امروز",
                                todayCount.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Dashboard Cards (2-column grid) ─────────────────────
            item {
                DashboardCard(
                    title = "محاسبه جدید",
                    description = "شروع محاسبه جداسازی مغناطیسی",
                    icon = Icons.Default.Calculate,
                    color = Color(0xFFE8722A),
                    onClick = { navController.navigate(Screen.Calculation.route) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "تاریخچه",
                    description = "مشاهده محاسبات قبلی",
                    icon = Icons.Default.History,
                    color = Color(0xFF2A6B9A),
                    onClick = { navController.navigate(Screen.History.route) },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "دیاگرام فرآیند",
                    description = "شماتیک خط فرآوری",
                    icon = Icons.Default.AccountTree,
                    color = Color(0xFF27AE60),
                    onClick = { navController.navigate(Screen.Diagram.route) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "گزارش‌ها",
                    description = "تولید و صدور گزارش PDF",
                    icon = Icons.Default.PictureAsPdf,
                    color = Color(0xFF8E44AD),
                    onClick = { navController.navigate(Screen.Reports.route) },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            // ── دو بخش جدید ──────────────────────────────────────────
            item {
                DashboardCard(
                    title = "توزین و میکس",
                    description = "ثبت توزین O10A/O30A و عیار میکس شده",
                    icon = Icons.Default.Scale,
                    color = Color(0xFF1565C0),
                    onClick = { navController.navigate(Screen.MixWeighing.route) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "گزارش پرعیارسازی",
                    description = "گزارش روزانه LIMS، بازیابی و تولید",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFF1B5E20),
                    onClick = { navController.navigate(Screen.BenefReport.route) },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "تنظیمات",
                    description = "تنظیمات برنامه",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF7F8C8D),
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            item {
                DashboardCard(
                    title = "درباره",
                    description = "اطلاعات توسعه‌دهنده",
                    icon = Icons.Default.Info,
                    color = Color(0xFF16A085),
                    onClick = { navController.navigate(Screen.About.route) },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // ── Footer (full-width) ──────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "توسعه‌یافته توسط علی بهمنی | ۰۹۹۱۵۴۲۰۵۵۸",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE8722A)
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
