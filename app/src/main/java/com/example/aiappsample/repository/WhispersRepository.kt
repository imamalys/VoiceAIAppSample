package com.example.aiappsample.repository

import com.example.aiappsample.database.AppDatabase
import com.example.aiappsample.database.TextGenerateEntity
import com.example.aiappsample.database.TextGenerateEntityResult
import com.example.aiappsample.database.asDomainModel
import com.example.aiappsample.domain.TextGenerateResultDomain
import com.example.aiappsample.domain.WhisperDomain
import com.example.aiappsample.network.WhisperApi
import com.example.aiappsample.network.model.TextGenerateModel
import com.example.aiappsample.network.model.asDatabaseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class WhispersRepository @Inject constructor(
    private val whisperApi: WhisperApi,
    private val appDatabase: AppDatabase
) {

    val whispers: Flow<List<WhisperDomain>?> = appDatabase.voiceAIDao.getWhispers().map { it.asDomainModel() }
    var tableOwnerId = MutableStateFlow(0L)
    val networkError = MutableStateFlow(false)
    var countRetry = 0

    @OptIn(ExperimentalCoroutinesApi::class)
    var textGenerate: Flow<List<TextGenerateResultDomain>> =
        tableOwnerId.flatMapLatest { id->
            appDatabase.voiceAIDao.getTextGenerateWithResultsById(id).map { it?.generateResult?.asDomainModel() ?: emptyList() }
        }

    suspend fun postWhisper(sessionId: String, multipart: MultipartBody.Part) {
        try {
            val whisper = whisperApi.postWhisper(multipart)
            if (whisper.isSuccessful) {
                countRetry = 0
                whisper.body()?.let {
                    appDatabase.voiceAIDao.insertWhisperWithLimit(it.asDatabaseModel(sessionId))
                }
            } else {
                if (countRetry < 3) {
                    countRetry++
                    postWhisper(sessionId, multipart)
                } else {
                    networkError.value = true
                }
            }
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    fun getTableId(sessionId: String): Flow<Long?> {
        return appDatabase.voiceAIDao.getTextGenerateId(sessionId)
    }

    fun getWhispers(sessionId: String): Flow<List<WhisperDomain>?> {
       return appDatabase.voiceAIDao.getWhispers(sessionId).map { it?.asDomainModel() }
    }

    suspend fun generateAnswer(sessionId: String, body: RequestBody) {
        try {
            val answers = whisperApi.generateAnswer(body)
            if (answers.isSuccessful) {
                countRetry = 0
                answers.body()?.let {
                    if (tableOwnerId.value == 0L) {
                        val id = appDatabase.voiceAIDao.insertTextGenerateWithLimit(TextGenerateEntity(sessionId = sessionId))
                        if (id.toInt() != 0) {
                            saveAnswer(id, it)
                        } else {

                        }
                    } else {
                        saveAnswer(tableOwnerId.value, it)
                    }
                }
            } else {
                if (countRetry < 3) {
                    countRetry++
                    generateAnswer(sessionId, body)
                } else {
                    networkError.value = true
                }
            }

        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    private suspend fun saveAnswer(id: Long, answers: TextGenerateModel) {
        if (answers.results.size > 1) {
            for (answer in answers.results) {
                appDatabase.voiceAIDao.insertTextGenerateWithLimitResult(
                    TextGenerateEntityResult(
                        generatedText = answer.generatedText,
                        tableOwnerId = id
                    )
                )
            }
        } else {
            if (answers.results.isNotEmpty()) {
                appDatabase.voiceAIDao.insertTextGenerateWithLimitResult(
                    TextGenerateEntityResult(
                        generatedText = answers.results[0].generatedText,
                        tableOwnerId = id
                    )
                )
                tableOwnerId.value = id
            }
        }
//        getTableId(sessionId).collect( { id ->
//            if (id != null) {
//                if (answers.results.size > 1) {
//                    for (answer in answers.results) {
//                        appDatabase.voiceAIDao.insertTextGenerateWithLimitResult(
//                            TextGenerateEntityResult(
//                                generatedText = answer.generatedText,
//                                tableOwnerId = id
//                            )
//                        )
//                    }
//                } else {
//                    if (answers.results.isNotEmpty()) {
//                        appDatabase.voiceAIDao.insertTextGenerateWithLimitResult(
//                            TextGenerateEntityResult(
//                                generatedText = answers.results[0].generatedText,
//                                tableOwnerId = id
//                            )
//                        )
//                    }
//                }
//            } else {
//
//            }
//        })
    }
}