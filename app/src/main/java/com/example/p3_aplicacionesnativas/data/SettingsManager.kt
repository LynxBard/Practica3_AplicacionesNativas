package com.example.p3_aplicacionesnativas.data

import android.content.Context
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme

class SettingsManager(context: Context) {
    // Usamos un nombre de archivo diferente para separar configuraciones de favoritos
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "app_theme"
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
}