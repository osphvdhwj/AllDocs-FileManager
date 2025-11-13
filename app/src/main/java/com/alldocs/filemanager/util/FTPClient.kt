package com.alldocs.filemanager.util

import com.alldocs.filemanager.model.RemoteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

class FTPClient(private val host: String, private val port: Int = 21) {
    
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null
    
    suspend fun connect(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = Socket(host, port)
            reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
            writer = PrintWriter(socket?.getOutputStream(), true)
            
            // Read welcome message
            reader?.readLine()
            
            // Send USER command
            writer?.println("USER $username")
            reader?.readLine()
            
            // Send PASS command
            writer?.println("PASS $password")
            val response = reader?.readLine()
            
            response?.startsWith("230") == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun listFiles(path: String): List<RemoteFile> = withContext(Dispatchers.IO) {
        try {
            writer?.println("CWD $path")
            reader?.readLine()
            
            writer?.println("PASV")
            val pasvResponse = reader?.readLine()
            
            writer?.println("LIST")
            reader?.readLine()
            
            // Parse file list (simplified)
            val files = mutableListOf<RemoteFile>()
            var line = reader?.readLine()
            while (line != null && !line.startsWith("226")) {
                // Parse FTP LIST format (basic parsing)
                val parts = line.split("\\s+".toRegex())
                if (parts.size >= 9) {
                    val name = parts.subList(8, parts.size).joinToString(" ")
                    val isDir = line.startsWith("d")
                    files.add(
                        RemoteFile(
                            name = name,
                            path = "$path/$name",
                            size = parts.getOrNull(4)?.toLongOrNull() ?: 0,
                            isDirectory = isDir,
                            lastModified = System.currentTimeMillis()
                        )
                    )
                }
                line = reader?.readLine()
            }
            
            files
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun downloadFile(remotePath: String, localFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            writer?.println("TYPE I")
            reader?.readLine()
            
            writer?.println("RETR $remotePath")
            val response = reader?.readLine()
            
            if (response?.startsWith("150") == true) {
                FileOutputStream(localFile).use { fos ->
                    socket?.getInputStream()?.copyTo(fos)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun disconnect() {
        try {
            writer?.println("QUIT")
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}