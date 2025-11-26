package com.example.p3_aplicacionesnativas.data

import android.content.Context
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme
import com.example.p3_aplicacionesnativas.ui.theme.ThemeMode // Importar el nuevo enum

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "app_theme"
        private const val KEY_THEME_MODE = "app_theme_mode" // Nueva clave
    }

    fun saveTheme(theme: AppTheme) {
        // Guardamos el nombre del enum (ej: "Guinda", "Azul")
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getTheme(): AppTheme {
        val themeName = prefs.getString(KEY_THEME, AppTheme.Default.name)
        return try {
            // Intentamos convertir el string guardado de vuelta al Enum
            AppTheme.valueOf(themeName ?: AppTheme.Default.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.Default
        }
    }

    fun saveThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getThemeMode(): ThemeMode {
        val modeName = prefs.getString(KEY_THEME_MODE, ThemeMode.System.name)
        return try {
            ThemeMode.valueOf(modeName ?: ThemeMode.System.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.System
        }
    }
}