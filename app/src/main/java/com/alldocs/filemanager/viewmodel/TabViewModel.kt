package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.alldocs.filemanager.model.TabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class TabViewModel : ViewModel() {
    
    private val _tabs = MutableStateFlow<List<TabItem>>(emptyList())
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()
    
    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()
    
    init {
        // Create initial tab
        val initialTab = TabItem(
            title = "Home",
            currentDirectory = File("/storage/emulated/0"),
            isActive = true
        )
        _tabs.value = listOf(initialTab)
        _activeTabId.value = initialTab.id
    }
    
    fun addTab(directory: File) {
        val newTab = TabItem(
            title = directory.name.ifEmpty { "Storage" },
            currentDirectory = directory,
            isActive = false
        )
        _tabs.value = _tabs.value + newTab
    }
    
    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) return // Keep at least one tab
        
        _tabs.value = currentTabs.filter { it.id != tabId }
        
        if (_activeTabId.value == tabId) {
            _activeTabId.value = _tabs.value.firstOrNull()?.id
        }
    }
    
    fun setActiveTab(tabId: String) {
        _activeTabId.value = tabId
        _tabs.value = _tabs.value.map { tab ->
            tab.copy(isActive = tab.id == tabId)
        }
    }
    
    fun updateTabDirectory(tabId: String, directory: File) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                tab.copy(
                    currentDirectory = directory,
                    title = directory.name.ifEmpty { "Storage" },
                    navigationHistory = tab.navigationHistory.apply { add(directory) }
                )
            } else {
                tab
            }
        }
    }
    
    fun getActiveTab(): TabItem? {
        return _tabs.value.find { it.id == _activeTabId.value }
    }
}