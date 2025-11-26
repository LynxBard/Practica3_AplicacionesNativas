package com.example.p3_aplicacionesnativas.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.p3_aplicacionesnativas.data.FavoritesManager
import com.example.p3_aplicacionesnativas.model.FileItem
import com.example.p3_aplicacionesnativas.ui.components.CreateFolderDialog
import com.example.p3_aplicacionesnativas.ui.components.DeleteConfirmDialog
import com.example.p3_aplicacionesnativas.ui.components.FavoritesDialog
import com.example.p3_aplicacionesnativas.ui.components.FileListItemWithActions
import com.example.p3_aplicacionesnativas.ui.components.FileOptionsDialog
import com.example.p3_aplicacionesnativas.ui.components.RecentFilesDialog
import com.example.p3_aplicacionesnativas.ui.components.RenameDialog
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme
import com.example.p3_aplicacionesnativas.utils.FileOperations
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel,
    onThemeChange: (AppTheme) -> Unit,
    onFileClick: (File) -> Unit,
    onImageClick: (File) -> Unit,
    onJsonXmlClick: (File) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val favoritesManager = remember { FavoritesManager(context) }

    val files by viewModel.files.collectAsState()
    val currentPath by viewModel.currentPath.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showRecentDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<FileItem?>(null) }
    var showFileOptionsDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var operationMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.updatePermissionStatus(isGranted)
            if (isGranted) {
                viewModel.loadFiles()
            }
        }
    )

    // Verificar permisos
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAndUpdatePermissions(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAndUpdatePermissions(context)
        favoritesManager.cleanupFavorites()
    }

    // Breadcrumb path
    val pathSegments = remember(currentPath) {
        currentPath.split("/").filter { it.isNotEmpty() }
    }

    // Manejar botón atrás
    val rootPath = android.os.Environment.getExternalStorageDirectory().absolutePath
    val isAtRoot = currentPath == rootPath

    BackHandler(enabled = !isAtRoot) {
        val parentFile = File(currentPath).parentFile
        if (parentFile != null && parentFile.exists()) {
            viewModel.loadFiles(parentFile.absolutePath)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Explorador de Archivos",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    navigationIcon = {
                        val parentPath = File(currentPath).parent
                        if (parentPath != null) {
                            IconButton(onClick = { viewModel.loadFiles(parentPath) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFavoritesDialog = true }) {
                            Icon(Icons.Filled.Star, contentDescription = "Favoritos")
                        }
                        IconButton(onClick = { showRecentDialog = true }) {
                            Icon(Icons.Filled.History, contentDescription = "Recientes")
                        }
                        IconButton(onClick = { showCreateFolderDialog = true }) {
                            Icon(Icons.Filled.CreateNewFolder, contentDescription = "Nueva carpeta")
                        }
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Más")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tema Guinda") },
                                    onClick = {
                                        onThemeChange(AppTheme.Guinda)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Tema Azul") },
                                    onClick = {
                                        onThemeChange(AppTheme.Azul)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Tema por defecto") },
                                    onClick = {
                                        onThemeChange(AppTheme.Default)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                )

                // Breadcrumb
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        pathSegments.forEachIndexed { index, segment ->
                            Text(
                                text = " / ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = segment,
                                color = if (index == pathSegments.lastIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (index == pathSegments.lastIndex) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = {
            operationMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { operationMessage = null }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasPermission) {
                if (files.isEmpty()) {
                    EmptyFolderContent()
                } else {
                    LazyColumn {
                        items(files) { fileItem ->
                            FileListItemWithActions(
                                fileItem = fileItem,
                                isFavorite = favoritesManager.isFavorite(fileItem.file.absolutePath),
                                onClick = {
                                    if (fileItem.isDirectory) {
                                        viewModel.loadFiles(fileItem.file.absolutePath)
                                    } else {
                                        favoritesManager.addRecentFile(fileItem.file.absolutePath)
                                        when {
                                            FileOperations.isImageFile(fileItem.file) -> {
                                                onImageClick(fileItem.file)
                                            }
                                            FileOperations.isJsonFile(fileItem.file) || FileOperations.isXmlFile(fileItem.file) -> {
                                                onJsonXmlClick(fileItem.file)
                                            }
                                            FileOperations.isTextFile(fileItem.file) -> {
                                                onFileClick(fileItem.file)
                                            }
                                            else -> {
                                                openFileWithIntent(context, fileItem.file)
                                            }
                                        }
                                    }
                                },
                                onLongClick = {
                                    selectedFile = fileItem
                                    showFileOptionsDialog = true
                                }
                            )
                        }
                    }
                }
            } else {
                PermissionRequiredContent(
                    onGrantPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            intent.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intent)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                )
            }
        }
    }

    // Diálogos
    if (showFavoritesDialog) {
        FavoritesDialog(
            favorites = favoritesManager.getFavoriteFiles(),
            onDismiss = { showFavoritesDialog = false },
            onFileClick = { file ->
                showFavoritesDialog = false
                if (file.isDirectory) {
                    viewModel.loadFiles(file.absolutePath)
                } else {
                    when {
                        FileOperations.isImageFile(file) -> onImageClick(file)
                        FileOperations.isJsonFile(file) || FileOperations.isXmlFile(file) -> onJsonXmlClick(file)
                        FileOperations.isTextFile(file) -> onFileClick(file)
                        else -> openFileWithIntent(context, file)
                    }
                }
            }
        )
    }

    if (showRecentDialog) {
        RecentFilesDialog(
            recentFiles = favoritesManager.getRecentFilesList(),
            onDismiss = { showRecentDialog = false },
            onFileClick = { file ->
                showRecentDialog = false
                when {
                    FileOperations.isImageFile(file) -> onImageClick(file)
                    FileOperations.isJsonFile(file) || FileOperations.isXmlFile(file) -> onJsonXmlClick(file)
                    FileOperations.isTextFile(file) -> onFileClick(file)
                    else -> openFileWithIntent(context, file)
                }
            },
            onClearRecent = {
                favoritesManager.clearRecentFiles()
                showRecentDialog = false
            }
        )
    }

    selectedFile?.let { file ->
        if (showFileOptionsDialog) {
            FileOptionsDialog(
                file = file,
                isFavorite = favoritesManager.isFavorite(file.file.absolutePath),
                onDismiss = {
                    showFileOptionsDialog = false
                    selectedFile = null
                },
                onToggleFavorite = {
                    if (favoritesManager.isFavorite(file.file.absolutePath)) {
                        favoritesManager.removeFavorite(file.file.absolutePath)
                    } else {
                        favoritesManager.addFavorite(file.file.absolutePath)
                    }
                    showFileOptionsDialog = false
                    selectedFile = null
                },
                onRename = {
                    newFileName = file.name
                    showFileOptionsDialog = false
                    showRenameDialog = true
                },
                onDelete = {
                    showFileOptionsDialog = false
                    showDeleteConfirmDialog = true
                }
            )
        }
    }

    if (showRenameDialog && selectedFile != null) {
        RenameDialog(
            currentName = selectedFile!!.name,
            newName = newFileName,
            onNameChange = { newFileName = it },
            onConfirm = {
                val result = FileOperations.renameFile(selectedFile!!.file, newFileName)
                when (result) {
                    is FileOperations.FileOperationResult.Success -> {
                        operationMessage = "Archivo renombrado correctamente"
                        viewModel.loadFiles()
                    }
                    is FileOperations.FileOperationResult.Error -> {
                        operationMessage = result.message
                    }
                }
                showRenameDialog = false
                selectedFile = null
            },
            onDismiss = {
                showRenameDialog = false
                selectedFile = null
            }
        )
    }

    if (showDeleteConfirmDialog && selectedFile != null) {
        DeleteConfirmDialog(
            fileName = selectedFile!!.name,
            onConfirm = {
                val result = FileOperations.deleteFile(selectedFile!!.file)
                when (result) {
                    is FileOperations.FileOperationResult.Success -> {
                        operationMessage = "Archivo eliminado correctamente"
                        viewModel.loadFiles()
                    }
                    is FileOperations.FileOperationResult.Error -> {
                        operationMessage = result.message
                    }
                }
                showDeleteConfirmDialog = false
                selectedFile = null
            },
            onDismiss = {
                showDeleteConfirmDialog = false
                selectedFile = null
            }
        )
    }

    if (showCreateFolderDialog) {
        var folderName by remember { mutableStateOf("") }
        CreateFolderDialog(
            folderName = folderName,
            onNameChange = { folderName = it },
            onConfirm = {
                val result = FileOperations.createFolder(File(currentPath), folderName)
                when (result) {
                    is FileOperations.FileOperationResult.Success -> {
                        operationMessage = "Carpeta creada correctamente"
                        viewModel.loadFiles()
                    }
                    is FileOperations.FileOperationResult.Error -> {
                        operationMessage = result.message
                    }
                }
                showCreateFolderDialog = false
            },
            onDismiss = { showCreateFolderDialog = false }
        )
    }
}

@Composable
private fun EmptyFolderContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "Esta carpeta está vacía",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PermissionRequiredContent(onGrantPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Folder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Permiso de Almacenamiento Requerido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "Esta aplicación necesita acceso al almacenamiento para explorar tus archivos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.padding(24.dp))
        Button(onClick = onGrantPermission) {
            Text("Otorgar Permiso")
        }
    }
}

private fun openFileWithIntent(context: Context, file: File) {
    try {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: "*/*"
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Abrir con"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}