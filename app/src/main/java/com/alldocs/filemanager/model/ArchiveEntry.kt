package com.alldocs.filemanager.model

data class ArchiveEntry(
    val name: String,
    val size: Long,
    val isDirectory: Boolean
) {
    val sizeFormatted: String
        get() = formatFileSize(size)
    
    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}