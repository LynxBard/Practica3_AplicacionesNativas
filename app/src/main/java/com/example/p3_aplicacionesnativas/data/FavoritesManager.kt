package com.example.p3_aplicacionesnativas.data

import android.content.Context
import android.content.SharedPreferences
import java.io.File

class FavoritesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "file_manager_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_RECENT_FILES = "recent_files"
        private const val MAX_RECENT_FILES = 20
        private const val SEPARATOR = "|#|"
    }

    // Favoritos
    fun addFavorite(filePath: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(filePath)
        saveFavorites(favorites)
    }

    fun removeFavorite(filePath: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(filePath)
        saveFavorites(favorites)
    }

    fun isFavorite(filePath: String): Boolean {
        return getFavorites().contains(filePath)
    }

    fun getFavorites(): Set<String> {
        val favoritesString = prefs.getString(KEY_FAVORITES, "") ?: ""
        return if (favoritesString.isEmpty()) {
            emptySet()
        } else {
            favoritesString.split(SEPARATOR).toSet()
        }
    }

    fun getFavoriteFiles(): List<File> {
        return getFavorites()
            .map { File(it) }
            .filter { it.exists() }
    }

    private fun saveFavorites(favorites: Set<String>) {
        prefs.edit()
            .putString(KEY_FAVORITES, favorites.joinToString(SEPARATOR))
            .apply()
    }

    // Archivos recientes
    fun addRecentFile(filePath: String) {
        val recent = getRecentFiles().toMutableList()
        // Remover si ya existe para ponerlo al inicio
        recent.remove(filePath)
        // Agregar al inicio
        recent.add(0, filePath)
        // Limitar a MAX_RECENT_FILES
        if (recent.size > MAX_RECENT_FILES) {
            recent.subList(MAX_RECENT_FILES, recent.size).clear()
        }
        saveRecentFiles(recent)
    }

    fun getRecentFiles(): List<String> {
        val recentString = prefs.getString(KEY_RECENT_FILES, "") ?: ""
        return if (recentString.isEmpty()) {
            emptyList()
        } else {
            recentString.split(SEPARATOR)
        }
    }

    fun getRecentFilesList(): List<File> {
        return getRecentFiles()
            .map { File(it) }
            .filter { it.exists() && it.isFile }
    }

    fun clearRecentFiles() {
        prefs.edit()
            .remove(KEY_RECENT_FILES)
            .apply()
    }

    private fun saveRecentFiles(recent: List<String>) {
        prefs.edit()
            .putString(KEY_RECENT_FILES, recent.joinToString(SEPARATOR))
            .apply()
    }

    // Limpiar favoritos que ya no existen
    fun cleanupFavorites() {
        val favorites = getFavorites()
        val existingFavorites = favorites.filter { File(it).exists() }.toSet()
        if (existingFavorites.size != favorites.size) {
            saveFavorites(existingFavorites)
        }
    }
}