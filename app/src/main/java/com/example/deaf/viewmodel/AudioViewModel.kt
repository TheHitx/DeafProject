package com.example.deaf.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioViewModel : ViewModel() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var keepListening = false

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription

    fun startRecording(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _transcription.value = "Reconocimiento de voz no disponible en este dispositivo."
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isRecording.value = true
            }

            override fun onBeginningOfSpeech() {
                _transcription.value = ""
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                if (keepListening) {
                    speechRecognizer?.stopListening()
                    speechRecognizer?.cancel()
                    speechRecognizer?.startListening(createRecognizerIntent())
                } else {
                    _isRecording.value = false
                    _transcription.value = "Error en reconocimiento ($error)"
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _transcription.value = matches[0]
                } else {
                    _transcription.value = "No se entendió el audio."
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    _transcription.value = partial[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }

        speechRecognizer?.startListening(recognizerIntent)
    }

    fun stopRecording() {
        fun saveTranscriptionToFile(context: Context): String {
            val filename = "transcripcion_${System.currentTimeMillis()}.txt"
            val fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fileOutput.write(transcription.value.toByteArray())
            fileOutput.close()
            return filename
        }

        speechRecognizer?.stopListening()
        _isRecording.value = false
    }

    private fun releaseRecognizer() {
        speechRecognizer?.destroy()
    }

    override fun onCleared() {
        super.onCleared()
        releaseRecognizer()
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }
    }
}
