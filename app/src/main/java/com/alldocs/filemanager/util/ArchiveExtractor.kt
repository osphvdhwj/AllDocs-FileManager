package com.alldocs.filemanager.util

import android.os.Environment
import com.alldocs.filemanager.model.ArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object ArchiveExtractor {
    
    fun listEntries(file: File): List<ArchiveEntry> {
        return when (file.extension.lowercase()) {
            "zip" -> listZipEntries(file)
            "tar" -> listTarEntries(file)
            "gz", "gzip" -> listGzipEntries(file)
            else -> emptyList()
        }
    }
    
    private fun listZipEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        try {
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    entries.add(
                        ArchiveEntry(
                            name = entry.name,
                            size = entry.size,
                            isDirectory = entry.isDirectory
                        )
                    )
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
            throw Exception("Error reading ZIP archive: ${e.message}")
        }
        
        return entries
    }
    
    private fun listTarEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        try {
            TarArchiveInputStream(FileInputStream(file)).use { tis ->
                var entry = tis.nextEntry
                while (entry != null) {
                    entries.add(
                        ArchiveEntry(
                            name = entry.name,
                            size = entry.size,
                            isDirectory = entry.isDirectory
                        )
                    )
                    entry = tis.nextEntry
                }
            }
        } catch (e: Exception) {
            throw Exception("Error reading TAR archive: ${e.message}")
        }
        
        return entries
    }
    
    private fun listGzipEntries(file: File): List<ArchiveEntry> {
        // GZIP typically contains single file
        return listOf(
            ArchiveEntry(
                name = file.nameWithoutExtension,
                size = file.length(),
                isDirectory = false
            )
        )
    }
    
    fun extractAll(file: File) {
        val outputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            file.nameWithoutExtension
        )
        outputDir.mkdirs()
        
        when (file.extension.lowercase()) {
            "zip" -> extractZip(file, outputDir)
            "tar" -> extractTar(file, outputDir)
            "gz", "gzip" -> extractGzip(file, outputDir)
        }
    }
    
    private fun extractZip(file: File, outputDir: File) {
        ZipInputStream(FileInputStream(file)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outputFile = File(outputDir, entry.name)
                
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                
                entry = zis.nextEntry
            }
        }
    }
    
    private fun extractTar(file: File, outputDir: File) {
        TarArchiveInputStream(FileInputStream(file)).use { tis ->
            var entry = tis.nextEntry
            while (entry != null) {
                val outputFile = File(outputDir, entry.name)
                
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { fos ->
                        tis.copyTo(fos)
                    }
                }
                
                entry = tis.nextEntry
            }
        }
    }
    
    private fun extractGzip(file: File, outputDir: File) {
        val outputFile = File(outputDir, file.nameWithoutExtension)
        outputFile.parentFile?.mkdirs()
        
        GzipCompressorInputStream(FileInputStream(file)).use { gis ->
            FileOutputStream(outputFile).use { fos ->
                gis.copyTo(fos)
            }
        }
    }
    
    fun extractEntry(archiveFile: File, entry: ArchiveEntry) {
        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFile = File(outputDir, entry.name)
        outputFile.parentFile?.mkdirs()
        
        when (archiveFile.extension.lowercase()) {
            "zip" -> extractZipEntry(archiveFile, entry.name, outputFile)
            "tar" -> extractTarEntry(archiveFile, entry.name, outputFile)
        }
    }
    
    private fun extractZipEntry(file: File, entryName: String, outputFile: File) {
        ZipInputStream(FileInputStream(file)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (entry.name == entryName && !entry.isDirectory) {
                    FileOutputStream(outputFile).use { fos ->
                        zis.copyTo(fos)
                    }
                    break
                }
                entry = zis.nextEntry
            }
        }
    }
    
    private fun extractTarEntry(file: File, entryName: String, outputFile: File) {
        TarArchiveInputStream(FileInputStream(file)).use { tis ->
            var entry = tis.nextEntry
            while (entry != null) {
                if (entry.name == entryName && !entry.isDirectory) {
                    FileOutputStream(outputFile).use { fos ->
                        tis.copyTo(fos)
                    }
                    break
                }
                entry = tis.nextEntry
            }
        }
    }
}