package com.example.aiappsample.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AudioPlayer @Inject constructor(@ApplicationContext private val context: Context) {
    var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    lateinit var mediaItem: MediaItem

    fun setUri(uri: String) {
        mediaItem = MediaItem.fromUri(uri) // Change file path as needed
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun start() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun onRelease() {
        exoPlayer.release()
    }
}