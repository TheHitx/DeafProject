package com.example.deaf.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun TranscriptionEditorScreen(
    context: Context,
    audioViewModel: AudioViewModel,
    filename: String,
    onSaveSuccess: () -> Unit
) {
    var editedText by remember { mutableStateOf("") }

    // Carga el contenido del archivo cuando la pantalla se abre
    LaunchedEffect(filename) {
        editedText = audioViewModel.loadTranscriptionFromFile(context, filename)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Editar transcripción:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = editedText,
            onValueChange = { editedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = Int.MAX_VALUE,
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newFilename = audioViewModel.saveEditedTranscriptionWithDate(context, editedText)
                onSaveSuccess()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }
    }
}
