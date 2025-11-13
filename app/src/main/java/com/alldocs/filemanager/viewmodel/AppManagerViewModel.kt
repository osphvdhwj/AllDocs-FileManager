package com.alldocs.filemanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alldocs.filemanager.model.AppInfo
import com.alldocs.filemanager.util.AppManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AppManagerViewModel : ViewModel() {
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()
    
    private val _showSystemApps = MutableStateFlow(false)
    val showSystemApps: StateFlow<Boolean> = _showSystemApps.asStateFlow()
    
    fun loadApps(context: Context) {
        viewModelScope.launch {
            try {
                _loadingState.value = LoadingState.Loading
                val appList = AppManager.getInstalledApps(context, _showSystemApps.value)
                _apps.value = appList
                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Failed to load apps")
            }
        }
    }
    
    fun toggleSystemApps(context: Context) {
        _showSystemApps.value = !_showSystemApps.value
        loadApps(context)
    }
    
    fun extractApk(context: Context, packageName: String, destinationDir: File) {
        viewModelScope.launch {
            try {
                _loadingState.value = LoadingState.Loading
                val apkFile = AppManager.extractApk(context, packageName, destinationDir)
                if (apkFile != null) {
                    _loadingState.value = LoadingState.Success
                } else {
                    _loadingState.value = LoadingState.Error("Failed to extract APK")
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Extraction failed")
            }
        }
    }
    
    fun searchApps(query: String) {
        if (query.isBlank()) return
        _apps.value = _apps.value.filter { app ->
            app.appName.contains(query, ignoreCase = true) ||
            app.packageName.contains(query, ignoreCase = true)
        }
    }
}

sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
}