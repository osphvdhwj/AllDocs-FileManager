package com.alldocs.filemanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alldocs.filemanager.viewmodel.EncryptionState
import com.alldocs.filemanager.viewmodel.EncryptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionVaultScreen(
    viewModel: EncryptionViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val vaults by viewModel.vaults.collectAsState()
    val encryptionState by viewModel.encryptionState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Vault") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Create Vault")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Lock, "Create") },
                text = { Text("New Vault") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (encryptionState) {
                is EncryptionState.Processing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (vaults.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No vaults created",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Create a vault to encrypt your files",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(vaults) { vault ->
                                VaultCard(
                                    vault = vault,
                                    onUnlock = { viewModel.unlockVault(vault.id, "") },
                                    onLock = { viewModel.lockVault(vault.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showCreateDialog) {
        CreateVaultDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, password ->
                val vaultDir = java.io.File("/storage/emulated/0/SecureVault/$name")
                viewModel.createVault(name, vaultDir, password)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun VaultCard(
    vault: com.alldocs.filemanager.model.SecureVault,
    onUnlock: () -> Unit,
    onLock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (vault.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (vault.isLocked) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vault.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${vault.encryptedFiles.size} files • ${if (vault.isLocked) "Locked" else "Unlocked"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(
                onClick = { if (vault.isLocked) onUnlock() else onLock() }
            ) {
                Text(if (vault.isLocked) "Unlock" else "Lock")
            }
        }
    }
}

@Composable
fun CreateVaultDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var vaultName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Secure Vault") },
        text = {
            Column {
                TextField(
                    value = vaultName,
                    onValueChange = { vaultName = it },
                    label = { Text("Vault Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(vaultName, password) },
                enabled = vaultName.isNotBlank() && password.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}