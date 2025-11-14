package com.alldocs.filemanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alldocs.filemanager.viewmodel.AnalysisState
import com.alldocs.filemanager.viewmodel.StorageViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    viewModel: StorageViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val storageInfo by viewModel.storageInfo.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    val junkFiles by viewModel.junkFiles.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.analyzeStorage(File("/storage/emulated/0"))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Analyzer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.analyzeStorage(File("/storage/emulated/0"))
                    }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (analysisState) {
            is AnalysisState.Analyzing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing storage...")
                    }
                }
            }
            is AnalysisState.Complete -> {
                storageInfo?.let { info ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Storage Overview
                        item {
                            StorageOverviewCard(info)
                        }
                        
                        // Largest Files
                        item {
                            Text(
                                "Largest Files",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        
                        items(info.largestFiles.take(10)) { file ->
                            FileListItem(
                                fileItem = file,
                                onClick = { },
                                onDeleteClick = { },
                                onRenameClick = { }
                            )
                        }
                        
                        // Junk Files Section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Junk Files", style = MaterialTheme.typography.titleMedium)
                                        Text("${junkFiles.size} files found")
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.findJunkFiles(File("/storage/emulated/0"))
                                        }
                                    ) {
                                        Text("Scan")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is AnalysisState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (analysisState as AnalysisState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun StorageOverviewCard(info: com.alldocs.filemanager.model.StorageInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Storage Overview", style = MaterialTheme.typography.titleLarge)
            
            LinearProgressIndicator(
                progress = { info.usedPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Used", style = MaterialTheme.typography.bodySmall)
                    Text(
                        formatBytes(info.usedSpace),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Free", style = MaterialTheme.typography.bodySmall)
                    Text(
                        formatBytes(info.freeSpace),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}