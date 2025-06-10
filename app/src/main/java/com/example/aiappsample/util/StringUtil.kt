package com.example.aiappsample.util

import android.os.Handler
import com.example.aiappsample.domain.TextGenerateResultDomain
import com.example.aiappsample.domain.WhisperDomain

object StringUtil {
    fun textGenerateFormat(whispers: List<WhisperDomain>, textGenerateDomains: List<TextGenerateResultDomain>): String {
        var message = ""
        var i = 0
        for (whisper in whispers) {
            if (i == 0) {
                message = String.format("<s>[INST] %s [/INST] ", whisper.text)
            } else {
                message = String.format("%s[INST] %s [/INST] ", message, whisper.text)
            }
            if (i < textGenerateDomains.size) {
                message = String.format("%s %s </s>",message, textGenerateDomains[i].generatedText)
            }
            i++
        }

        return message
    }
}