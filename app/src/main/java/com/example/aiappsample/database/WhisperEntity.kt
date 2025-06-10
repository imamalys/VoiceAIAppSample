package com.example.aiappsample.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aiappsample.domain.WhisperDomain

@Entity
data class WhisperEntity(
    @PrimaryKey(autoGenerate = true)
    val tableId: Int = 0,
    val requestId: String,
    val text: String,
    val sessionId: String,
)


fun List<WhisperEntity>.asDomainModel(): List<WhisperDomain> {
    return map {
        WhisperDomain(
            requestId = it.requestId,
            text = it.text,
        )
    }
}

fun WhisperEntity.asDomainModel(): WhisperDomain {
    return WhisperDomain(
        requestId = this.requestId,
        text = this.text
    )
}