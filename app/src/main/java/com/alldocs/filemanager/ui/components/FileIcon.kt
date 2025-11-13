package com.alldocs.filemanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alldocs.filemanager.model.FileType

@Composable
fun FileIcon(
    fileType: FileType,
    modifier: Modifier = Modifier
) {
    val (icon, color) = getIconAndColor(fileType)
    
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = fileType.displayName,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun getIconAndColor(fileType: FileType): Pair<ImageVector, Color> {
    return when (fileType) {
        FileType.PDF -> Icons.Default.Description to Color(0xFFE53935)
        FileType.WORD -> Icons.Default.Description to Color(0xFF1976D2)
        FileType.EXCEL -> Icons.Default.TableChart to Color(0xFF388E3C)
        FileType.POWERPOINT -> Icons.Default.Slideshow to Color(0xFFD84315)
        FileType.ARCHIVE -> Icons.Default.FolderZip to Color(0xFFFBC02D)
        FileType.TEXT -> Icons.Default.TextSnippet to Color(0xFF757575)
        FileType.IMAGE -> Icons.Default.Image to Color(0xFF7B1FA2)
        FileType.FOLDER -> Icons.Default.Folder to MaterialTheme.colorScheme.primary
        FileType.UNKNOWN -> Icons.Default.InsertDriveFile to Color(0xFF9E9E9E)
    }
}