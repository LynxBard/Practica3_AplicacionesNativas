package com.example.p3_aplicacionesnativas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.p3_aplicacionesnativas.viewmodel.FileManagerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFileViewerScreen(
    filePath: String,
    viewModel: FileManagerViewModel,
    onNavigateUp: () -> Unit
) {
    val fileContent by viewModel.fileContent.collectAsState()

    LaunchedEffect(filePath) {
        viewModel.readFileContent(File(filePath))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = File(filePath).name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = fileContent ?: "Loading...")
        }
    }
}