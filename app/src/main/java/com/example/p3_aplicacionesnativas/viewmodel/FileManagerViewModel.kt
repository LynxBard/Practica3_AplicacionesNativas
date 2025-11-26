package com.example.p3_aplicacionesnativas.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p3_aplicacionesnativas.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class FileManagerViewModel : ViewModel() {

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files = _files.asStateFlow()

    private val _currentPath = MutableStateFlow(Environment.getExternalStorageDirectory().absolutePath)
    val currentPath = _currentPath.asStateFlow()

    private val _fileContent = MutableStateFlow<String?>(null)
    val fileContent = _fileContent.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // NUEVO: Estado de permisos manejado por el ViewModel
    private val _hasPermission = MutableStateFlow(false)
    val hasPermission = _hasPermission.asStateFlow()

    // Función para verificar y actualizar permisos
    fun checkAndUpdatePermissions(context: Context) {
        val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // Solo actualizar si hay cambio en el estado
        if (_hasPermission.value != permissionGranted) {
            _hasPermission.value = permissionGranted
            if (permissionGranted) {
                loadFiles()
            }
        }
    }

    // Actualizar el estado de permisos manualmente
    fun updatePermissionStatus(granted: Boolean) {
        _hasPermission.value = granted
    }

    fun loadFiles(path: String = _currentPath.value) {
        val file = File(path)

        if (file.exists() && file.isDirectory) {
            val list = file.listFiles()

            if (list != null) {
                _currentPath.value = path
                val fileList = list.map { FileItem(it) }
                _files.value = fileList.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                _errorMessage.value = null
            } else {
                _errorMessage.value = "No se pudo acceder a la carpeta (Permiso denegado o error de sistema)."
            }
        }
    }

    fun readFileContent(file: File) {
        viewModelScope.launch {
            _fileContent.value = "Cargando..."
            try {
                val content = withContext(Dispatchers.IO) {
                    if (file.length() > 10 * 1024) {
                        file.bufferedReader().use { it.readText().take(10 * 1024) } + "\n\n... (Archivo truncado por tamaño) ..."
                    } else {
                        file.readText()
                    }
                }
                _fileContent.value = content
            } catch (e: IOException) {
                _fileContent.value = "Error leyendo archivo: ${e.message}"
            }
        }
    }
}