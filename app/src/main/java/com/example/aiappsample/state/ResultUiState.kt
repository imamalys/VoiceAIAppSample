package com.example.aiappsample.state

data class ResultUiState(
    var text: String = "",
    var displayedText: String = "",
    var isProcessing: Boolean = true,
    var isPlaying: Boolean = true,
    var isSpeech: Boolean = true,
    var isNetworkError :Boolean = false,
    var status :Boolean = false
)