package com.example.deaf.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun TranscriptionEditorScreen(
    context: Context,
    viewModel: AudioViewModel,
    filename: String = "transcripcion_autosave.txt" // cambia si usas otro nombre
) {
    var text by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    if (!isLoaded) {
        text = viewModel.loadTranscriptionFromFile(context, filename)
        isLoaded = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Edita la transcripción:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = Int.MAX_VALUE
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveEditedTranscription(context, filename, text)
        }) {
            Text("Guardar cambios")
        }
    }
}
