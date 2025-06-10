package com.example.aiappsample.helper

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class InjectTextToSpeech @Inject constructor(@ApplicationContext private val context: Context) {
    var status = MutableStateFlow(false)
    val textToSpeech: TextToSpeech = TextToSpeech(context) {success ->
        if (success == TextToSpeech.SUCCESS) {
            status.value = true
        }
    }

    fun get() {}
}