package com.alldocs.filemanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.viewmodel.FileBrowserViewModel
import com.alldocs.filemanager.viewmodel.TabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabbedFileBrowserScreen(
    tabViewModel: TabViewModel = viewModel(),
    onFileClick: (FileItem) -> Unit
) {
    val tabs by tabViewModel.tabs.collectAsState()
    val activeTabId by tabViewModel.activeTabId.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 3.dp
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs, key = { it.id }) { tab ->
                        TabChip(
                            title = tab.getDisplayTitle(),
                            isActive = tab.id == activeTabId,
                            onClick = { tabViewModel.setActiveTab(tab.id) },
                            onClose = { tabViewModel.closeTab(tab.id) }
                        )
                    }
                }
                
                IconButton(onClick = {
                    tabViewModel.getActiveTab()?.let { activeTab ->
                        tabViewModel.addTab(activeTab.currentDirectory)
                    }
                }) {
                    Icon(Icons.Default.Add, "Add Tab")
                }
            }
        }
        
        // Active Tab Content
        val activeTab = tabs.find { it.id == activeTabId }
        if (activeTab != null) {
            FileBrowserScreen(
                onFileClick = onFileClick,
                onNavigateUp = { }
            )
        }
    }
}

@Composable
fun TabChip(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    FilterChip(
        selected = isActive,
        onClick = onClick,
        label = { Text(title) },
        trailingIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    "Close Tab",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )
}