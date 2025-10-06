package com.example.p3_aplicacionesnativas.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import com.example.p3_aplicacionesnativas.model.FileItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException

class FileManagerViewModel : ViewModel() {

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files = _files.asStateFlow()

    private val _currentPath = MutableStateFlow(Environment.getExternalStorageDirectory().absolutePath)
    val currentPath = _currentPath.asStateFlow()

    private val _fileContent = MutableStateFlow<String?>(null)
    val fileContent = _fileContent.asStateFlow()

    fun loadFiles(path: String = _currentPath.value) {
        val file = File(path)
        if (file.exists() && file.isDirectory) {
            _currentPath.value = path
            val fileList = file.listFiles()?.map { FileItem(it) } ?: emptyList()
            _files.value = fileList.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        }
    }

    fun readFileContent(file: File) {
        try {
            _fileContent.value = file.readText()
        } catch (e: IOException) {
            _fileContent.value = "Error reading file: ${e.message}"
        }
    }
}