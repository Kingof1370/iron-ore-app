package ir.alibahmani.ironorecalc.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.ui.components.IronOreTopBar

@Composable
fun DiagramScreen(navController: NavController) {
    Scaffold(
        topBar = {
            IronOreTopBar(
                title = "دیاگرام فرآیند",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "شماتیک خط فرآوری سنگ آهن — جداسازی مغناطیسی خشک دو مرحله‌ای",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ProcessFlowDiagram()

            Spacer(Modifier.height(16.dp))

            // Legend
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("راهنما", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    LegendItem(Color(0xFF2A6B9A), "مراحل فرآوری اصلی")
                    LegendItem(Color(0xFFE8722A), "جداسازی مغناطیسی")
                    LegendItem(Color(0xFF27AE60), "محصول نهایی")
                    LegendItem(Color(0xFF7F8C8D), "ضایعات (باطله)")
                }
            }
        }
    }
}

@Composable
private fun ProcessFlowDiagram() {
    val infiniteTransition = rememberInfiniteTransition(label = "flow")
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowOffset"
    )

    val nodes = listOf(
        ProcessNode("سنگ معدنی خام", Color(0xFF5D6D7E), Icons.Default.Landscape),
        ProcessNode("سنگ‌شکن", Color(0xFF2A6B9A), Icons.Default.Construction),
        ProcessNode("آسیاب", Color(0xFF2A6B9A), Icons.Default.Autorenew),
        ProcessNode("سرند", Color(0xFF2A6B9A), Icons.Default.GridOn),
        ProcessNode("جداساز مغناطیسی\nمرحله اول", Color(0xFFE8722A), Icons.Default.ElectricBolt),
        ProcessNode("جداساز مغناطیسی\nمرحله دوم", Color(0xFFE8722A), Icons.Default.ElectricBolt),
        ProcessNode("فیلتر", Color(0xFF1ABC9C), Icons.Default.FilterAlt),
        ProcessNode("کنسانتره نهایی", Color(0xFF27AE60), Icons.Default.CheckCircle)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        nodes.forEachIndexed { index, node ->
            ProcessNodeCard(node)
            if (index < nodes.size - 1) {
                FlowArrow(flowOffset)
                // Show waste branches at separation stages
                if (index == 4) {
                    WasteBranch("باطله مرحله اول")
                }
                if (index == 5) {
                    WasteBranch("باطله نهایی")
                }
            }
        }
    }
}

@Composable
private fun ProcessNodeCard(node: ProcessNode) {
    Card(
        modifier = Modifier
            .width(240.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = node.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(node.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Text(
                node.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun FlowArrow(flowOffset: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier
            .width(240.dp)
            .height(40.dp)
    ) {
        val centerX = size.width / 2f
        // Draw dashed animated line
        val dashLength = 12.dp.toPx()
        val gapLength = 8.dp.toPx()
        val totalLength = dashLength + gapLength
        val animatedOffset = flowOffset * totalLength
        val paint = Paint().apply {
            color = primaryColor
            strokeWidth = 3.dp.toPx()
            style = PaintingStyle.Stroke
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength, gapLength), animatedOffset
            )
        }
        drawLine(
            brush = SolidColor(primaryColor),
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height - 10.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), animatedOffset)
        )
        // Arrow head
        val arrowY = size.height - 4.dp.toPx()
        drawPath(
            path = Path().apply {
                moveTo(centerX, arrowY)
                lineTo(centerX - 8.dp.toPx(), arrowY - 12.dp.toPx())
                lineTo(centerX + 8.dp.toPx(), arrowY - 12.dp.toPx())
                close()
            },
            color = primaryColor
        )
    }
}

@Composable
private fun WasteBranch(label: String) {
    Row(
        modifier = Modifier.width(300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(Modifier.weight(1f))
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7F8C8D))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

private data class ProcessNode(val label: String, val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector)
