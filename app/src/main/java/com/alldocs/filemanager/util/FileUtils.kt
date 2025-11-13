package com.alldocs.filemanager.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.model.FileType
import java.io.File

object FileUtils {
    
    fun getStorageDirectories(): List<File> {
        val directories = mutableListOf<File>()
        
        // Primary external storage
        val externalStorage = Environment.getExternalStorageDirectory()
        if (externalStorage.exists()) {
            directories.add(externalStorage)
        }
        
        return directories
    }
    
    fun getQuickAccessFolders(): List<File> {
        return listOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        ).filter { it.exists() }
    }
    
    fun getFilesInDirectory(directory: File, showHidden: Boolean = false): List<FileItem> {
        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }
        
        return directory.listFiles()?.filter { file ->
            showHidden || !file.name.startsWith(".")
        }?.map { FileItem(it) }?.sortedWith(
            compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() }
        ) ?: emptyList()
    }
    
    fun searchFiles(directory: File, query: String, showHidden: Boolean = false): List<FileItem> {
        val results = mutableListOf<FileItem>()
        
        fun searchRecursive(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (showHidden || !file.name.startsWith(".")) {
                    if (file.name.lowercase().contains(query.lowercase())) {
                        results.add(FileItem(file))
                    }
                    if (file.isDirectory) {
                        searchRecursive(file)
                    }
                }
            }
        }
        
        searchRecursive(directory)
        return results.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
    }
    
    fun deleteFile(file: File): Boolean {
        return if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }
    
    fun renameFile(file: File, newName: String): Boolean {
        val newFile = File(file.parent, newName)
        return file.renameTo(newFile)
    }
    
    fun shareFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(file)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share file"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getMimeType(file: File): String {
        return when (FileType.fromExtension(file.extension)) {
            FileType.PDF -> "application/pdf"
            FileType.WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            FileType.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            FileType.POWERPOINT -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            FileType.ARCHIVE -> when (file.extension.lowercase()) {
                "zip" -> "application/zip"
                "rar" -> "application/x-rar-compressed"
                "7z" -> "application/x-7z-compressed"
                else -> "application/octet-stream"
            }
            FileType.TEXT -> "text/plain"
            FileType.IMAGE -> "image/*"
            else -> "*/*"
        }
    }
}