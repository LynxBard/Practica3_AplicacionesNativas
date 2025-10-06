package com.example.p3_aplicacionesnativas.model

import java.io.File

data class FileItem(
    val file: File,
    val name: String = file.name,
    val isDirectory: Boolean = file.isDirectory,
    val size: Long = if (file.isDirectory) 0 else file.length(),
    val lastModified: Long = file.lastModified()
)