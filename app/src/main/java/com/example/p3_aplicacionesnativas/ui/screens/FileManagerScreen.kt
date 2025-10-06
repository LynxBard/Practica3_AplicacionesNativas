package com.example.p3_aplicacionesnativas.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.p3_aplicacionesnativas.model.FileItem
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel,
    onThemeChange: (AppTheme) -> Unit,
    onFileClick: (File) -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val files by viewModel.files.collectAsState()
    val currentPath by viewModel.currentPath.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
        }
    )

    LaunchedEffect(hasPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasPermission = android.os.Environment.isExternalStorageManager()
        } else {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            hasPermission = permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (hasPermission) {
            viewModel.loadFiles()
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentPath) },
                navigationIcon = {
                    val parentPath = File(currentPath).parent
                    if (parentPath != null) {
                        IconButton(onClick = { viewModel.loadFiles(parentPath) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Más")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("Tema Guinda") }, onClick = {
                                onThemeChange(AppTheme.Guinda)
                                showMenu = false
                            })
                            DropdownMenuItem(text = { Text("Tema Azul") }, onClick = {
                                onThemeChange(AppTheme.Azul)
                                showMenu = false
                            })
                            DropdownMenuItem(text = { Text("Tema por defecto") }, onClick = {
                                onThemeChange(AppTheme.Default)
                                showMenu = false
                            })
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (hasPermission) {
                LazyColumn {
                    items(files) { fileItem ->
                        FileListItem(fileItem = fileItem) {
                            if (fileItem.isDirectory) {
                                viewModel.loadFiles(fileItem.file.absolutePath)
                            } else {
                                val textExtensions = listOf("txt", "md", "log", "json", "xml")
                                if (fileItem.file.extension in textExtensions) {
                                    onFileClick(fileItem.file)
                                } else {
                                    openFileWithIntent(context, fileItem.file)
                                }
                            }
                        }
                    }
                }
            } else {
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }) {
                    Text("Otorgar Permiso")
                }
            }
        }
    }
}

private fun openFileWithIntent(context: Context, file: File) {
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: "*/*"
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Abrir con"))
}

@Composable
fun FileListItem(fileItem: FileItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (fileItem.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = fileItem.name)
            Row {
                Text(text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(fileItem.lastModified)))
                if (!fileItem.isDirectory) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "- ${android.text.format.Formatter.formatShortFileSize(LocalContext.current, fileItem.size)}")
                }
            }
        }
    }
}