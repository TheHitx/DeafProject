package com.example.deaf.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun TranscriptionViewerScreen(
    context: Context,
    filename: String,
    viewModel: AudioViewModel
) {
    var content by remember { mutableStateOf("Cargando...") }

    LaunchedEffect(filename) {
        content = viewModel.loadTranscriptionFromFile(context, filename)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Archivo: $filename", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(content)
    }
}
