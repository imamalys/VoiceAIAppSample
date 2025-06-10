package com.example.aiappsample.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aiappsample.interfaces.ShowToolManager
import com.example.aiappsample.ui.component.NoNetwork
import com.example.aiappsample.ui.component.ToolComponent
import com.example.aiappsample.ui.theme.layer3
import com.example.aiappsample.util.ScreenSize
import com.example.aiappsample.viewmodel.ResultViewModel
import com.example.aiappsample.viewmodel.ShareViewModel

@Composable
fun ResultScreen(viewModelShare: ShareViewModel) {
    val viewModelResult = hiltViewModel<ResultViewModel>()
    val uiStateResult = viewModelResult.uiState
    val stateShare = viewModelShare.state

    // Track the scroll state of the column
    val scrollState = rememberScrollState()

    LaunchedEffect(uiStateResult.displayedText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    LaunchedEffect(uiStateResult.status) {
        if (uiStateResult.status) {
            viewModelResult.setGenerateText(stateShare.answerText)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer3)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {

        if (uiStateResult.isNetworkError) {
            NoNetwork()
        } else {
            ShowResult(
                box = this,
                displayedText = uiStateResult.displayedText,
                isSpeech = uiStateResult.isSpeech,
                isPlaying = uiStateResult.isPlaying,
                scrollState = scrollState
            ) { isSpeak->
                if (isSpeak) {
                    viewModelResult.resumeSpeak()
                } else {
                    viewModelResult.stopSpeak()
                }
            }
        }
    }
}

@Composable
fun ShowResult(
    box: BoxScope,
    displayedText: String,
    isSpeech: Boolean,
    isPlaying: Boolean,
    scrollState: ScrollState,
    onResult: (Boolean) -> Unit) {

    box.apply {
        Column(
            modifier = Modifier,
            Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(top = ScreenSize.setPercentageVertical(0.05f))
                    .fillMaxSize() // Make it take up full vertical space
                    .verticalScroll(scrollState)
                    .weight(1f),
                Arrangement.Center,
                Alignment.CenterHorizontally) {
                Text(
                    text = displayedText,
                    textAlign = TextAlign.Justify,
                    fontSize = 20.sp,
                )
            }
            ToolComponent(
                isSpeech = isSpeech,
                isPlaying = isPlaying,
                showTool = true,
                showTogglePlayback = true,
                manager = ShowToolManager(
                    toggleAction = {isSpeak->
                        onResult(isSpeak)
                    },
                    recordAction = {},
                    typeAction = {}
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinearGradient() {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer3)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {
        ShowResult(
            box = this,
            displayedText = "uiStateResult.displayedText",
            isSpeech = true,
            isPlaying = true,
            scrollState = scrollState,
        ) {

        }
    }
}
