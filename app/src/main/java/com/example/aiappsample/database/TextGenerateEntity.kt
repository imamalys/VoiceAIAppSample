package com.example.aiappsample.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.aiappsample.domain.TextGenerateResultDomain

@Entity
data class TextGenerateEntity(
    @PrimaryKey(autoGenerate = true)
    val tableId: Int = 0,
    val sessionId: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = TextGenerateEntity::class,
        parentColumns = ["tableId"],
        childColumns = ["tableOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TextGenerateEntityResult(
    @PrimaryKey(autoGenerate = true)
    val tableId: Long = 0,
    val generatedText: String,
    @ColumnInfo(name = "tableOwnerId", index = true)  // Explicitly specify the column name
    val tableOwnerId: Long  // Foreign key
)

data class TextGenerateWithResultEntity(
    @Embedded val generate: TextGenerateEntity,
    @Relation(
        parentColumn = "tableId",
        entityColumn = "tableOwnerId"
    )
    val generateResult: List<TextGenerateEntityResult>
)

fun List<TextGenerateEntityResult>.asDomainModel(): List<TextGenerateResultDomain> {
    return map {
        TextGenerateResultDomain(
            generatedText =  it.generatedText
        )
    }
}

fun TextGenerateEntityResult.asDomainModel(): TextGenerateResultDomain {
    return TextGenerateResultDomain(
        generatedText =  this.generatedText
    )
}