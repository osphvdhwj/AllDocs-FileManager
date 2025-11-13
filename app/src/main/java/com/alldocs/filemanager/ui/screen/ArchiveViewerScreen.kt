package com.alldocs.filemanager.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alldocs.filemanager.model.ArchiveEntry
import com.alldocs.filemanager.ui.components.FileIcon
import com.alldocs.filemanager.model.FileType
import com.alldocs.filemanager.util.ArchiveExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveViewerScreen(
    file: File,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var entries by remember { mutableStateOf<List<ArchiveEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showExtractDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<ArchiveEntry?>(null) }
    
    LaunchedEffect(file) {
        scope.launch {
            try {
                entries = withContext(Dispatchers.IO) {
                    ArchiveExtractor.listEntries(file)
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error reading archive"
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = file.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${entries.size} items",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        ArchiveExtractor.extractAll(file)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.message
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.FolderOpen, "Extract All")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                entries.isEmpty() -> {
                    Text(
                        text = "Archive is empty",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(entries) { entry ->
                            ArchiveEntryItem(
                                entry = entry,
                                onClick = {
                                    selectedEntry = entry
                                    showExtractDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showExtractDialog && selectedEntry != null) {
        ExtractDialog(
            entry = selectedEntry!!,
            onDismiss = { showExtractDialog = false },
            onConfirm = {
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            ArchiveExtractor.extractEntry(file, selectedEntry!!)
                        }
                        showExtractDialog = false
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            }
        )
    }
}

@Composable
fun ArchiveEntryItem(
    entry: ArchiveEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FileIcon(
                fileType = if (entry.isDirectory) FileType.FOLDER else FileType.fromExtension(entry.name.substringAfterLast(".", "")),
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (entry.isDirectory) "Folder" else entry.sizeFormatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ExtractDialog(
    entry: ArchiveEntry,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Extract File") },
        text = { Text("Extract \"${entry.name}\" to Downloads folder?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Extract")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}