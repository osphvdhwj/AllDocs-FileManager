package com.alldocs.filemanager.util

import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.model.FileType
import com.alldocs.filemanager.model.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object StorageAnalyzer {
    
    suspend fun analyzeStorage(rootDir: File): StorageInfo = withContext(Dispatchers.IO) {
        val totalSpace = rootDir.totalSpace
        val freeSpace = rootDir.freeSpace
        val usedSpace = totalSpace - freeSpace
        
        val allFiles = mutableListOf<FileItem>()
        val filesByType = mutableMapOf<FileType, Long>()
        
        scanDirectory(rootDir, allFiles, filesByType)
        
        // Sort by size to find largest files
        val largestFiles = allFiles
            .filter { !it.isDirectory }
            .sortedByDescending { it.size }
            .take(50)
        
        // Find duplicates by size and name
        val duplicates = findDuplicates(allFiles)
        
        StorageInfo(
            totalSpace = totalSpace,
            freeSpace = freeSpace,
            usedSpace = usedSpace,
            largestFiles = largestFiles,
            filesByType = filesByType,
            duplicateFiles = duplicates
        )
    }
    
    private fun scanDirectory(
        dir: File,
        allFiles: MutableList<FileItem>,
        filesByType: MutableMap<FileType, Long>
    ) {
        dir.listFiles()?.forEach { file ->
            try {
                val fileItem = FileItem(file)
                allFiles.add(fileItem)
                
                if (!file.isDirectory) {
                    val currentSize = filesByType.getOrDefault(fileItem.fileType, 0L)
                    filesByType[fileItem.fileType] = currentSize + file.length()
                } else {
                    scanDirectory(file, allFiles, filesByType)
                }
            } catch (e: Exception) {
                // Skip inaccessible files
            }
        }
    }
    
    private fun findDuplicates(files: List<FileItem>): List<List<FileItem>> {
        return files
            .filter { !it.isDirectory }
            .groupBy { "${it.name}_${it.size}" }
            .filter { it.value.size > 1 }
            .values
            .toList()
    }
    
    suspend fun findLargeFiles(dir: File, minSizeMB: Long = 10): List<FileItem> = withContext(Dispatchers.IO) {
        val minSize = minSizeMB * 1024 * 1024
        val largeFiles = mutableListOf<FileItem>()
        
        fun scan(directory: File) {
            directory.listFiles()?.forEach { file ->
                try {
                    if (file.isDirectory) {
                        scan(file)
                    } else if (file.length() >= minSize) {
                        largeFiles.add(FileItem(file))
                    }
                } catch (e: Exception) {
                    // Skip
                }
            }
        }
        
        scan(dir)
        largeFiles.sortedByDescending { it.size }
    }
    
    suspend fun findJunkFiles(dir: File): List<FileItem> = withContext(Dispatchers.IO) {
        val junkPatterns = listOf(
            ".tmp", ".temp", ".cache", ".log", ".bak", ".old",
            "~", ".thumbnails", ".trash"
        )
        
        val junkFiles = mutableListOf<FileItem>()
        
        fun scan(directory: File) {
            directory.listFiles()?.forEach { file ->
                try {
                    val lowerName = file.name.lowercase()
                    if (junkPatterns.any { lowerName.endsWith(it) || lowerName.contains(it) }) {
                        junkFiles.add(FileItem(file))
                    }
                    if (file.isDirectory) {
                        scan(file)
                    }
                } catch (e: Exception) {
                    // Skip
                }
            }
        }
        
        scan(dir)
        junkFiles
    }
}