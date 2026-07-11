package ir.alibahmani.ironorecalc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.ui.components.IronOreTopBar
import ir.alibahmani.ironorecalc.ui.components.IndustrialCard

@Composable
fun SettingsScreen(navController: NavController) {
    var selectedTheme by remember { mutableStateOf("system") }
    var precision by remember { mutableStateOf(2f) }

    Scaffold(
        topBar = {
            IronOreTopBar(
                title = "تنظیمات",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Theme Section
            IndustrialCard {
                Text("ظاهر برنامه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("system" to "سیستم", "light" to "روشن", "dark" to "تاریک").forEach { (key, label) ->
                        FilterChip(
                            selected = selectedTheme == key,
                            onClick = { selectedTheme = key },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Precision
            IndustrialCard {
                Text("دقت اعشار نتایج", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("تعداد رقم اعشار: ${precision.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = precision,
                    onValueChange = { precision = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // App Info
            IndustrialCard {
                Text("اطلاعات برنامه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsInfoRow(Icons.Default.AppRegistration, "نسخه برنامه", "1.0.0")
                SettingsInfoRow(Icons.Default.Person, "توسعه‌دهنده", "علی بهمنی")
                SettingsInfoRow(Icons.Default.Phone, "تلفن تماس", "۰۹۹۱۵۴۲۰۵۵۸")
                SettingsInfoRow(Icons.Default.WifiOff, "اتصال اینترنت", "آفلاین — بدون اتصال به سرور")
                SettingsInfoRow(Icons.Default.Security, "حریم خصوصی", "بدون تبلیغات، بدون ردیابی")
            }

            // Technical Requirements
            IndustrialCard {
                Text("مشخصات فنی", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsInfoRow(Icons.Default.Android, "حداقل Android", "8.0 (API 26)")
                SettingsInfoRow(Icons.Default.Memory, "معماری", "MVVM + Clean Architecture")
                SettingsInfoRow(Icons.Default.Storage, "پایگاه داده", "Room Database (آفلاین)")
                SettingsInfoRow(Icons.Default.Calculate, "موتور محاسبات", "موازنه جرم دو‌محصولی استاندارد")
            }
        }
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
