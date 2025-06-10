package com.example.aiappsample.state

import com.example.aiappsample.domain.WhisperDomain

data class HomeUiState(
    val helloText: String = "",
    val isAnimate: Boolean = false,
    val isSpeech: Boolean = false,
    val showTool: Boolean = false,
    val sessionId: String = "",
    val whispersDomain: List<WhisperDomain> = mutableListOf()
)