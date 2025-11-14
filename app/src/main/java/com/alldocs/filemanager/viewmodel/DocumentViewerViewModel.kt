package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DocumentViewerViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<DocumentViewerUiState>(DocumentViewerUiState.Idle)
    val uiState: StateFlow<DocumentViewerUiState> = _uiState.asStateFlow()
    
    private val _currentFile = MutableStateFlow<File?>(null)
    val currentFile: StateFlow<File?> = _currentFile.asStateFlow()
    
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    fun loadDocument(file: File) {
        _currentFile.value = file
        _uiState.value = DocumentViewerUiState.Loading
        
        viewModelScope.launch {
            try {
                // Document loading is handled by specific viewers
                _uiState.value = DocumentViewerUiState.Ready
            } catch (e: Exception) {
                _uiState.value = DocumentViewerUiState.Error(e.message ?: "Error loading document")
            }
        }
    }
    
    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }
    
    fun closeDocument() {
        _currentFile.value = null
        _currentPage.value = 0
        _uiState.value = DocumentViewerUiState.Idle
    }
}

sealed class DocumentViewerUiState {
    object Idle : DocumentViewerUiState()
    object Loading : DocumentViewerUiState()
    object Ready : DocumentViewerUiState()
    data class Error(val message: String) : DocumentViewerUiState()
}