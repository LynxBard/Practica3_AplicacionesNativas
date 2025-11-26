package com.example.p3_aplicacionesnativas.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattedTextViewerScreen(
    filePath: String,
    viewModel: FileManagerViewModel,
    onNavigateUp: () -> Unit
) {
    val fileContent by viewModel.fileContent.collectAsState()
    val file = File(filePath)
    val isJson = file.extension.equals("json", ignoreCase = true)
    val isXml = file.extension.equals("xml", ignoreCase = true)

    LaunchedEffect(filePath) {
        viewModel.readFileContent(file)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = file.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isJson || isXml) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (isJson) "JSON Formateado" else "XML Formateado",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val formattedContent = remember(fileContent) {
                    when {
                        fileContent == null -> "Cargando..."
                        isJson -> formatJson(fileContent!!)
                        isXml -> formatXml(fileContent!!)
                        else -> fileContent!!
                    }
                }

                Text(
                    text = formattedContent,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .verticalScroll(rememberScrollState()),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun formatJson(jsonString: String): String {
    return try {
        when {
            jsonString.trim().startsWith("{") -> {
                val jsonObject = JSONObject(jsonString)
                jsonObject.toString(2)
            }
            jsonString.trim().startsWith("[") -> {
                val jsonArray = JSONArray(jsonString)
                jsonArray.toString(2)
            }
            else -> jsonString
        }
    } catch (e: Exception) {
        "Error al formatear JSON:\n${e.message}\n\nContenido original:\n$jsonString"
    }
}

private fun formatXml(xmlString: String): String {
    return try {
        val xmlFormatted = StringBuilder()
        var indent = 0
        val lines = xmlString.trim().split(">")

        for (line in lines) {
            if (line.trim().isEmpty()) continue

            val trimmedLine = line.trim()

            // Detectar cierre de etiqueta
            if (trimmedLine.startsWith("</")) {
                indent = maxOf(0, indent - 1)
            }

            // Agregar indentación
            xmlFormatted.append("    ".repeat(indent))
            xmlFormatted.append(trimmedLine)
            xmlFormatted.append(">\n")

            // Detectar apertura de etiqueta (pero no auto-cerradas)
            if (trimmedLine.startsWith("<") &&
                !trimmedLine.startsWith("</") &&
                !trimmedLine.endsWith("/") &&
                !trimmedLine.startsWith("<?") &&
                !trimmedLine.startsWith("<!")) {
                indent++
            }
        }

        xmlFormatted.toString()
    } catch (e: Exception) {
        "Error al formatear XML:\n${e.message}\n\nContenido original:\n$xmlString"
    }
}