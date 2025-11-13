package com.alldocs.filemanager.model

data class StorageInfo(
    val totalSpace: Long,
    val freeSpace: Long,
    val usedSpace: Long,
    val largestFiles: List<FileItem> = emptyList(),
    val filesByType: Map<FileType, Long> = emptyMap(),
    val duplicateFiles: List<List<FileItem>> = emptyList()
) {
    val usedPercentage: Float
        get() = if (totalSpace > 0) (usedSpace.toFloat() / totalSpace.toFloat()) * 100 else 0f
    
    val freePercentage: Float
        get() = if (totalSpace > 0) (freeSpace.toFloat() / totalSpace.toFloat()) * 100 else 0f
}