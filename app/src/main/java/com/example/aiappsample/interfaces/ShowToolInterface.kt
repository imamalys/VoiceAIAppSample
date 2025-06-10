package com.example.aiappsample.interfaces

interface ShowToolInterface {
    fun onRecord()
    fun onType()
    fun onTogglePlayback(isPlaying: Boolean)
}

class ShowToolManager(
    val recordAction: () -> Unit,
    val typeAction: () -> Unit,
    val toggleAction: (Boolean) -> Unit,
): ShowToolInterface {
    override fun onRecord() {
        recordAction()
    }

    override fun onType() {
        typeAction()
    }

    override fun onTogglePlayback(isPlaying: Boolean) {
        toggleAction(isPlaying)
    }
}