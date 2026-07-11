package ir.alibahmani.ironorecalc.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.ui.components.*
import ir.alibahmani.ironorecalc.ui.viewmodel.CalculationViewModel
import ir.alibahmani.ironorecalc.domain.model.CalculationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationScreen(
    navController: NavController,
    viewModel: CalculationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var projectName by remember { mutableStateOf("") }

    // Save success snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("پروژه با موفقیت ذخیره شد")
            viewModel.clearSaveStatus()
        }
    }

    Scaffold(
        topBar = {
            IronOreTopBar(
                title = "محاسبه جداسازی مغناطیسی",
                onNavigateBack = { navController.popBackStack() },
                actions = {
                    if (uiState.result != null) {
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(Icons.Default.Save, "ذخیره")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Stage 1 ─────────────────────────────────────────────
            IndustrialCard {
                SectionHeader("مرحله اول جداسازی", "ورودی‌های خوراک و درام اول", Icons.Default.Tune)
                NumberInputField("نرخ خوراک ورودی", uiState.form["feedRate"] ?: "", { viewModel.setField("feedRate", it) }, unit = "t/h", required = true)
                NumberInputField("عیار کل آهن خوراک", uiState.form["feedGrade"] ?: "", { viewModel.setField("feedGrade", it) }, unit = "٪", required = true)
                NumberInputField("عیار Fe محصول میانی (خروجی درام اول)", uiState.form["midGrade"] ?: "", { viewModel.setField("midGrade", it) }, unit = "٪", required = true)
                NumberInputField("عیار Fe باطله مرحله اول", uiState.form["tailGrade1"] ?: "", { viewModel.setField("tailGrade1", it) }, unit = "٪", required = true)
                NumberInputField("شدت میدان مغناطیسی درام اول", uiState.form["h1"] ?: "", { viewModel.setField("h1", it) }, unit = "گاوس", required = true)
                NumberInputField("دانه‌بندی محصول آسیاب (D80)", uiState.form["d80"] ?: "", { viewModel.setField("d80", it) }, unit = "میکرون", required = true)
                NumberInputField("رطوبت خوراک", uiState.form["moisture"] ?: "", { viewModel.setField("moisture", it) }, unit = "٪", required = true)
            }

            // ── Stage 2 ─────────────────────────────────────────────
            IndustrialCard {
                SectionHeader("مرحله دوم جداسازی", "ورودی‌های کنسانتره و باطله نهایی", Icons.Default.FilterAlt)
                NumberInputField("عیار Fe کنسانتره نهایی", uiState.form["finalConcGrade"] ?: "", { viewModel.setField("finalConcGrade", it) }, unit = "٪", required = true)
                NumberInputField("عیار Fe باطله نهایی", uiState.form["finalTailGrade"] ?: "", { viewModel.setField("finalTailGrade", it) }, unit = "٪", required = true)
                NumberInputField("شدت میدان مغناطیسی درام دوم", uiState.form["h2"] ?: "", { viewModel.setField("h2", it) }, unit = "گاوس", required = true)
            }

            // ── Advanced ─────────────────────────────────────────────
            IndustrialCard {
                CollapsibleSection(
                    title = "پارامترهای علمی تکمیلی",
                    subtitle = "اختیاری — کانی‌شناسی و شاخص‌های مغناطیسی"
                ) {
                    NumberInputField("درصد FeO خوراک (تیتراسیون)", uiState.form["feO"] ?: "", { viewModel.setField("feO", it) }, unit = "٪")
                    NumberInputField("درصد Fe2O3 کل خوراک", uiState.form["fe2o3"] ?: "", { viewModel.setField("fe2o3", it) }, unit = "٪")
                    NumberInputField("شاخص آزادشدگی (آزمایشگاهی)", uiState.form["liberation"] ?: "", { viewModel.setField("liberation", it) }, unit = "٪")
                    NumberInputField("چگالی کانی باارزش", uiState.form["rhoVal"] ?: "", { viewModel.setField("rhoVal", it) }, unit = "g/cm³")
                    NumberInputField("چگالی گانگ", uiState.form["rhoGang"] ?: "", { viewModel.setField("rhoGang", it) }, unit = "g/cm³")
                    NumberInputField("چگالی سیال جداسازی", uiState.form["rhoFluid"] ?: "", { viewModel.setField("rhoFluid", it) }, unit = "g/cm³")
                    NumberInputField("پذیرفتاری مغناطیسی کانی باارزش (χ)", uiState.form["chiVal"] ?: "", { viewModel.setField("chiVal", it) })
                    NumberInputField("پذیرفتاری مغناطیسی گانگ (χ)", uiState.form["chiGang"] ?: "", { viewModel.setField("chiGang", it) })
                }
            }

            // ── Equipment ─────────────────────────────────────────────
            IndustrialCard {
                CollapsibleSection(
                    title = "پارامترهای تجهیزات (دور درام و سرعت نوار)",
                    subtitle = "اختیاری — سرعت درام، سرعت نوار و کفایت میدان"
                ) {
                    NumberInputField("قطر درام مرحله اول", uiState.form["drumDiameter1"] ?: "", { viewModel.setField("drumDiameter1", it) }, unit = "متر")
                    NumberInputField("عرض نوار مرحله اول", uiState.form["beltWidth1"] ?: "", { viewModel.setField("beltWidth1", it) }, unit = "متر")
                    NumberInputField("ضخامت بستر خوراک مرحله اول", uiState.form["bedThickness1"] ?: "", { viewModel.setField("bedThickness1", it) }, unit = "mm")
                    NumberInputField("قطر درام مرحله دوم", uiState.form["drumDiameter2"] ?: "", { viewModel.setField("drumDiameter2", it) }, unit = "متر")
                    NumberInputField("عرض نوار مرحله دوم", uiState.form["beltWidth2"] ?: "", { viewModel.setField("beltWidth2", it) }, unit = "متر")
                    NumberInputField("ضخامت بستر خوراک مرحله دوم", uiState.form["bedThickness2"] ?: "", { viewModel.setField("bedThickness2", it) }, unit = "mm")
                    NumberInputField("چگالی توده خوراک (Bulk Density)", uiState.form["bulkDensity"] ?: "", { viewModel.setField("bulkDensity", it) }, unit = "t/m³")
                }
            }

            // ── Errors ───────────────────────────────────────────────
            AnimatedVisibility(visible = uiState.errors.isNotEmpty()) {
                AlertMessageCard(
                    messages = uiState.errors,
                    icon = Icons.Default.ErrorOutline,
                    title = "خطا در ورودی‌ها",
                    containerColor = MaterialTheme.colorScheme.error
                )
            }

            // ── Action Buttons ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.calculate() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("محاسبه کن", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
            }

            // ── Results ───────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState.result != null,
                enter = fadeIn() + expandVertically()
            ) {
                uiState.result?.let { result ->
                    ResultsSection(result = result, onSave = { showSaveDialog = true })
                }
            }

            // Footer note
            Text(
                "محاسبات بر اساس موازنه جرم دو‌محصولی استاندارد. برخی شاخص‌ها صرفاً برآورد تقریبی هستند.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    // ── Save Dialog ────────────────────────────────────────────────────
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("ذخیره پروژه") },
            text = {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("نام پروژه") },
                    placeholder = { Text("مثلاً: معدن سنگان - مرحله ۱") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (projectName.isNotBlank()) {
                        viewModel.saveProject(projectName)
                        showSaveDialog = false
                        projectName = ""
                    }
                }) { Text("ذخیره") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("لغو") }
            }
        )
    }
}

@Composable
private fun ResultsSection(result: CalculationResult, onSave: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // ── Mass Balance Card ──────────────────────────────────────
        IndustrialCard {
            SectionHeader("موازنه جرم", icon = Icons.Default.BarChart, toneColor = MaterialTheme.colorScheme.primary)
            val mb = result.massBalance
            ResultRow("ظرفیت محصول میانی (F2)", "%.2f تن/ساعت".format(mb.f2))
            ResultRow("باطله مرحله اول (T1)", "%.2f تن/ساعت".format(mb.t1))
            ResultRow("بازیابی مرحله اول (R1)", "%.2f٪".format(mb.r1))
            ResultRow("ظرفیت کنسانتره نهایی (F3)", "%.2f تن/ساعت".format(mb.f3))
            ResultRow("باطله نهایی (T2)", "%.2f تن/ساعت".format(mb.t2))
            ResultRow("بازیابی مرحله دوم (R2)", "%.2f٪".format(mb.r2))
            ResultRow("بازیابی کلی آهن", "%.2f٪".format(mb.rTotal), emphasis = true)
            ResultRow("خطای موازنه آهن (قدر مطلق)", "%.2f٪".format(result.ironBalance.errorAbs))
        }

        // ── Process Indicators ────────────────────────────────────
        IndustrialCard {
            SectionHeader("شاخص‌های فرآوری", icon = Icons.Default.TrackChanges, toneColor = Color(0xFFE8722A))
            ResultRow("نوع کانی خوراک", result.mineral.label)
            ResultRow("نسبت غنی‌سازی", "%.2f".format(result.enrichmentRatio))
            ResultRow("بازده جرمی کلی", "%.2f٪".format(result.yieldPct))
            ResultRow("کارایی جداسازی Newton", "%.2f٪".format(result.newton), note = "با فرض گانگ = ۱۰۰ − آهن")
            ResultRow("شاخص گزینش‌پذیری (Gaudin–Schulz)", "%.2f".format(result.selectivityIndex))
            ResultRow("نسبت پذیرفتاری مغناطیسی (MSR)", result.msr?.let { "%.4f".format(it) } ?: "وارد نشده")
            ResultRow("شاخص محرک مغناطیسی نسبی", result.relativeMagneticDrivingIndex?.let { "%.0f".format(it) } ?: "وارد نشده")
            ResultRow("Concentration Criterion", result.concentrationCriterion?.let { "%.2f".format(it) } ?: "وارد نشده", note = "شاخص ثقلی — فقط جهت مرجع")
        }

        // ── Equipment Results ─────────────────────────────────────
        IndustrialCard {
            SectionHeader("سرعت درام‌ها و نوارها", icon = Icons.Default.Settings, toneColor = Color(0xFF2A6B9A))
            val ds1 = result.drumSpeed1
            ResultRow(
                "سرعت بحرانی درام اول",
                ds1?.let { "%.1f دور/دقیقه".format(it.criticalSpeedRpm) } ?: "قطر درام وارد نشده",
                note = ds1?.let { "بازه توصیه‌شده: %.1f تا %.1f دور/دقیقه".format(it.recommendedMinRpm, it.recommendedMaxRpm) }
            )
            val ds2 = result.drumSpeed2
            ResultRow(
                "سرعت بحرانی درام دوم",
                ds2?.let { "%.1f دور/دقیقه".format(it.criticalSpeedRpm) } ?: "قطر درام وارد نشده",
                note = ds2?.let { "بازه توصیه‌شده: %.1f تا %.1f دور/دقیقه".format(it.recommendedMinRpm, it.recommendedMaxRpm) }
            )
            ResultRow("سرعت لازم نوار مرحله اول", result.beltSpeed1?.let { "%.2f متر/دقیقه".format(it) } ?: "وارد نشده")
            ResultRow("سرعت لازم نوار مرحله دوم", result.beltSpeed2?.let { "%.2f متر/دقیقه".format(it) } ?: "وارد نشده")
            ResultRow("کفایت میدان درام اول", result.fieldAdequacy1.status,
                note = "بازه: ${result.fieldAdequacy1.recommendedMinGauss.toInt()}–${result.fieldAdequacy1.recommendedMaxGauss.toInt()} گاوس")
            ResultRow("کفایت میدان درام دوم", result.fieldAdequacy2.status,
                note = "بازه: ${result.fieldAdequacy2.recommendedMinGauss.toInt()}–${result.fieldAdequacy2.recommendedMaxGauss.toInt()} گاوس")
        }

        // ── Multi-Criteria ───────────────────────────────────────
        IndustrialCard {
            SectionHeader("تحلیل چندمعیاره عملکرد", icon = Icons.Default.PieChart, toneColor = Color(0xFF27AE60))
            val mc = result.multiCriteria
            ScoreBar(mc.overallScore, modifier = Modifier.padding(bottom = 8.dp))
            ResultRow("امتیاز کلی اطمینان", "%.1f / ۱۰۰".format(mc.overallScore), emphasis = true)
            ResultRow("مهم‌ترین عامل محدودکننده", mc.limitingFactor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            mc.breakdown.forEach { c ->
                ResultRow("${c.name} (وزن ${c.weight})", "%.0f/100".format(c.score))
            }
        }

        // ── Warnings & Suggestions ────────────────────────────────
        AlertMessageCard(
            messages = result.warnings,
            icon = Icons.Default.Warning,
            title = "هشدارها",
            containerColor = Color(0xFFE67E22)
        )
        AlertMessageCard(
            messages = result.suggestions,
            icon = Icons.Default.Lightbulb,
            title = "پیشنهادهای بهبود",
            containerColor = Color(0xFF3498DB)
        )

        // Save button at bottom of results
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ذخیره پروژه", fontWeight = FontWeight.Bold)
        }
    }
}
