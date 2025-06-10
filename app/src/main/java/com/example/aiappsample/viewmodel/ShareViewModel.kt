package com.example.aiappsample.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aiappsample.state.ShareState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareViewModel@Inject constructor(): ViewModel() {
    var state by mutableStateOf(ShareState())
        private set

    fun setAnswerText(text: String) {
        state = state.copy(answerText = text)
    }

    fun setQuestionText(text: String) {
        state = state.copy(questionText = text)
    }

    fun setSessionId(sessionId: String) {
        state = state.copy(sessionId = sessionId)
    }
}