package com.example.p3_aplicacionesnativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.p3_aplicacionesnativas.ui.screens.FileManagerScreen
import com.example.p3_aplicacionesnativas.ui.screens.TextFileViewerScreen
import com.example.p3_aplicacionesnativas.ui.theme.AppTheme
import com.example.p3_aplicacionesnativas.ui.theme.AzulTheme
import com.example.p3_aplicacionesnativas.ui.theme.GuindaTheme
import com.example.p3_aplicacionesnativas.ui.theme.P3_AplicacionesNativasTheme
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by rememberSaveable { mutableStateOf(AppTheme.Default) }

            // Aplicar el tema seleccionado
            when (currentTheme) {
                AppTheme.Guinda -> GuindaTheme { AppContent(onThemeChange = { currentTheme = it }) }
                AppTheme.Azul -> AzulTheme { AppContent(onThemeChange = { currentTheme = it }) }
                AppTheme.Default -> P3_AplicacionesNativasTheme { AppContent(onThemeChange = { currentTheme = it }) }
            }
        }
    }
}

@Composable
private fun AppContent(onThemeChange: (AppTheme) -> Unit) {
    val navController = rememberNavController()
    val viewModel: FileManagerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "file_manager") {
        composable("file_manager") {
            FileManagerScreen(
                viewModel = viewModel,
                onThemeChange = onThemeChange,
                onFileClick = {
                    val encodedPath = URLEncoder.encode(it.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("text_viewer/$encodedPath")
                }
            )
        }
        composable(
            route = "text_viewer/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) {
            val filePath = it.arguments?.getString("filePath") ?: ""
            TextFileViewerScreen(
                filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString()),
                viewModel = viewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}