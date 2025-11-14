package com.alldocs.filemanager.model

import java.io.File

data class EncryptedFile(
    val originalFile: File,
    val encryptedFile: File,
    val encryptionTimestamp: Long,
    val algorithm: String = "AES",
    val keySize: Int = 256
)

data class SecureVault(
    val id: String,
    val name: String,
    val path: String,
    val isLocked: Boolean = true,
    val encryptedFiles: List<EncryptedFile> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)