package com.example.aiappsample.network.model

import com.example.aiappsample.database.TextGenerateEntityResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextGenerateModel(
    @Json(name = "results")
    var results: List<TextGenerateResultModel>
)

@JsonClass(generateAdapter = true)
data class TextGenerateResultModel(
    @Json(name = "generated_text")
    var generatedText: String
)

fun TextGenerateResultModel.asDatabaseModel(foreignKey: Long): TextGenerateEntityResult {
    return TextGenerateEntityResult(
        generatedText = this.generatedText,
        tableOwnerId =  foreignKey
    )
}