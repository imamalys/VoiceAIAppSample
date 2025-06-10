package com.example.aiappsample.audio

import android.content.Context
import android.database.Cursor
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.aiappsample.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class AudioRecorder @Inject constructor(@ApplicationContext private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isSilent = false
    var isRecording = MutableStateFlow(false)
    private val silenceThreshold = 2000.0 // Adjust based on testing
    private val silenceDuration = 3000 // 3 seconds
    private var silenceStartTime: Long = 0
    private var handler = Handler(Looper.getMainLooper())

    fun startRecording(): Uri? {
        // Create content values for the new audio file
        val fileName = "audio_${System.currentTimeMillis()}"
        val storageDir = File(context.getExternalFilesDir(null), "Audio")
        val cacheFile = FileUtil.makeFile(fileName, storageDir)
        val uriFile = FileProvider.getUriForFile(context, "${context.packageName}.provider", cacheFile)

        outputFile = cacheFile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile) // Set the output file to the MediaStore URI
            }
        } else {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile?.absolutePath)
            }
        }

        try {
            recorder?.prepare()
            recorder?.start()
            isRecording.value = true
            recorder
            startSilenceDetection()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // Return the URI for MediaStore, this URI is used to access the recorded audio
        return uriFile
    }

    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
            isRecording.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRealFilePathFromURI(uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                val fileName = getFileName(context, uri) ?: return null
                val cacheDir = context.cacheDir
                val file = File(cacheDir, fileName)

                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    return file.absolutePath // Return the copied file path
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                null
            }
            "file" -> uri.path // Direct file path
            else -> null
        }
    }

    // **Helper function to get file name from URI**
    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }

    // Helper function to get the real file path from a URI
    fun getRealPathFromURI(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun startSilenceDetection() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val amplitude = recorder?.maxAmplitude ?: 0

                if (amplitude < silenceThreshold) {
                    if (silenceStartTime == 0L) {
                        silenceStartTime = System.currentTimeMillis()
                    }
                    val elapsedTime = System.currentTimeMillis() - silenceStartTime
                    if (elapsedTime >= silenceDuration && !isSilent) {
                        isSilent = true
                        onSilenceDetected()
                    }
                } else {
                    silenceStartTime = 0L
                    isSilent = false
                }

                if (!isSilent) {
                    handler.postDelayed(this, 200) // Check every 200ms
                }
            }
        }, 200)
    }

    private fun onSilenceDetected() {
        stopRecording()
    }
}
