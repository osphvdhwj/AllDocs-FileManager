package com.alldocs.filemanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DualPaneLayout(
    leftPane: @Composable () -> Unit,
    rightPane: @Composable () -> Unit,
    isLandscape: Boolean = true
) {
    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                leftPane()
            }
            
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                rightPane()
            }
        }
    } else {
        // Single pane in portrait
        Box(modifier = Modifier.fillMaxSize()) {
            leftPane()
        }
    }
}