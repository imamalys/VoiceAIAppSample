package com.example.aiappsample.di

import android.content.Context
import com.example.aiappsample.helper.InjectTextToSpeech
import com.example.aiappsample.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    fun provideTTS(@ApplicationContext context: Context): InjectTextToSpeech {
        return InjectTextToSpeech(context)
    }
}