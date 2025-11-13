package com.alldocs.filemanager.model

import java.io.File

data class Bookmark(
    val id: String,
    val name: String,
    val path: String,
    val file: File = File(path),
    val icon: String? = null,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun exists(): Boolean = file.exists()
}