// Archivo: app/src/main/java/com/example/p3_aplicacionesnativas/viewmodel/FileManagerViewModel.kt

package com.example.p3_aplicacionesnativas.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Importante agregar
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

    // Nuevo estado para mostrar errores en la UI (Opcional pero recomendado)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadFiles(path: String = _currentPath.value) {
        val file = File(path)

        // Verificamos si existe y es directorio
        if (file.exists() && file.isDirectory) {
            val list = file.listFiles()

            if (list != null) {
                _currentPath.value = path
                val fileList = list.map { FileItem(it) }
                _files.value = fileList.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                _errorMessage.value = null // Limpiar errores previos
            } else {
                _errorMessage.value = "No se pudo acceder a la carpeta (Permiso denegado o error de sistema)."
            }
        }
    }

    fun readFileContent(file: File) {
        viewModelScope.launch { // Usar corrutinas para no bloquear el hilo principal
            _fileContent.value = "Cargando..."
            try {
                // CORRECCIÓN: Leer solo una parte si es muy grande o usar un stream
                // Aquí leemos máximo 10KB para vista previa
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