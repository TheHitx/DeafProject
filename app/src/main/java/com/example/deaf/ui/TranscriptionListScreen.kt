package com.example.deaf.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun TranscriptionListScreen(
    context: Context,
    viewModel: AudioViewModel,
    onFileSelected: (String) -> Unit
) {
    val files = remember { viewModel.getSavedTranscriptionFiles(context) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Transcripciones guardadas:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        files.forEach { filename ->
            Button(
                onClick = { onFileSelected(filename) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(filename)
            }
        }
    }
}
