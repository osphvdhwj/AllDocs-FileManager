package com.alldocs.filemanager.model

import java.io.File
import java.util.UUID

data class TabItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val currentDirectory: File,
    val navigationHistory: MutableList<File> = mutableListOf(currentDirectory),
    val isActive: Boolean = false
) {
    fun getDisplayTitle(): String {
        return currentDirectory.name.ifEmpty { "Storage" }
    }
}