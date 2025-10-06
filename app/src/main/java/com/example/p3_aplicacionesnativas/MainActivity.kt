package com.example.p3_aplicacionesnativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            var currentTheme by remember { mutableStateOf(AppTheme.Default) }

            val theme: @Composable (content: @Composable () -> Unit) -> Unit = when (currentTheme) {
                AppTheme.Guinda -> { content -> GuindaTheme { content() } }
                AppTheme.Azul -> { content -> AzulTheme { content() } }
                else -> { content -> P3_AplicacionesNativasTheme { content() } }
            }

            theme {
                val navController = rememberNavController()
                val viewModel: FileManagerViewModel = viewModel()

                NavHost(navController = navController, startDestination = "file_manager") {
                    composable("file_manager") {
                        FileManagerScreen(
                            viewModel = viewModel,
                            onThemeChange = { newTheme -> currentTheme = newTheme },
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
        }
    }
}
