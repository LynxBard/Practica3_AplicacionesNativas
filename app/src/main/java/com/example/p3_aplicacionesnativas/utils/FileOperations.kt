package com.example.p3_aplicacionesnativas.utils

import android.content.Context
import java.io.File
import java.io.IOException

object FileOperations {

    sealed class FileOperationResult {
        object Success : FileOperationResult()
        data class Error(val message: String) : FileOperationResult()
    }

    fun copyFile(source: File, destination: File): FileOperationResult {
        return try {
            if (!source.exists()) {
                return FileOperationResult.Error("El archivo origen no existe")
            }
            if (destination.exists()) {
                return FileOperationResult.Error("Ya existe un archivo con ese nombre")
            }

            source.copyTo(destination, overwrite = false)
            FileOperationResult.Success
        } catch (e: IOException) {
            FileOperationResult.Error("Error al copiar: ${e.message}")
        } catch (e: SecurityException) {
            FileOperationResult.Error("Sin permisos para copiar el archivo")
        }
    }

    fun moveFile(source: File, destination: File): FileOperationResult {
        return try {
            if (!source.exists()) {
                return FileOperationResult.Error("El archivo origen no existe")
            }
            if (destination.exists()) {
                return FileOperationResult.Error("Ya existe un archivo con ese nombre")
            }

            val copied = source.copyTo(destination, overwrite = false)
            if (copied.exists() && source.delete()) {
                FileOperationResult.Success
            } else {
                destination.delete() // Limpiar si algo salió mal
                FileOperationResult.Error("Error al mover el archivo")
            }
        } catch (e: IOException) {
            FileOperationResult.Error("Error al mover: ${e.message}")
        } catch (e: SecurityException) {
            FileOperationResult.Error("Sin permisos para mover el archivo")
        }
    }

    fun renameFile(file: File, newName: String): FileOperationResult {
        return try {
            if (!file.exists()) {
                return FileOperationResult.Error("El archivo no existe")
            }
            if (newName.isBlank()) {
                return FileOperationResult.Error("El nombre no puede estar vacío")
            }
            if (newName.contains("/") || newName.contains("\\")) {
                return FileOperationResult.Error("El nombre contiene caracteres no válidos")
            }

            val newFile = File(file.parent, newName)
            if (newFile.exists()) {
                return FileOperationResult.Error("Ya existe un archivo con ese nombre")
            }

            if (file.renameTo(newFile)) {
                FileOperationResult.Success
            } else {
                FileOperationResult.Error("No se pudo renombrar el archivo")
            }
        } catch (e: SecurityException) {
            FileOperationResult.Error("Sin permisos para renombrar el archivo")
        }
    }

    fun deleteFile(file: File): FileOperationResult {
        return try {
            if (!file.exists()) {
                return FileOperationResult.Error("El archivo no existe")
            }

            if (file.isDirectory) {
                // Eliminar recursivamente si es directorio
                val deleted = file.deleteRecursively()
                if (deleted) {
                    FileOperationResult.Success
                } else {
                    FileOperationResult.Error("No se pudo eliminar la carpeta")
                }
            } else {
                if (file.delete()) {
                    FileOperationResult.Success
                } else {
                    FileOperationResult.Error("No se pudo eliminar el archivo")
                }
            }
        } catch (e: SecurityException) {
            FileOperationResult.Error("Sin permisos para eliminar el archivo")
        }
    }

    fun createFolder(parentDir: File, folderName: String): FileOperationResult {
        return try {
            if (folderName.isBlank()) {
                return FileOperationResult.Error("El nombre no puede estar vacío")
            }
            if (folderName.contains("/") || folderName.contains("\\")) {
                return FileOperationResult.Error("El nombre contiene caracteres no válidos")
            }

            val newFolder = File(parentDir, folderName)
            if (newFolder.exists()) {
                return FileOperationResult.Error("Ya existe una carpeta con ese nombre")
            }

            if (newFolder.mkdir()) {
                FileOperationResult.Success
            } else {
                FileOperationResult.Error("No se pudo crear la carpeta")
            }
        } catch (e: SecurityException) {
            FileOperationResult.Error("Sin permisos para crear carpetas")
        }
    }

    fun getFileExtension(file: File): String {
        return file.extension.lowercase()
    }

    fun isTextFile(file: File): Boolean {
        val textExtensions = setOf("txt", "md", "log", "json", "xml", "csv", "html", "htm")
        return getFileExtension(file) in textExtensions
    }

    fun isImageFile(file: File): Boolean {
        val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        return getFileExtension(file) in imageExtensions
    }

    fun isJsonFile(file: File): Boolean {
        return getFileExtension(file) == "json"
    }

    fun isXmlFile(file: File): Boolean {
        return getFileExtension(file) == "xml"
    }
}