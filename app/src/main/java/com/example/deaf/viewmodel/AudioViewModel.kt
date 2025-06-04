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

    private var keepListening: Boolean = true
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private var isListening = false
    private var isBusy = false


    private val _livePartial = MutableStateFlow("")
    val livePartial: StateFlow<String> = _livePartial

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _transcriptionList = MutableStateFlow<List<String>>(emptyList())
    val transcriptionList: StateFlow<List<String>> = _transcriptionList

    fun startRecording(context: Context) {
        if (isBusy || _isRecording.value) return
        isBusy = true
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _transcriptionList.value = _transcriptionList.value + "Reconocimiento de voz no disponible en este dispositivo."
            return
        }

        recognizerIntent = createRecognizerIntent()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isRecording.value = true
                isListening = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                restartListeningIfNeeded()
            }

            override fun onError(error: Int) {
                isListening = false
                restartListeningIfNeeded()
                isBusy = false
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val finalText = matches[0]
                    _transcriptionList.value += finalText
                }

                _livePartial.value = ""  // Limpia texto parcial

                if (keepListening) {
                    speechRecognizer?.stopListening()
                    speechRecognizer?.cancel()
                    speechRecognizer?.startListening(createRecognizerIntent())
                } else {
                    _isRecording.value = false
                    isBusy = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    _livePartial.value = partial[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(recognizerIntent)
    }

    fun stopRecording(context: Context) {
        if (isBusy || !_isRecording.value) return
        isBusy = true
        _isRecording.value = false
        isListening = false
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null

        saveTranscriptionToFile(context)

        isBusy = false
    }

    private fun restartListeningIfNeeded() {
        if (_isRecording.value && !isListening) {
            speechRecognizer?.cancel()
            speechRecognizer?.startListening(recognizerIntent)
            isListening = true
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    private fun saveTranscriptionToFile(context: Context): String {
        val filename = "transcripcion_${System.currentTimeMillis()}.txt"
        val fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutput.write(transcriptionList.value.joinToString("\n").toByteArray())
        fileOutput.close()
        return filename
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
