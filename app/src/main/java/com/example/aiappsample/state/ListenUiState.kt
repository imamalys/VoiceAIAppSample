package com.example.aiappsample.state

data class ListenUiState(
    val audioPath: String = "",
    val questionText: String = "Listening...",
    val isListening: Boolean = false,
    val showLoading: Boolean = false,
    val answerText: String = "",
)