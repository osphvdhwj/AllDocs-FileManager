package com.alldocs.filemanager.model

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileItem(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val isDirectory: Boolean = file.isDirectory,
    val size: Long = file.length(),
    val lastModified: Long = file.lastModified(),
    val extension: String = file.extension.lowercase(),
    val fileType: FileType = FileType.fromExtension(file.extension)
) {
    val sizeFormatted: String
        get() = formatFileSize(size)
    
    val dateFormatted: String
        get() = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(lastModified))
    
    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}

enum class FileType(val displayName: String, val extensions: List<String>) {
    PDF("PDF Document", listOf("pdf")),
    WORD("Word Document", listOf("doc", "docx")),
    EXCEL("Excel Spreadsheet", listOf("xls", "xlsx")),
    POWERPOINT("PowerPoint Presentation", listOf("ppt", "pptx")),
    ARCHIVE("Archive", listOf("zip", "rar", "7z", "tar", "gz", "bz2")),
    TEXT("Text File", listOf("txt", "md", "log")),
    IMAGE("Image", listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")),
    FOLDER("Folder", emptyList()),
    UNKNOWN("Unknown", emptyList());
    
    companion object {
        fun fromExtension(ext: String): FileType {
            val extension = ext.lowercase()
            return values().find { it.extensions.contains(extension) } ?: UNKNOWN
        }
    }
}