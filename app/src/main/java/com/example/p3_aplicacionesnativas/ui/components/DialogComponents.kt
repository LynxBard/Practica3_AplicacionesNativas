package com.example.p3_aplicacionesnativas.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.p3_aplicacionesnativas.model.FileItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItemWithActions(
    fileItem: FileItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    fileItem.isDirectory -> Icons.Filled.Folder
                    else -> Icons.Filled.InsertDriveFile
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (fileItem.isDirectory)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = fileItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favorito",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(start = 4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row {
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(fileItem.lastModified)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!fileItem.isDirectory) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${android.text.format.Formatter.formatShortFileSize(
                                androidx.compose.ui.platform.LocalContext.current,
                                fileItem.size
                            )}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsDialog(
    file: FileItem,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (file.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
                contentDescription = null
            )
        },
        title = { Text(text = file.name) },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text(if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos") },
                    leadingContent = {
                        Icon(
                            if (isFavorite) Icons.Filled.StarBorder else Icons.Filled.Star,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        onToggleFavorite()
                    }
                )
                ListItem(
                    headlineContent = { Text("Renombrar") },
                    leadingContent = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    modifier = Modifier.clickable {
                        onRename()
                    }
                )
                ListItem(
                    headlineContent = { Text("Eliminar") },
                    leadingContent = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    modifier = Modifier.clickable {
                        onDelete()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesDialog(
    favorites: List<File>,
    onDismiss: () -> Unit,
    onFileClick: (File) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Star, contentDescription = null) },
        title = { Text("Favoritos") },
        text = {
            if (favorites.isEmpty()) {
                Text("No hay favoritos guardados")
            } else {
                LazyColumn {
                    items(favorites) { file ->
                        ListItem(
                            headlineContent = { Text(file.name) },
                            supportingContent = { Text(file.parent ?: "") },
                            leadingContent = {
                                Icon(
                                    if (file.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable {
                                onFileClick(file)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentFilesDialog(
    recentFiles: List<File>,
    onDismiss: () -> Unit,
    onFileClick: (File) -> Unit,
    onClearRecent: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.History, contentDescription = null) },
        title = { Text("Archivos Recientes") },
        text = {
            if (recentFiles.isEmpty()) {
                Text("No hay archivos recientes")
            } else {
                LazyColumn {
                    items(recentFiles) { file ->
                        ListItem(
                            headlineContent = { Text(file.name) },
                            supportingContent = { Text(file.parent ?: "") },
                            leadingContent = {
                                Icon(Icons.Filled.InsertDriveFile, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                onFileClick(file)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        dismissButton = {
            if (recentFiles.isNotEmpty()) {
                TextButton(onClick = onClearRecent) {
                    Text("Limpiar")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialog(
    currentName: String,
    newName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
        title = { Text("Renombrar") },
        text = {
            Column {
                Text("Nombre actual: $currentName")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = onNameChange,
                    label = { Text("Nuevo nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = newName.isNotBlank() && newName != currentName
            ) {
                Text("Renombrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    fileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar '$fileName'? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(
    folderName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.CreateNewFolder, contentDescription = null) },
        title = { Text("Crear nueva carpeta") },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = onNameChange,
                label = { Text("Nombre de la carpeta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = folderName.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}