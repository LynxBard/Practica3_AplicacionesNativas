package com.example.p3_aplicacionesnativas.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    filePath: String,
    onNavigateUp: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar la imagen
    LaunchedEffect(filePath) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    imageBitmap = bitmap.asImageBitmap()
                    isLoading = false
                } else {
                    errorMessage = "No se pudo decodificar la imagen"
                    isLoading = false
                }
            } else {
                errorMessage = "El archivo no existe"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar la imagen: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = File(filePath).name,
                        maxLines = 1
                    )
                },
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
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            scale = (scale - 0.5f).coerceAtLeast(0.5f)
                        }
                    ) {
                        Icon(Icons.Filled.ZoomOut, "Alejar")
                    }

                    IconButton(
                        onClick = {
                            scale = 1f
                            rotation = 0f
                            offset = Offset.Zero
                        }
                    ) {
                        Text("Reset", style = MaterialTheme.typography.labelLarge)
                    }

                    IconButton(
                        onClick = {
                            scale = (scale + 0.5f).coerceAtMost(5f)
                        }
                    ) {
                        Icon(Icons.Filled.ZoomIn, "Acercar")
                    }

                    IconButton(
                        onClick = { rotation -= 90f }
                    ) {
                        Icon(Icons.Filled.RotateLeft, "Rotar izquierda")
                    }

                    IconButton(
                        onClick = { rotation += 90f }
                    ) {
                        Icon(Icons.Filled.RotateRight, "Rotar derecha")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                imageBitmap != null -> {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Imagen",
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, rotate ->
                                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                                    rotation += rotate
                                    offset += pan
                                }
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}