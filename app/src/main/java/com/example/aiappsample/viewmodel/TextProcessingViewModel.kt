package com.example.aiappsample.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiappsample.domain.TextGenerateResultDomain
import com.example.aiappsample.domain.WhisperDomain
import com.example.aiappsample.repository.WhispersRepository
import com.example.aiappsample.state.TextProcessingState
import com.example.aiappsample.util.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TextProcessingViewModel @Inject constructor(
    private val whispersRepository: WhispersRepository
) : ViewModel() {
    var uiState by mutableStateOf(TextProcessingState())
        private set
    private var sessionId = ""
    private var listWhisper: MutableList<WhisperDomain> = mutableListOf()
    private var listAnswer: MutableList<TextGenerateResultDomain> = mutableListOf()
    private var isResultWhisper = false
    private var isResultAnswer = false
    private var isProcessing = false

    fun setSessionId(sessionId: String) {
        this.sessionId = sessionId
        viewModelScope.launch(Dispatchers.IO) {
            whispersRepository.getWhispers(sessionId).collect {  whisper ->
                if (whisper != null) {
                    listWhisper = whisper.toMutableList()
                }
                isResultWhisper = true
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            whispersRepository.getTableId(sessionId).collect {  id ->
                if (id != null) {
                    whispersRepository.tableOwnerId.value = id
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            whispersRepository.textGenerate.collect({generate ->
                if (generate.isNotEmpty()) {
                    listAnswer = generate.toMutableList()
                    if (isProcessing) {
                        isProcessing = false
                        uiState = uiState.copy(
                            answerText = listAnswer.last().generatedText
                        )
                    }
                }
                isResultAnswer = true
            })
        }
    }

    fun setQuestionText(text: String) {
        uiState = uiState.copy(questionText = text)
        if (isResultAnswer && isResultWhisper) {
            listWhisper.add(WhisperDomain(requestId = "", text = text))
            viewModelScope.launch(Dispatchers.IO) {
                generateAnswer(listWhisper, listAnswer)
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                setQuestionText(text)
            }
        }
    }

    private suspend fun generateAnswer(listWhisper: MutableList<WhisperDomain>, listAnswer: MutableList<TextGenerateResultDomain>) {
        val input = StringUtil.textGenerateFormat(listWhisper, listAnswer)
        val json = JSONObject()
        json.put("input", input)
        json.put("stop", JSONArray().put("</s>"))

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        withContext(Dispatchers.IO) {
            delay(1000)
            isProcessing = true
            whispersRepository.generateAnswer(sessionId, requestBody)
        }
    }
}