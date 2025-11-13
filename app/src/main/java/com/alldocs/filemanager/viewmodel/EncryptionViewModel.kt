package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alldocs.filemanager.model.EncryptedFile
import com.alldocs.filemanager.model.SecureVault
import com.alldocs.filemanager.util.EncryptionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.crypto.SecretKey

class EncryptionViewModel : ViewModel() {
    
    private val _vaults = MutableStateFlow<List<SecureVault>>(emptyList())
    val vaults: StateFlow<List<SecureVault>> = _vaults.asStateFlow()
    
    private val _encryptionState = MutableStateFlow<EncryptionState>(EncryptionState.Idle)
    val encryptionState: StateFlow<EncryptionState> = _encryptionState.asStateFlow()
    
    private var currentKey: SecretKey? = null
    
    fun createVault(name: String, vaultPath: File, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _encryptionState.value = EncryptionState.Processing
                
                vaultPath.mkdirs()
                
                val key = EncryptionUtils.generateKey()
                currentKey = key
                
                val vault = SecureVault(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    path = vaultPath.absolutePath,
                    isLocked = false
                )
                
                _vaults.value = _vaults.value + vault
                _encryptionState.value = EncryptionState.Success("Vault created successfully")
            } catch (e: Exception) {
                _encryptionState.value = EncryptionState.Error(e.message ?: "Failed to create vault")
            }
        }
    }
    
    fun encryptFile(file: File, vaultId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _encryptionState.value = EncryptionState.Processing
                
                val vault = _vaults.value.find { it.id == vaultId }
                if (vault == null) {
                    _encryptionState.value = EncryptionState.Error("Vault not found")
                    return@launch
                }
                
                val key = currentKey ?: EncryptionUtils.generateKey().also { currentKey = it }
                
                val encryptedFile = File(vault.path, "${file.name}.encrypted")
                val success = EncryptionUtils.encryptFile(file, encryptedFile, key)
                
                if (success) {
                    val encFileInfo = EncryptedFile(
                        originalFile = file,
                        encryptedFile = encryptedFile,
                        encryptionTimestamp = System.currentTimeMillis()
                    )
                    
                    _vaults.value = _vaults.value.map { v ->
                        if (v.id == vaultId) {
                            v.copy(encryptedFiles = v.encryptedFiles + encFileInfo)
                        } else v
                    }
                    
                    _encryptionState.value = EncryptionState.Success("File encrypted successfully")
                } else {
                    _encryptionState.value = EncryptionState.Error("Encryption failed")
                }
            } catch (e: Exception) {
                _encryptionState.value = EncryptionState.Error(e.message ?: "Encryption error")
            }
        }
    }
    
    fun decryptFile(encryptedFile: File, outputFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _encryptionState.value = EncryptionState.Processing
                
                val key = currentKey
                if (key == null) {
                    _encryptionState.value = EncryptionState.Error("No key available")
                    return@launch
                }
                
                val success = EncryptionUtils.decryptFile(encryptedFile, outputFile, key)
                
                if (success) {
                    _encryptionState.value = EncryptionState.Success("File decrypted successfully")
                } else {
                    _encryptionState.value = EncryptionState.Error("Decryption failed")
                }
            } catch (e: Exception) {
                _encryptionState.value = EncryptionState.Error(e.message ?: "Decryption error")
            }
        }
    }
    
    fun lockVault(vaultId: String) {
        _vaults.value = _vaults.value.map { vault ->
            if (vault.id == vaultId) vault.copy(isLocked = true) else vault
        }
        currentKey = null
    }
    
    fun unlockVault(vaultId: String, password: String) {
        // In production, validate password and load key
        _vaults.value = _vaults.value.map { vault ->
            if (vault.id == vaultId) vault.copy(isLocked = false) else vault
        }
        currentKey = EncryptionUtils.generateKey()
    }
}

sealed class EncryptionState {
    object Idle : EncryptionState()
    object Processing : EncryptionState()
    data class Success(val message: String) : EncryptionState()
    data class Error(val message: String) : EncryptionState()
}