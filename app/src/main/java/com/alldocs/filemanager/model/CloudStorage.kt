package com.alldocs.filemanager.model

enum class CloudStorageType {
    GOOGLE_DRIVE,
    DROPBOX,
    ONEDRIVE,
    FTP,
    SFTP,
    SMB,
    WEBDAV,
    LOCAL
}

data class CloudStorageAccount(
    val id: String,
    val name: String,
    val type: CloudStorageType,
    val host: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = "",
    val rootPath: String = "",
    val isConnected: Boolean = false
)

data class RemoteFile(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean,
    val lastModified: Long,
    val mimeType: String = ""
)