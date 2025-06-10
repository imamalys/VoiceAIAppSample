package com.example.aiappsample.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiappsample.helper.InjectTextToSpeech
import com.example.aiappsample.state.ResultUiState
import com.example.aiappsample.util.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val injectTextToSpeech: InjectTextToSpeech
) : ViewModel() {
    var uiState by mutableStateOf(ResultUiState())
        private set

    private var handler = Handler(Looper.getMainLooper())
    private var i = 0
    private var lastSpeak = ""

    init {
        viewModelScope.launch(Dispatchers.IO) {
            injectTextToSpeech.status.collect({ status ->
                uiState = uiState.copy(status = status)
            })
        }
    }

    fun resumeSpeak() {
        uiState.isPlaying = true
    }

    fun stopSpeak() {
        uiState.isPlaying = false
        injectTextToSpeech.textToSpeech.stop()
    }

    fun setGenerateText(text: String) {
        uiState = uiState.copy(text = text)
    }

    private fun autoText() {
        if (uiState.isPlaying) {
            handler.postDelayed({
                uiState = uiState.copy(displayedText = uiState.text.substring(0, i))
                if (i < uiState.text.length) {
                    i++
                    autoText()
                }
            }, 55)
        }
    }

    private fun tts(context: Context) {
        if (lastSpeak == "") {
            lastSpeak = uiState.text
        }
        injectTextToSpeech.textToSpeech.setSpeechRate(0.9f)

        val fileName = "audio_${System.currentTimeMillis()}"
        val storageDir = File(context.getExternalFilesDir(null), "Audio")
        val cacheFile = FileUtil.makeFile(fileName, storageDir)

        injectTextToSpeech.textToSpeech.synthesizeToFile(uiState.text, null, cacheFile, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)

    }
}