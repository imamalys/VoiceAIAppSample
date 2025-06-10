package com.example.aiappsample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aiappsample.ui.component.ListenComponent
import com.example.aiappsample.ui.component.LoadingComponent
import com.example.aiappsample.ui.theme.layer3
import com.example.aiappsample.util.ScreenSize
import com.example.aiappsample.viewmodel.ListenViewModel
import com.example.aiappsample.viewmodel.ShareViewModel
import kotlinx.coroutines.delay

@Composable
fun ListenScreen(
    viewModelShare: ShareViewModel,
    onResult: (String) -> Unit
) {
    val viewModel = hiltViewModel<ListenViewModel>()
    val uiState = viewModel.uiState
    val stateShare = viewModelShare.state

    LaunchedEffect(Unit) {
        viewModel.setSessionId(stateShare.sessionId)
    }

    LaunchedEffect(uiState.answerText) {
        if (uiState.answerText != "") {
            delay(1000)
            onResult(uiState.answerText)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer3)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {
        ListenComponent(
            box = this,
            questionText = uiState.questionText,
            isListening = uiState.isListening
        )

        LoadingComponent(
            box = this,
            showLoading = uiState.showLoading,
            questionText = uiState.questionText,
        )
    }
}