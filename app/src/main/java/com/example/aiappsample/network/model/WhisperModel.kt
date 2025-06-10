package com.example.aiappsample.network.model

import com.example.aiappsample.database.WhisperEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WhisperModel(
    @Json(name = "text")
    var text: String,
    @Json(name = "request_id")
    var requestId: String
)

fun WhisperModel.asDatabaseModel(sessionId: String): WhisperEntity {
    return WhisperEntity(
        text = this.text,
        requestId = this.requestId,
        sessionId = sessionId
    )
}