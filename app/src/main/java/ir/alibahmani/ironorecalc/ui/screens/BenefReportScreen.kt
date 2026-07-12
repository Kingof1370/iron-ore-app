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
import ir.alibahmani.ironorecalc.domain.model.BenefDailyReport
import ir.alibahmani.ironorecalc.domain.model.BenefSample
import ir.alibahmani.ironorecalc.ui.components.IronOreTopBar
import ir.alibahmani.ironorecalc.ui.viewmodel.BenefViewModel

@Composable
fun BenefReportScreen(
    navController: NavController,
    viewModel: BenefViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("نمونه روزانه", "گزارش کلی", "تاریخچه", "آمار تجمعی")

    Scaffold(
        topBar = {
            IronOreTopBar(
                title = "گزارش پرعیارسازی",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                        text = { Text(t, fontSize = 11.sp) })
                }
            }
            when (selectedTab) {
                0 -> SampleEntryTab(viewModel)
                1 -> DailyReportTab(viewModel)
                2 -> SampleHistoryTab(viewModel)
                3 -> CumulativeStatsTab(viewModel)
            }
        }
    }
}

// ── Tab 1: ورود نمونه روزانه ─────────────────────────────────────────────────
@Composable
private fun SampleEntryTab(viewModel: BenefViewModel) {
    var date          by remember { mutableStateOf("") }
    var rowNum        by remember { mutableStateOf("") }
    var opHours       by remember { mutableStateOf("") }
    var sampleCode    by remember { mutableStateOf("R2") }
    var sampleType    by remember { mutableStateOf("میلیمتر10-0") }
    var drumNumbers   by remember { mutableStateOf("1و2و3و4") }
    var drumSpeed     by remember { mutableStateOf("") }
    var bladeAngle    by remember { mutableStateOf("") }
    var fieldStrength by remember { mutableStateOf("") }
    var truckCount    by remember { mutableStateOf("") }
    var feedWt  by remember { mutableStateOf("") }; var feedFe  by remember { mutableStateOf("") }; var feedFeo by remember { mutableStateOf("") }
    var concWt  by remember { mutableStateOf("") }; var concFe  by remember { mutableStateOf("") }; var concFeo by remember { mutableStateOf("") }
    var tailWt  by remember { mutableStateOf("") }; var tailFe  by remember { mutableStateOf("") }; var tailFeo by remember { mutableStateOf("") }
    var notes         by remember { mutableStateOf("") }
    var saved         by remember { mutableStateOf(false) }

    val fwt = feedWt.toDoubleOrNull() ?: 0.0
    val ffe = feedFe.toDoubleOrNull() ?: 0.0
    val cwt = concWt.toDoubleOrNull() ?: 0.0
    val cfe = concFe.toDoubleOrNull() ?: 0.0

    // محاسبه آنی فرمول‌ها
    val calcRec   = if (fwt > 0 && ffe > 0) (cwt * cfe) / (fwt * ffe) else 0.0
    val weightRec = if (fwt > 0) cwt / fwt else 0.0
    val metalCont = cwt * cfe

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── اطلاعات اولیه ────────────────────────────────────────────────────
        BSectionCard("اطلاعات اولیه نمونه") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it; saved = false },
                    label = { Text("تاریخ  (مثال: ۱۴۰۴/۰۶/۰۴)") },
                    modifier = Modifier.weight(1.5f), leadingIcon = { Icon(Icons.Default.DateRange, null) })
                OutlinedTextField(value = rowNum, onValueChange = { rowNum = it },
                    label = { Text("ردیف") }, modifier = Modifier.weight(0.7f))
            }
            OutlinedTextField(value = opHours, onValueChange = { opHours = it },
                label = { Text("مجموع ساعت کارکرد") }, modifier = Modifier.fillMaxWidth())
            // کد نمونه
            Text("کد نمونه", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("R1","R2","D1","D2").forEach { code ->
                    FilterChip(selected = sampleCode == code, onClick = { sampleCode = code },
                        label = { Text(code, fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                }
            }
            // نوع نمونه
            Text("نوع نمونه", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("میلیمتر10-0","میلیمتر30-10").forEach { t ->
                    FilterChip(selected = sampleType == t, onClick = { sampleType = t },
                        label = { Text(t, fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                }
            }
        }

        // ── مشخصات درام ──────────────────────────────────────────────────────
        BSectionCard("مشخصات درام") {
            OutlinedTextField(value = drumNumbers, onValueChange = { drumNumbers = it },
                label = { Text("شماره درام  (مثال: ۱و۲و۳و۴)") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = drumSpeed, onValueChange = { drumSpeed = it },
                    label = { Text("سرعت (دور/دقیقه)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = bladeAngle, onValueChange = { bladeAngle = it },
                    label = { Text("زاویه تیغه") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = fieldStrength, onValueChange = { fieldStrength = it },
                label = { Text("شدت میدان (گوس) — مثال: ۱۳۰۰و۹۰۰") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = truckCount, onValueChange = { truckCount = it },
                label = { Text("تعداد کامیون") }, modifier = Modifier.fillMaxWidth())
        }

        // ── عیارها ───────────────────────────────────────────────────────────
        BSectionCard("عیارها — خوراک (F)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = feedWt, onValueChange = { feedWt = it; saved = false },
                    label = { Text("وزن (تن) *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = feedFe, onValueChange = { feedFe = it; saved = false },
                    label = { Text("Fe% *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = feedFeo, onValueChange = { feedFeo = it },
                    label = { Text("FeO%") }, modifier = Modifier.weight(1f))
            }
        }
        BSectionCard("عیارها — محصول / کنسانتره (C)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = concWt, onValueChange = { concWt = it; saved = false },
                    label = { Text("وزن (تن) *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = concFe, onValueChange = { concFe = it; saved = false },
                    label = { Text("Fe% *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = concFeo, onValueChange = { concFeo = it },
                    label = { Text("FeO%") }, modifier = Modifier.weight(1f))
            }
        }
        BSectionCard("عیارها — باطله (T)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = tailWt, onValueChange = { tailWt = it },
                    label = { Text("وزن (تن)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = tailFe, onValueChange = { tailFe = it },
                    label = { Text("Fe%") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = tailFeo, onValueChange = { tailFeo = it },
                    label = { Text("FeO%") }, modifier = Modifier.weight(1f))
            }
        }

        // ── نتیجه محاسبه آنی ─────────────────────────────────────────────────
        if (fwt > 0 && ffe > 0 && cwt > 0 && cfe > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("نتایج محاسباتی (مطابق اکسل)",
                        fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    Divider()
                    // فرمول بازیابی محاسباتی = (وزن محصول × Fe محصول) / (وزن خوراک × Fe خوراک)
                    BCalcRow("بازیابی محاسباتی (V)",
                        "(%.3f × %.4f) / (%.3f × %.4f) = %.4f".format(cwt, cfe, fwt, ffe, calcRec))
                    // فرمول بازیابی وزنی = وزن محصول / وزن خوراک
                    BCalcRow("بازیابی وزنی (W)",
                        "%.3f / %.3f = %.4f".format(cwt, fwt, weightRec))
                    // فرمول محتوی فلز = وزن محصول × Fe محصول
                    BCalcRow("محتوی فلز محصول (X)",
                        "%.3f × %.4f = %.4f  تن".format(cwt, cfe, metalCont))
                }
            }
        }

        OutlinedTextField(value = notes, onValueChange = { notes = it },
            label = { Text("توضیحات") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

        Button(
            onClick = {
                if (date.isNotBlank() && fwt > 0 && ffe > 0 && cwt >= 0 && cfe >= 0) {
                    viewModel.saveSample(BenefSample(
                        date = date,
                        rowNum = rowNum.toIntOrNull() ?: 1,
                        operatingHours = opHours.toDoubleOrNull() ?: 0.0,
                        sampleCode = sampleCode, sampleType = sampleType,
                        drumNumbers = drumNumbers,
                        drumSpeed = drumSpeed.toIntOrNull() ?: 0,
                        bladeAngle = bladeAngle, fieldStrength = fieldStrength,
                        truckCount = truckCount.toIntOrNull() ?: 0,
                        feedWeight = fwt, feedFe = ffe,
                        feedFeo = feedFeo.toDoubleOrNull() ?: 0.0,
                        concWeight = cwt, concFe = cfe,
                        concFeo = concFeo.toDoubleOrNull() ?: 0.0,
                        tailWeight = tailWt.toDoubleOrNull() ?: 0.0,
                        tailFe = tailFe.toDoubleOrNull() ?: 0.0,
                        tailFeo = tailFeo.toDoubleOrNull() ?: 0.0,
                        notes = notes
                    ))
                    saved = true
                    rowNum = ""; opHours = ""; feedWt = ""; feedFe = ""; feedFeo = ""
                    concWt = ""; concFe = ""; concFeo = ""
                    tailWt = ""; tailFe = ""; tailFeo = ""; notes = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = date.isNotBlank() && fwt > 0 && ffe > 0
        ) {
            Icon(Icons.Default.Save, null); Spacer(Modifier.width(6.dp))
            Text("ذخیره نمونه")
        }
        if (saved) Text("✓ ذخیره شد", color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

// ── Tab 2: گزارش کلی روزانه ──────────────────────────────────────────────────
@Composable
private fun DailyReportTab(viewModel: BenefViewModel) {
    var date        by remember { mutableStateOf("") }
    var lims1 by remember { mutableStateOf(true) }
    var lims2 by remember { mutableStateOf(true) }
    var lims3 by remember { mutableStateOf(true) }
    var lims4 by remember { mutableStateOf(true) }
    var feedType    by remember { mutableStateOf("R2") }
    var downtime    by remember { mutableStateOf("") }
    var runtime     by remember { mutableStateOf("") }
    var prodTon     by remember { mutableStateOf("") }
    var weighTon    by remember { mutableStateOf("") }
    var cumProd     by remember { mutableStateOf("") }
    var cargoType   by remember { mutableStateOf("O10A") }
    var cargoFe     by remember { mutableStateOf("") }
    var cargoWt     by remember { mutableStateOf("") }
    var cargoTrucks by remember { mutableStateOf("") }
    var transWt     by remember { mutableStateOf("") }
    var transFeo    by remember { mutableStateOf("") }
    var diesel      by remember { mutableStateOf("") }
    // Personnel
    var pOffice by remember { mutableStateOf("") }
    var pSafety by remember { mutableStateOf("") }
    var pExpert by remember { mutableStateOf("") }
    var pElec   by remember { mutableStateOf("") }
    var pLabor  by remember { mutableStateOf("") }
    var pMech   by remember { mutableStateOf("") }
    var pSvc    by remember { mutableStateOf("") }
    var pLoader by remember { mutableStateOf("") }
    var pTruck  by remember { mutableStateOf("") }
    var pKara   by remember { mutableStateOf("") }
    var pWare   by remember { mutableStateOf("") }
    var pSupply by remember { mutableStateOf("") }
    var pBenef  by remember { mutableStateOf("") }
    var pCrush  by remember { mutableStateOf("") }
    var pTech   by remember { mutableStateOf("") }
    var notes   by remember { mutableStateOf("") }
    var saved   by remember { mutableStateOf(false) }

    fun int(s: String) = s.toIntOrNull() ?: 0
    fun dbl(s: String) = s.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BSectionCard("اطلاعات روز") {
            OutlinedTextField(value = date, onValueChange = { date = it; saved = false },
                label = { Text("تاریخ  (مثال: ۱۴۰۴/۰۶/۰۴)") }, modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, null) })
            Text("نوع بار فید شده", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("R1","R2","D1","D2").forEach { code ->
                    FilterChip(selected = feedType == code, onClick = { feedType = code },
                        label = { Text(code) }, modifier = Modifier.weight(1f))
                }
            }
        }

        BSectionCard("وضعیت سپراتورها (ON/OFF)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()) {
                LimsToggle("LIMS 1", lims1, modifier = Modifier.weight(1f)) { lims1 = it }
                LimsToggle("LIMS 2", lims2, modifier = Modifier.weight(1f)) { lims2 = it }
                LimsToggle("LIMS 3", lims3, modifier = Modifier.weight(1f)) { lims3 = it }
                LimsToggle("LIMS 4", lims4, modifier = Modifier.weight(1f)) { lims4 = it }
            }
        }

        BSectionCard("زمان شیفت") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = downtime, onValueChange = { downtime = it },
                    label = { Text("توقفات (ساعت)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = runtime, onValueChange = { runtime = it },
                    label = { Text("کارکرد (ساعت)") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("تولید و توزین") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = prodTon, onValueChange = { prodTon = it },
                    label = { Text("تولید روزانه (تن)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = weighTon, onValueChange = { weighTon = it },
                    label = { Text("توزین روزانه (تن)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = cumProd, onValueChange = { cumProd = it },
                label = { Text("تولید از ابتدای ماه (تن)") }, modifier = Modifier.fillMaxWidth())
        }

        BSectionCard("توزین بار") {
            Text("نوع بار توزین شده", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("O10A","O30A").forEach { code ->
                    FilterChip(selected = cargoType == code, onClick = { cargoType = code },
                        label = { Text(code) }, modifier = Modifier.weight(1f))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = cargoFe, onValueChange = { cargoFe = it },
                    label = { Text("عیار بار Fe%") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = cargoWt, onValueChange = { cargoWt = it },
                    label = { Text("وزن (تن)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = cargoTrucks, onValueChange = { cargoTrucks = it },
                    label = { Text("تعداد کامیون") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = transWt, onValueChange = { transWt = it },
                    label = { Text("وزن منتقل شده (تن)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = transFeo, onValueChange = { transFeo = it },
                    label = { Text("FeO% منتقل شده") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("پرسنل — دفتر") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pOffice, onValueChange = { pOffice = it },
                    label = { Text("سرپرست") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pSafety, onValueChange = { pSafety = it },
                    label = { Text("کارشناس ایمنی") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pExpert, onValueChange = { pExpert = it },
                    label = { Text("کارشناس") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("پرسنل — خدمات فنی") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pElec, onValueChange = { pElec = it },
                    label = { Text("برق‌کار") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pLabor, onValueChange = { pLabor = it },
                    label = { Text("کارگر ساده") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("پرسنل — ماشین‌آلات") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pMech, onValueChange = { pMech = it },
                    label = { Text("مکانیک") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pSvc, onValueChange = { pSvc = it },
                    label = { Text("سرویس‌کار") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pLoader, onValueChange = { pLoader = it },
                    label = { Text("راننده لودر") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pTruck, onValueChange = { pTruck = it },
                    label = { Text("راننده کامیون") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pKara, onValueChange = { pKara = it },
                    label = { Text("راننده کارا") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("پرسنل — واحد پرعیارسازی") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pBenef, onValueChange = { pBenef = it },
                    label = { Text("سرپرست") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pCrush, onValueChange = { pCrush = it },
                    label = { Text("کارگر سنگ‌شکن") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = pTech, onValueChange = { pTech = it },
                    label = { Text("کارگر فنی") }, modifier = Modifier.weight(1f))
            }
        }

        BSectionCard("سایر") {
            OutlinedTextField(value = diesel, onValueChange = { diesel = it },
                label = { Text("مصرف گازوییل روزانه") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes, onValueChange = { notes = it },
                label = { Text("توضیحات") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        }

        Button(
            onClick = {
                if (date.isNotBlank()) {
                    viewModel.saveDaily(BenefDailyReport(
                        date = date,
                        lims1 = lims1, lims2 = lims2, lims3 = lims3, lims4 = lims4,
                        feedType = feedType,
                        shiftDowntimeHr = dbl(downtime), shiftRuntimeHr = dbl(runtime),
                        dailyProductionTon = dbl(prodTon), dailyWeighingTon = dbl(weighTon),
                        cumulativeProdTon = dbl(cumProd),
                        weighedCargoType = cargoType, weighedCargoFe = dbl(cargoFe),
                        weighedCargoWeightTon = dbl(cargoWt), weighedCargoTruckCount = int(cargoTrucks),
                        transferredWeightTon = dbl(transWt), transferredFeo = dbl(transFeo),
                        personnelOffice = int(pOffice), personnelSafetyExpert = int(pSafety),
                        personnelExpert = int(pExpert), personnelTechnicalElec = int(pElec),
                        personnelTechnicalLabor = int(pLabor), personnelMechanics = int(pMech),
                        personnelServiceman = int(pSvc), personnelLoaderDriver = int(pLoader),
                        personnelTruckDriver = int(pTruck), personnelKaraDriver = int(pKara),
                        personnelWarehouse = int(pWare), personnelSupply = int(pSupply),
                        personnelBenefHead = int(pBenef), personnelCrusherWorker = int(pCrush),
                        personnelTechnicalWorker = int(pTech),
                        dieselConsumption = dbl(diesel), notes = notes
                    ))
                    saved = true
                }
            },
            modifier = Modifier.fillMaxWidth(), enabled = date.isNotBlank()
        ) {
            Icon(Icons.Default.Save, null); Spacer(Modifier.width(6.dp))
            Text("ذخیره گزارش روز")
        }
        if (saved) Text("✓ ذخیره شد", color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

// ── Tab 3: تاریخچه نمونه‌ها ──────────────────────────────────────────────────
@Composable
private fun SampleHistoryTab(viewModel: BenefViewModel) {
    val samples by viewModel.allSamples.collectAsState()

    if (samples.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("هیچ نمونه‌ای ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Header
        item {
            Row(modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                .padding(8.dp)) {
                listOf("تاریخ","ردیف","کد","خوراک\n(تن)","Fe%\nخوراک",
                    "محصول\n(تن)","Fe%\nمحصول","بازیابی\nمحاسباتی","بازیابی\nوزنی","فلز\nمحصول"
                ).forEach { h ->
                    Text(h, modifier = Modifier.weight(1f), fontSize = 8.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        items(samples, key = { it.id }) { s ->
            Row(modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(s.date,                           Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("${s.rowNum}",                    Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text(s.sampleCode,                     Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("%.1f".format(s.feedWeight),      Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(s.feedFe),          Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("%.2f".format(s.concWeight),      Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(s.concFe),          Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Text("%.4f".format(s.calcRecovery),    Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center,
                    color = Color(0xFF1565C0))
                Text("%.4f".format(s.weightRecovery),  Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32))
                Text("%.3f".format(s.metalContent),    Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

// ── Tab 4: آمار تجمعی ────────────────────────────────────────────────────────
@Composable
private fun CumulativeStatsTab(viewModel: BenefViewModel) {
    val stats by viewModel.cumulativeStats.collectAsState()
    val reports by viewModel.dailyReports.collectAsState()

    if (stats.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("داده‌ای برای نمایش وجود ندارد", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // خلاصه کلی
        item {
            val last = stats.last()
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("آمار تجمعی کلی", fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D47A1))
                    HorizontalDivider()
                    BSummaryRow("تعداد روزهای ثبت‌شده", "${stats.size}  روز")
                    BSummaryRow("تولید تجمعی (تن)", "%.2f".format(last.monthToDateProdTon))
                    BSummaryRow("عیار میانگین تا تاریخ  (Y)", "%.5f".format(last.cumulativeAvgFe), true)
                    BSummaryRow("بازیابی وزنی میانگین  (Z)", "%.5f".format(last.cumulativeAvgWeightRecovery), true)
                    if (reports.isNotEmpty()) {
                        val totalProd = reports.sumOf { it.dailyProductionTon }
                        BSummaryRow("جمع تولید روزانه از گزارش‌ها", "%.2f  تن".format(totalProd))
                    }
                }
            }
        }

        // هدر جدول
        item {
            Row(modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                .padding(8.dp)) {
                listOf("تاریخ", "تولید تجمعی\n(تن)",
                    "عیار میانگین\nتا تاریخ (Y)",
                    "بازیابی وزنی\nمیانگین (Z)"
                ).forEach { h ->
                    Text(h, modifier = Modifier.weight(1f), fontSize = 9.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        items(stats.reversed()) { s ->
            Row(modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(s.date,
                    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("%.2f".format(s.monthToDateProdTon),
                    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
                Text("%.5f".format(s.cumulativeAvgFe),
                    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center,
                    color = Color(0xFF1565C0), fontWeight = FontWeight.Medium)
                Text("%.5f".format(s.cumulativeAvgWeightRecovery),
                    Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(thickness = 0.5.dp)
        }

        // راهنمای فرمول‌ها
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("فرمول‌ها (مطابق اکسل گزارش پرعیارسازی)",
                        fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("• بازیابی محاسباتی (V) = (وزن محصول × Fe محصول) / (وزن خوراک × Fe خوراک)", fontSize = 9.sp)
                    Text("• بازیابی وزنی (W) = وزن محصول / وزن خوراک", fontSize = 9.sp)
                    Text("• محتوی فلز محصول (X) = وزن محصول × Fe محصول", fontSize = 9.sp)
                    Text("• عیار میانگین تا تاریخ (Y) = Σ(وزن محصول×Fe) / Σوزن محصول", fontSize = 9.sp)
                    Text("• بازیابی وزنی میانگین (Z) = Σوزن محصول / Σوزن خوراک", fontSize = 9.sp)
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
@Composable
private fun LimsToggle(label: String, value: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Switch(checked = value, onCheckedChange = onToggle,
            thumbContent = {
                Text(if (value) "ON" else "OFF", fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value) Color.White else Color.Gray)
            })
    }
}

@Composable
private fun BCalcRow(label: String, formula: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
        Text(formula, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun BSummaryRow(label: String, value: String, highlight: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 11.sp, modifier = Modifier.weight(1f),
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface)
        Text(value, fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun BSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            HorizontalDivider()
            content()
        }
    }
}
