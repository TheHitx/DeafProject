package com.example.deaf.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deaf.viewmodel.AudioViewModel

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val audioViewModel: AudioViewModel = viewModel()
    val isRecording by audioViewModel.isRecording.collectAsState()
    val transcription by audioViewModel.transcription.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (isRecording) {
                    audioViewModel.stopRecording()
                    Toast.makeText(context, "Grabación detenida", Toast.LENGTH_SHORT).show()
            } else {
                    audioViewModel.startRecording(context)
                    Toast.makeText(context, "Grabando...", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = if (isRecording) "Detener" else "Grabar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (transcription.isNotBlank()) transcription else "Aquí aparecerá el texto transcrito.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}
