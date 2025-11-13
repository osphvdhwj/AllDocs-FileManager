package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.model.StorageInfo
import com.alldocs.filemanager.util.StorageAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class StorageViewModel : ViewModel() {
    
    private val _storageInfo = MutableStateFlow<StorageInfo?>(null)
    val storageInfo: StateFlow<StorageInfo?> = _storageInfo.asStateFlow()
    
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()
    
    private val _junkFiles = MutableStateFlow<List<FileItem>>(emptyList())
    val junkFiles: StateFlow<List<FileItem>> = _junkFiles.asStateFlow()
    
    fun analyzeStorage(rootDir: File) {
        viewModelScope.launch {
            try {
                _analysisState.value = AnalysisState.Analyzing
                val info = StorageAnalyzer.analyzeStorage(rootDir)
                _storageInfo.value = info
                _analysisState.value = AnalysisState.Complete
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Analysis failed")
            }
        }
    }
    
    fun findJunkFiles(rootDir: File) {
        viewModelScope.launch {
            try {
                _analysisState.value = AnalysisState.Analyzing
                val junk = StorageAnalyzer.findJunkFiles(rootDir)
                _junkFiles.value = junk
                _analysisState.value = AnalysisState.Complete
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Search failed")
            }
        }
    }
    
    fun deleteJunkFiles() {
        viewModelScope.launch {
            _junkFiles.value.forEach { fileItem ->
                try {
                    fileItem.file.delete()
                } catch (e: Exception) {
                    // Continue with next file
                }
            }
            _junkFiles.value = emptyList()
        }
    }
}

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Analyzing : AnalysisState()
    object Complete : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}