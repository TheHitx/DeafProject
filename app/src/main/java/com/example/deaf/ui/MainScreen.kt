package com.example.deaf.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deaf.viewmodel.AudioViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState


@Composable
fun MainScreen() {
    val context = LocalContext.current
    val audioViewModel: AudioViewModel = viewModel()
    val isRecording by audioViewModel.isRecording.collectAsState()
    val transcriptionList by audioViewModel.transcriptionList.collectAsState()
    val livePartial by audioViewModel.livePartial.collectAsState()
    val transcription = transcriptionList.joinToString(" ")
    val listState = rememberLazyListState()

    val fullText = if (transcription.isBlank() && livePartial.isBlank()) {
        "Aquí aparecerá el texto transcrito."
    } else {
        "$transcription $livePartial"
    }

    LaunchedEffect(fullText) {
        listState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        state = listState,
    ) {
        item {
            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    if (isRecording) {
                        audioViewModel.stopRecording(context)
                        Toast.makeText(context, "Transcripción guardada", Toast.LENGTH_LONG).show()
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
                text = fullText,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
