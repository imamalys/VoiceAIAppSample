@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.aiappsample.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiappsample.audio.AudioRecorder
import com.example.aiappsample.domain.TextGenerateResultDomain
import com.example.aiappsample.domain.WhisperDomain
import com.example.aiappsample.repository.WhispersRepository
import com.example.aiappsample.state.ListenUiState
import com.example.aiappsample.util.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ListenViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val whispersRepository: WhispersRepository
) : ViewModel() {
    var uiState by mutableStateOf(ListenUiState())
        private set
    private var audioUri: Uri? = null
    private var sessionId = MutableStateFlow("")
    var listWhisper: List<WhisperDomain> = mutableListOf()
    var listGenearate: List<TextGenerateResultDomain> = mutableListOf()

    init {
        // Collect audioRecorder's isRecording and update uiState
        viewModelScope.launch(Dispatchers.IO) {
            audioRecorder.isRecording.collect { isRecording ->
                if (!isRecording && audioUri != null) {
                    val audioFie = getAudioPath()
                    if (audioFie != null) {
                        uiState = uiState.copy(
                            questionText = "",
                            isListening = false,
                            showLoading = true
                        )
                        postWhisper(audioFie)
                    } else {

                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            sessionId.flatMapLatest { id->
                whispersRepository.getWhispers(id).map {  whisper ->
                    if (whisper != null) {
                        listWhisper = whisper
                    }

                    if (listWhisper.isNotEmpty()) {
                        uiState = uiState.copy(questionText = listWhisper.last().text)
                        generateAnswer(listWhisper, listGenearate)
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            sessionId.flatMapLatest { id->
                whispersRepository.getTableId(id).map {  id ->
                    if (id != null) {
                        whispersRepository.tableOwnerId.value = id
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            whispersRepository.textGenerate.collect({generate ->
                if (generate.isNotEmpty()) {
                    listGenearate = generate
                    if (uiState.showLoading) {
                        uiState = uiState.copy(
                            answerText = listGenearate.last().generatedText
                        )
                    }
                }
            })
        }
    }

    fun setSessionId(sessionId: String) {
        this.sessionId.value = sessionId
        recordAudio()
    }

    private fun getAudioPath(): String? {
        return audioUri?.let { audioRecorder.getRealFilePathFromURI(it) }
    }

    private fun recordAudio() {
        viewModelScope.launch {
            audioUri = audioRecorder.startRecording()
            uiState = uiState.copy(
                isListening = true
            )
        }
    }

    private suspend fun postWhisper(audioPath: String) {
        val filePart = prepareFilePart(getAudioFile(audioPath))
        if (filePart != null) {
            withContext(Dispatchers.IO) {
//                whispersRepository.postWhisper(sessionId, filePart)
            }
        } else {

        }
    }

    private suspend fun generateAnswer(listWhisper: List<WhisperDomain>, listGenearate: List<TextGenerateResultDomain>) {
        val input = StringUtil.textGenerateFormat(listWhisper, listGenearate)
        val json = JSONObject()
        json.put("input", input)
        json.put("stop", JSONArray().put("</s>"))

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        withContext(Dispatchers.IO) {
            delay(1000)
            whispersRepository.generateAnswer(sessionId.value, requestBody)
        }
    }

    private fun getAudioFile(audioPath: String): File {
        return File(audioPath)
    }

    private fun prepareFilePart(file: File): MultipartBody.Part? {
        if (!file.exists()) {
            return null // Handle file not found
        }

        // Set the media type (MP4)
        val requestFile = file.asRequestBody("audio/mp4".toMediaType())

        // Create MultipartBody.Part
        return MultipartBody.Part.createFormData("audio", file.name, requestFile)
    }
}