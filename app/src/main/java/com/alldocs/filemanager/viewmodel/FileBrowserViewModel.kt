package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Stack

class FileBrowserViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<FileBrowserUiState>(FileBrowserUiState.Loading)
    val uiState: StateFlow<FileBrowserUiState> = _uiState.asStateFlow()
    
    private val _currentDirectory = MutableStateFlow<File?>(null)
    val currentDirectory: StateFlow<File?> = _currentDirectory.asStateFlow()
    
    private val _navigationStack = Stack<File>()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _showHidden = MutableStateFlow(false)
    val showHidden: StateFlow<Boolean> = _showHidden.asStateFlow()
    
    fun loadInitialDirectory() {
        val storageDir = FileUtils.getStorageDirectories().firstOrNull()
        if (storageDir != null) {
            navigateToDirectory(storageDir)
        } else {
            _uiState.value = FileBrowserUiState.Error("No storage found")
        }
    }
    
    fun navigateToDirectory(directory: File) {
        if (!directory.exists() || !directory.isDirectory) {
            _uiState.value = FileBrowserUiState.Error("Directory does not exist")
            return
        }
        
        _uiState.value = FileBrowserUiState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val files = FileUtils.getFilesInDirectory(directory, _showHidden.value)
                _currentDirectory.value = directory
                _navigationStack.push(directory)
                _uiState.value = FileBrowserUiState.Success(files)
            } catch (e: Exception) {
                _uiState.value = FileBrowserUiState.Error(e.message ?: "Error loading directory")
            }
        }
    }
    
    fun navigateBack(): Boolean {
        if (_navigationStack.size > 1) {
            _navigationStack.pop()
            val previousDir = _navigationStack.peek()
            navigateToDirectory(previousDir)
            return true
        }
        return false
    }
    
    fun refreshCurrentDirectory() {
        _currentDirectory.value?.let { navigateToDirectory(it) }
    }
    
    fun searchFiles(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            refreshCurrentDirectory()
            return
        }
        
        _uiState.value = FileBrowserUiState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val searchRoot = _currentDirectory.value ?: return@launch
                val results = FileUtils.searchFiles(searchRoot, query, _showHidden.value)
                _uiState.value = FileBrowserUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = FileBrowserUiState.Error(e.message ?: "Search failed")
            }
        }
    }
    
    fun toggleHiddenFiles() {
        _showHidden.value = !_showHidden.value
        refreshCurrentDirectory()
    }
    
    fun deleteFile(fileItem: FileItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = FileUtils.deleteFile(fileItem.file)
                if (success) {
                    refreshCurrentDirectory()
                }
            } catch (e: Exception) {
                _uiState.value = FileBrowserUiState.Error(e.message ?: "Delete failed")
            }
        }
    }
    
    fun renameFile(fileItem: FileItem, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = FileUtils.renameFile(fileItem.file, newName)
                if (success) {
                    refreshCurrentDirectory()
                }
            } catch (e: Exception) {
                _uiState.value = FileBrowserUiState.Error(e.message ?: "Rename failed")
            }
        }
    }
}

sealed class FileBrowserUiState {
    object Loading : FileBrowserUiState()
    data class Success(val files: List<FileItem>) : FileBrowserUiState()
    data class Error(val message: String) : FileBrowserUiState()
}