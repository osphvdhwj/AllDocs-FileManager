package com.alldocs.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alldocs.filemanager.model.FileItem
import com.alldocs.filemanager.model.FileType
import com.alldocs.filemanager.ui.screen.*
import com.alldocs.filemanager.ui.theme.AllDocsFileManagerTheme
import com.alldocs.filemanager.util.PermissionUtils
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Request MANAGE_EXTERNAL_STORAGE for Android 11+
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestPermissions()
        
        setContent {
            AllDocsFileManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var hasPermission by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = "permission_check"
    ) {
        composable("permission_check") {
            PermissionCheckScreen(
                onPermissionGranted = {
                    hasPermission = true
                    navController.navigate("file_browser") {
                        popUpTo("permission_check") { inclusive = true }
                    }
                }
            )
        }
        
        composable("file_browser") {
            FileBrowserScreen(
                onFileClick = { fileItem ->
                    when (fileItem.fileType) {
                        FileType.FOLDER -> {
                            // Navigation to folder is handled by ViewModel
                        }
                        FileType.PDF -> {
                            val encodedPath = URLEncoder.encode(
                                fileItem.path,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("pdf_viewer/$encodedPath")
                        }
                        FileType.WORD, FileType.EXCEL, FileType.POWERPOINT -> {
                            val encodedPath = URLEncoder.encode(
                                fileItem.path,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("office_viewer/$encodedPath")
                        }
                        FileType.ARCHIVE -> {
                            val encodedPath = URLEncoder.encode(
                                fileItem.path,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("archive_viewer/$encodedPath")
                        }
                        else -> {
                            // Handle other file types or show unsupported message
                        }
                    }
                },
                onNavigateUp = { /* Handle exit */ }
            )
        }
        
        composable(
            route = "pdf_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: return@composable
            val filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
            PdfViewerScreen(
                file = File(filePath),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "office_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: return@composable
            val filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
            OfficeViewerScreen(
                file = File(filePath),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "archive_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: return@composable
            val filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
            ArchiveViewerScreen(
                file = File(filePath),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PermissionCheckScreen(
    onPermissionGranted: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var hasPermission by remember {
        mutableStateOf(PermissionUtils.hasStoragePermission(context))
    }
    
    LaunchedEffect(Unit) {
        if (hasPermission) {
            onPermissionGranted()
        }
    }
    
    if (!hasPermission) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Storage Permission Required",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This app needs storage permission to browse and view your files.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            intent.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intent)
                        } else {
                            ActivityCompat.requestPermissions(
                                context as ComponentActivity,
                                PermissionUtils.getRequiredPermissions(),
                                100
                            )
                        }
                    }
                ) {
                    Text("Grant Permission")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        hasPermission = PermissionUtils.hasStoragePermission(context)
                        if (hasPermission) {
                            onPermissionGranted()
                        }
                    }
                ) {
                    Text("Check Again")
                }
            }
        }
    }
}