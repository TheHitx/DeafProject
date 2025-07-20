package com.example.deaf.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun MainScreen(
    onEditClick: () -> Unit,
    viewModel: AudioViewModel,
    context: Context,
    onFileSelected: (String) -> Unit
) {
    val livePartial by viewModel.livePartial.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val savedFiles = remember { mutableStateListOf<String>() }


    LaunchedEffect(Unit) {
        savedFiles.clear()
        savedFiles.addAll(viewModel.getSavedTranscriptionFiles(context))
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = if (isRecording) "Grabando..." else "Presiona para grabar",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!isRecording) viewModel.startRecording(context)
                else viewModel.stopRecording(context)
            }
        ) {
            Text(if (isRecording) "Detener" else "Grabar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Transcripción parcial:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(livePartial)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onEditClick) {
            Text("Editar Transcripción")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Archivos guardados:", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(savedFiles) { filename ->
                Text(
                    text = filename,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFileSelected(filename) }
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
