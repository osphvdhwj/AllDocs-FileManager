package com.alldocs.filemanager.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.alldocs.filemanager.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AppManager {
    
    suspend fun getInstalledApps(context: Context, includeSystem: Boolean = false): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        packages.mapNotNull { appInfo ->
            try {
                if (!includeSystem && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                    return@mapNotNull null
                }
                
                val packageInfo = pm.getPackageInfo(appInfo.packageName, 0)
                val apkFile = File(appInfo.sourceDir)
                
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    versionName = packageInfo.versionName ?: "Unknown",
                    versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    },
                    icon = pm.getApplicationIcon(appInfo),
                    apkPath = appInfo.sourceDir,
                    size = apkFile.length(),
                    installDate = packageInfo.firstInstallTime,
                    updateDate = packageInfo.lastUpdateTime,
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.appName }
    }
    
    suspend fun extractApk(context: Context, packageName: String, destinationDir: File): File? = withContext(Dispatchers.IO) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val sourceApk = File(appInfo.sourceDir)
            
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }
            
            val appName = pm.getApplicationLabel(appInfo).toString()
            val destFile = File(destinationDir, "${appName}.apk")
            
            sourceApk.copyTo(destFile, overwrite = true)
            destFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getAppSize(context: Context, packageName: String): Long = withContext(Dispatchers.IO) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            File(appInfo.sourceDir).length()
        } catch (e: Exception) {
            0L
        }
    }
}