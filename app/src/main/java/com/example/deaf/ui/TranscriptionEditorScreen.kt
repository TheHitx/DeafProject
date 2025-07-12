package com.example.deaf.ui

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun TranscriptionEditorScreen(
    filename: String,
    audioViewModel: AudioViewModel = viewModel(),
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(filename) {
        audioViewModel.loadTranscriptionFromFile(context, filename)
    }

    val transcribedText by audioViewModel.transcribedText.collectAsState()

    var editedText by remember(transcribedText) { mutableStateOf(transcribedText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Editar Transcripción",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = editedText,
            onValueChange = {
                editedText = it
                audioViewModel.updateTranscribedText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            placeholder = { Text("Transcripción aquí...") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                audioViewModel.saveEditedTranscription(context, filename, editedText)
                onSaveSuccess()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }
    }
}
