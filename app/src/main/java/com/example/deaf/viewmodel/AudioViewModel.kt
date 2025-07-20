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
import java.text.SimpleDateFormat
import java.util.*

class AudioViewModel : ViewModel() {

    val transcribedText = MutableStateFlow("")

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

    private var autosaveFilename: String = "transcripcion_autosave.txt"

    fun startRecording(context: Context) {
        if (isBusy || _isRecording.value) return
        isBusy = true

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _transcriptionList.value += "Reconocimiento de voz no disponible en este dispositivo."
            isBusy = false
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

                    saveLatestTranscriptionLine(context, finalText)
                }

                _livePartial.value = ""

                if (keepListening) {
                    restartListeningIfNeeded()
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

        saveFullTranscriptionToFile(context)

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

    private fun saveLatestTranscriptionLine(context: Context, line: String) {
        val fileOutput = context.openFileOutput(autosaveFilename, Context.MODE_APPEND)
        fileOutput.write((line + "\n").toByteArray())
        fileOutput.close()
    }

    fun saveFullTranscriptionToFile(context: Context): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy HH-mm", Locale.ENGLISH) // Ejemplo: July 07, 2025 14-35
        val dateString = dateFormat.format(Date())
        val filename = "transcripcion_$dateString.txt"
        val fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutput.write(transcriptionList.value.joinToString("\n").toByteArray())
        fileOutput.close()
        return filename
    }

    fun saveEditedTranscription(context: Context, filename: String, newContent: String) {
        try {
            val fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fileOutput.write(newContent.toByteArray())
            fileOutput.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveEditedTranscriptionWithDate(context: Context, newContent: String): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy HH-mm", Locale.ENGLISH)
        val dateString = dateFormat.format(Date())
        val filename = "transcripcion_editada_$dateString.txt"
        try {
            val fileOutput = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fileOutput.write(newContent.toByteArray())
            fileOutput.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return filename
    }

    fun loadTranscriptionFromFile(context: Context, filename: String): String {
        return try {
            val content = context.openFileInput(filename).bufferedReader().use { it.readText() }
            transcribedText.value = content
            content
        } catch (e: Exception) {
            val error = "No se pudo cargar el archivo: ${e.message}"
            transcribedText.value = error
            error
        }
    }

    fun saveTranscription(context: Context, content: String) {
        val filename = "transcription_${System.currentTimeMillis()}.txt"
        val file = context.filesDir.resolve(filename)
        file.writeText(content)
    }

    fun getSavedTranscriptionFiles(context: Context): List<String> {
        val files = context.fileList()
        return files.filter { it.startsWith("transcripcion_") && it.endsWith(".txt") }
    }

    fun updateTranscribedText(newText: String) {
        transcribedText.value = newText
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
