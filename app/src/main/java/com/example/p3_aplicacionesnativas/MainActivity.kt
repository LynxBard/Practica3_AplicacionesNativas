package com.example.p3_aplicacionesnativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext // Importante agregar este import
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.p3_aplicacionesnativas.data.SettingsManager // Importar tu nueva clase
import com.example.p3_aplicacionesnativas.ui.screens.*
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme
import com.example.p3_aplicacionesnativas.ui.theme.AzulTheme
import com.example.p3_aplicacionesnativas.ui.theme.GuindaTheme
import com.example.p3_aplicacionesnativas.ui.theme.P3_AplicacionesNativasTheme
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.isSystemInDarkTheme // Necesario para el modo sistema
import com.example.p3_aplicacionesnativas.ui.theme.ThemeMode // Importar el nuevo enum

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }

            // Estado para el Color del tema (Guinda, Azul...)
            var currentTheme by remember { mutableStateOf(settingsManager.getTheme()) }
            // Estado para el Modo (Claro, Oscuro, Sistema)
            var currentMode by remember { mutableStateOf(settingsManager.getThemeMode()) }

            // Función para determinar si usar modo oscuro
            val useDarkTheme = when (currentMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> isSystemInDarkTheme()
            }

            // Callbacks para guardar cambios
            val onThemeChange = { newTheme: AppTheme ->
                currentTheme = newTheme
                settingsManager.saveTheme(newTheme)
            }

            val onModeChange = { newMode: ThemeMode ->
                currentMode = newMode
                settingsManager.saveThemeMode(newMode)
            }

            // Aplicamos el tema pasando explícitamente el parámetro darkTheme
            when (currentTheme) {
                AppTheme.Guinda -> GuindaTheme(darkTheme = useDarkTheme) {
                    AppContent(
                        onThemeChange = onThemeChange,
                        onModeChange = onModeChange // Pasamos la nueva función
                    )
                }
                AppTheme.Azul -> AzulTheme(darkTheme = useDarkTheme) {
                    AppContent(
                        onThemeChange = onThemeChange,
                        onModeChange = onModeChange
                    )
                }
                AppTheme.Default -> P3_AplicacionesNativasTheme(darkTheme = useDarkTheme) {
                    AppContent(
                        onThemeChange = onThemeChange,
                        onModeChange = onModeChange
                    )
                }
            }
        }
    }
}

@Composable
private fun AppContent(
    onThemeChange: (AppTheme) -> Unit,
    onModeChange: (ThemeMode) -> Unit // Nuevo parámetro
) {
    val navController = rememberNavController()
    val viewModel: FileManagerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "file_manager") {
        composable("file_manager") {
            FileManagerScreen(
                viewModel = viewModel,
                onThemeChange = onThemeChange,
                onModeChange = onModeChange,
                onFileClick = { file ->
                    val encodedPath = URLEncoder.encode(file.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("text_viewer/$encodedPath")
                },
                onImageClick = { file ->
                    val encodedPath = URLEncoder.encode(file.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("image_viewer/$encodedPath")
                },
                onJsonXmlClick = { file ->
                    val encodedPath = URLEncoder.encode(file.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("formatted_viewer/$encodedPath")
                }
            )
        }

        composable(
            route = "text_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
            TextFileViewerScreen(
                filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString()),
                viewModel = viewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable(
            route = "image_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
            ImageViewerScreen(
                filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString()),
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable(
            route = "formatted_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
            FormattedTextViewerScreen(
                filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString()),
                viewModel = viewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}