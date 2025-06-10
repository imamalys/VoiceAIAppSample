package com.example.aiappsample

import android.app.Application
import android.speech.tts.TextToSpeech
import com.example.aiappsample.helper.InjectTextToSpeech
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {
    @Inject
    lateinit var injectTextToSpeech: InjectTextToSpeech

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        injectTextToSpeech.get()
    }
}