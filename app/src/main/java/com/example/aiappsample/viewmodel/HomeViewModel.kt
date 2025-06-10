package com.example.aiappsample.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiappsample.domain.WhisperDomain
import com.example.aiappsample.helper.InjectTextToSpeech
import com.example.aiappsample.repository.WhispersRepository
import com.example.aiappsample.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val whispersRepository: WhispersRepository,
    private val injectTextToSpeech: InjectTextToSpeech,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set
    @SuppressLint("StaticFieldLeak")
    private var handler = Handler(Looper.getMainLooper())
    private var i = 0
    private var helloText = "Hello,\nHow can i help you?"
    var whispersDomain: List<WhisperDomain> = mutableListOf()

    init {
        uiState = uiState.copy(sessionId = "${System.currentTimeMillis()}")

        viewModelScope.launch(Dispatchers.IO) {
            injectTextToSpeech.status.collect({ status->
                if (status) {
                    tts()
                }
            })
        }

        viewModelScope.launch(Dispatchers.IO) {
            whispersRepository.whispers.collect({list->
                if (list != null) {
                    whispersDomain = list
                }
            })
        }
    }

    fun setAnimate() {
        uiState = uiState.copy(
            isAnimate = true
        )
    }

    private fun autoText() {
        handler.postDelayed({
            uiState = uiState.copy(helloText = helloText.substring(0, i))
            if (i < helloText.length) {
                i++
                autoText()
            }
        }, 60)
    }

    private fun tts() {
        injectTextToSpeech.textToSpeech.setSpeechRate(0.8f)
        injectTextToSpeech.textToSpeech.speak(helloText, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
        injectTextToSpeech. textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                i = 0
                autoText()
                uiState = uiState.copy(
                    isSpeech = true,
                    showTool = false
                )
            }

            override fun onDone(utteranceId: String?) {
                uiState = uiState.copy(isSpeech = false)
                handler.postDelayed({
                    uiState = uiState.copy(
                        showTool = true,
                        whispersDomain = whispersDomain
                    )
                }, 800)
            }



            override fun onError(utteranceId: String?) {

            }
        })
    }



    fun resetData() {
        uiState = uiState.copy(
            helloText = "",
            isAnimate = true,
            showTool = true,
            isSpeech = false,
        )
    }
}