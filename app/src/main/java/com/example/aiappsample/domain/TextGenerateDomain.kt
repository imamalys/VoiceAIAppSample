package com.example.aiappsample.domain

data class TextGenerateDomain(
    var results: List<TextGenerateResultDomain>
)

data class TextGenerateResultDomain(
    var generatedText: String
)