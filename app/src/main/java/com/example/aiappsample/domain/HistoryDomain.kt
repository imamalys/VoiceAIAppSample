package com.example.aiappsample.domain

data class HistoryDomain(
    val whisperDomains: List<WhisperDomain>,
    val textGenerateDomains: List<TextGenerateDomain>
)