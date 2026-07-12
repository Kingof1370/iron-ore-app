package ir.alibahmani.ironorecalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.domain.model.MixEntry
import ir.alibahmani.ironorecalc.ui.components.IronOreTopBar
import ir.alibahmani.ironorecalc.ui.viewmodel.MixViewModel

@Composable
fun MixWeighingScreen(
    navController: NavController,
    viewModel: MixViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("ورود داده", "تاریخچه", "خلاصه میکس")

    Scaffold(
        topBar = {
            IronOreTopBar(title = "توزین و میکس", onNavigateBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                        text = { Text(t, fontSize = 12.sp) })
                }
            }
            when (selectedTab) {
                0 -> MixEntryForm(viewModel)
                1 -> MixHistory(viewModel)
                2 -> MixSummaryTab(viewModel)
            }
        }
    }
}

// ── Tab 1: ورود داده ─────────────────────────────────────────────────────────
@Composable
private fun MixEntryForm(viewModel: MixViewModel) {
    var date        by remember { mutableStateOf("") }
    var productCode by remember { mutableStateOf("ریز دانه") }
    var weight      by remember { mutableStateOf("") }
    var fe          by remember { mutableStateOf("") }
    var feo         by remember { mutableStateOf("") }
    var saved       by remember { mutableStateOf(false) }

    val wt  = weight.toDoubleOrNull() ?: 0.0
    val fep = fe.toDoubleOrNull() ?: 0.0
    val metalContent = wt * fep

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionCard(title = "مشخصات ورودی") {
            // تاریخ
            OutlinedTextField(
                value = date, onValueChange = { date = it; saved = false },
                label = { Text("تاریخ  (مثال: ۱۴۰۴.۰۷.۱۲)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, null) }
            )
            // نوع محصول
            Text("نوع محصول", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ریز دانه", "درشت دانه").forEach { code ->
                    FilterChip(
                        selected = productCode == code,
                        onClick = { productCode = code; saved = false },
                        label = { Text(code, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // وزن
            OutlinedTextField(
                value = weight, onValueChange = { weight = it; saved = false },
                label = { Text("وزن (تن) *") },
                modifier = Modifier.fillMaxWidth()
            )
            // عیارها
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fe, onValueChange = { fe = it; saved = false },
                    label = { Text("Fe%  *") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = feo, onValueChange = { feo = it; saved = false },
                    label = { Text("FeO%") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // نتیجه محاسبه آنی
        if (wt > 0 && fep > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1).copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("محاسبه فلز محتوا", fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "فلز محتوا = وزن × Fe% = %.3f × %.4f = %.4f تن".format(wt, fep, metalContent),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Button(
            onClick = {
                if (date.isNotBlank() && wt > 0 && fep > 0) {
                    viewModel.save(
                        MixEntry(
                            date = date, productCode = productCode,
                            weightTon = wt, fePercent = fep,
                            feoPercent = feo.toDoubleOrNull() ?: 0.0
                        )
                    )
                    saved = true
                    weight = ""; fe = ""; feo = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = date.isNotBlank() && wt > 0 && fep > 0
        ) {
            Icon(Icons.Default.Save, null)
            Spacer(Modifier.width(6.dp))
            Text("ذخیره ردیف")
        }

        if (saved) {
            Text("✓ ذخیره شد", color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

// ── Tab 2: تاریخچه ───────────────────────────────────────────────────────────
@Composable
private fun MixHistory(viewModel: MixViewModel) {
    val entries by viewModel.entries.collectAsState()

    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("هیچ ردیفی ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                listOf("تاریخ", "نوع", "وزن (تن)", "Fe%", "FeO%", "فلز محتوا").forEachIndexed { i, h ->
                    Text(h, Modifier.weight(if (i == 0) 1.5f else 1f),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center)
                }
                Spacer(Modifier.width(28.dp))
            }
        }
        items(entries, key = { it.id }) { e ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(e.date,            Modifier.weight(1.5f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text(e.productCode,     Modifier.weight(1f),   fontSize = 10.sp, textAlign = TextAlign.Center,
                    color = if (e.productCode == "O10A") Color(0xFF1565C0) else Color(0xFF6A1B9A))
                Text("%.2f".format(e.weightTon),    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(e.fePercent),    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(e.feoPercent),   Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(e.metalContent), Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                IconButton(onClick = { viewModel.delete(e.id) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp))
                }
            }
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

// ── Tab 3: خلاصه میکس ───────────────────────────────────────────────────────
@Composable
private fun MixSummaryTab(viewModel: MixViewModel) {
    val sum by viewModel.summary.collectAsState()
    val entries by viewModel.entries.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // کارت O10A ریزدانه
        ProductSummaryCard(
            title = "O10A  —  ریزدانه",
            color = Color(0xFF1565C0),
            weightTon = sum.totalFineWeightTon,
            metalContent = sum.totalFineMetalContent,
            entryCount = entries.count { it.productCode == "O10A" }
        )
        // کارت O30A درشت‌دانه
        ProductSummaryCard(
            title = "O30A  —  درشت‌دانه",
            color = Color(0xFF6A1B9A),
            weightTon = sum.totalCoarseWeightTon,
            metalContent = sum.totalCoarseMetalContent,
            entryCount = entries.count { it.productCode == "O30A" }
        )

        // کارت عیار میکس شده
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.1f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("خلاصه میکس شده", fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20), fontSize = 15.sp)
                HorizontalDivider()
                SummaryRow("مجموع وزن توزین شده", "%.3f  تن".format(sum.totalWeight))
                SummaryRow("جمع فلز محتوا ریز",   "%.4f".format(sum.totalFineMetalContent))
                SummaryRow("جمع فلز محتوا درشت",  "%.4f".format(sum.totalCoarseMetalContent))
                SummaryRow("مجموع فلز محتوا",      "%.4f".format(sum.totalMetalContent))
                Divider()
                // فرمول: عیار میکس شده = Σ(وزن×Fe%) / Σوزن
                SummaryRow(
                    "عیار میکس شده  =  Σ(وزن×Fe%) / Σوزن",
                    "%.5f".format(sum.mixedGrade),
                    highlight = true
                )
            }
        }

        // راهنمای فرمول‌ها
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("فرمول‌ها (مطابق اکسل توزین میکس)", fontWeight = FontWeight.Bold,
                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("• فلز محتوا = وزن (تن) × Fe%", fontSize = 10.sp)
                Text("• عیار میکس شده = Σ(وزن×Fe%) / Σوزن", fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ProductSummaryCard(
    title: String, color: Color,
    weightTon: Double, metalContent: Double, entryCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
            HorizontalDivider()
            SummaryRow("تعداد ردیف‌های ثبت‌شده", "$entryCount  ردیف")
            SummaryRow("مجموع وزن توزین شده", "%.3f  تن".format(weightTon))
            SummaryRow("مجموع فلز محتوا",
                "%.4f".format(metalContent), highlight = true)
            if (weightTon > 0)
                SummaryRow("عیار میانگین = فلزمحتوا/وزن",
                    "%.5f".format(metalContent / weightTon))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlight: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(code, fontSize = 11.sp,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            content()
        }
    }
}
