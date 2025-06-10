package com.example.aiappsample.di

import android.content.Context
import androidx.room.Room
import com.example.aiappsample.database.AppDatabase
import com.example.aiappsample.database.VoiceAIDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "VoiceAI"
        )
            .addMigrations(migrations = arrayOf())
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): VoiceAIDao {
        return appDatabase.voiceAIDao
    }
}