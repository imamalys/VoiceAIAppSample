package com.example.aiappsample.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceAIDao {
    //==================== Whisper ====================
    @Query("select * from WhisperEntity Where sessionId = :sessionId")
    fun getWhispers(sessionId: String): Flow<List<WhisperEntity>?>

    @Query("SELECT * FROM WhisperEntity ORDER BY tableId DESC LIMIT 3")
    fun getWhispers():Flow<List<WhisperEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWhisper(whisper: WhisperEntity)

    @Query("DELETE FROM WhisperEntity WHERE tableId IN (SELECT tableId FROM WhisperEntity ORDER BY tableId ASC LIMIT 1)")
    suspend fun deleteOldestWhisper()

    @Query("SELECT COUNT(*) FROM WhisperEntity")
    suspend fun getCountWhisper(): Int

    @Transaction
    suspend fun insertWhisperWithLimit(whisper: WhisperEntity) {
        if (getCountWhisper() >= 10) {
            deleteOldestWhisper() // Delete the oldest entry if limit is exceeded
        }
        insertWhisper(whisper) // Insert the new data
    }
    //==================== Whisper End ====================

    //==================== Text Generate ====================
    @Query("select tableId from TextGenerateEntity Where sessionId = :sessionId")
    fun getTextGenerateId(sessionId: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTextGenerate(textGenerateEntity: TextGenerateEntity): Long

    @Query("DELETE FROM TextGenerateEntity WHERE tableId IN (SELECT tableId FROM TextGenerateEntity ORDER BY tableId ASC LIMIT 1)")
    suspend fun deleteOldestTextGenerate()

    @Query("SELECT COUNT(*) FROM TextGenerateEntity")
    suspend fun getCountTextGenerate(): Int

    @Transaction
    suspend fun insertTextGenerateWithLimit(textGenerateEntity: TextGenerateEntity): Long {
        if (getCountTextGenerate() >= 10) {
            deleteOldestTextGenerate() // Delete the oldest entry if limit is exceeded
        }
        return insertTextGenerate(textGenerateEntity) // Insert the new data
    }
    //==================== Text Generate End ====================

    //==================== Text Generate Result ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTextGenerateResult(textGenerateEntityResult: TextGenerateEntityResult)

    @Query("DELETE FROM TextGenerateEntityResult WHERE tableId IN (SELECT tableId FROM TextGenerateEntityResult ORDER BY tableId ASC LIMIT 1)")
    suspend fun deleteOldestTextGenerateResult()

    @Query("SELECT COUNT(*) FROM TextGenerateEntityResult")
    suspend fun getCountTextGenerateResult(): Int

    @Transaction
    suspend fun insertTextGenerateWithLimitResult(textGenerateEntityResult: TextGenerateEntityResult)  {
        if (getCountTextGenerateResult() >= 10) {
            deleteOldestTextGenerateResult() // Delete the oldest entry if limit is exceeded
        }
        insertTextGenerateResult(textGenerateEntityResult) // Insert the new data
    }

    //==================== Text Generate With Result ====================
    @Transaction
    @Query("SELECT * FROM TextGenerateEntity")
    fun getTextGenerateWithResults(): Flow<TextGenerateWithResultEntity?>

    @Transaction
    @Query("SELECT * FROM TextGenerateEntity WHERE tableId = :tableId")
    fun getTextGenerateWithResultsById(tableId: Long): Flow<TextGenerateWithResultEntity?>
}

@Database(entities =
    [
        WhisperEntity::class,
        TextGenerateEntity::class,
        TextGenerateEntityResult::class
    ], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val voiceAIDao: VoiceAIDao
}