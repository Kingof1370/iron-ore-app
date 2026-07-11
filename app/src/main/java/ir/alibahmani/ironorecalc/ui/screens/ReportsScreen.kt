package ir.alibahmani.ironorecalc.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.alibahmani.ironorecalc.domain.model.SavedProject
import ir.alibahmani.ironorecalc.ui.components.*
import ir.alibahmani.ironorecalc.ui.viewmodel.HistoryViewModel
import ir.alibahmani.ironorecalc.utils.CsvExporter
import ir.alibahmani.ironorecalc.utils.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            IronOreTopBar(
                title = "گزارش‌ها",
                onNavigateBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (projects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    Text("هیچ پروژه‌ای برای گزارش‌دهی وجود ندارد", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("ابتدا یک محاسبه را ذخیره کنید", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "انتخاب پروژه برای صدور گزارش",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(projects, key = { it.id }) { project ->
                        ReportProjectCard(
                            project = project,
                            isExporting = isExporting,
                            onExportPdf = {
                                scope.launch {
                                    isExporting = true
                                    try {
                                        val file = withContext(Dispatchers.IO) {
                                            PdfGenerator.generate(context, project.name, project.result)
                                        }
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "صدور PDF"))
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("خطا در تولید PDF: ${e.message}")
                                    } finally {
                                        isExporting = false
                                    }
                                }
                            },
                            onExportCsv = {
                                scope.launch {
                                    isExporting = true
                                    try {
                                        val file = withContext(Dispatchers.IO) {
                                            CsvExporter.export(context, project.name, project.inputs, project.result)
                                        }
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/csv"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "صدور CSV"))
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("خطا در صدور CSV: ${e.message}")
                                    } finally {
                                        isExporting = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportProjectCard(
    project: SavedProject,
    isExporting: Boolean,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR")) }
    val mb = project.result.massBalance

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(project.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(dateFormat.format(project.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip("%.1f٪".format(mb.rTotal), Color(0xFF27AE60))
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onExportPdf,
                    enabled = !isExporting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("PDF")
                }
                OutlinedButton(
                    onClick = onExportCsv,
                    enabled = !isExporting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("CSV")
                }
            }
        }
    }
}
