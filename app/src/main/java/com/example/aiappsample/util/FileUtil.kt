package com.example.aiappsample.util

import java.io.File

object FileUtil {
    fun makeFile(fileName: String, storageDir: File): File {
        // Create the folder if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(fileName, ".m4a", storageDir)
    }
}