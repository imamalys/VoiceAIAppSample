package com.example.aiappsample.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aiappsample.R
import com.example.aiappsample.interfaces.ShowToolManager
import com.example.aiappsample.util.ScreenSize

@Composable
fun ToolComponent(
    isSpeech: Boolean,
    isPlaying: Boolean,
    showTool: Boolean,
    showTogglePlayback: Boolean,
    manager: ShowToolManager
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.speech_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying
    )
    val alphaToggle by animateFloatAsState(
        targetValue = if (showTogglePlayback && isSpeech) 1f else 0f,
        animationSpec = tween(durationMillis = 1000) // 1 sec fade-out
    )

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .padding(top = ScreenSize.setPercentageVertical(0.05f)),
            visible = isSpeech,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800))
        ) {
            LottieAnimation(
                modifier = Modifier
                    .height(ScreenSize.setPercentageVertical(0.15f))
                    .graphicsLayer(
                        scaleX = 2f, // Scale X
                        scaleY = 2f  // Scale Y
                    ),
                composition = composition,
                progress = { progress }
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .padding(top = ScreenSize.setPercentageVertical(0.02f)),
            visible = showTool,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)),
            exit = fadeOut(animationSpec = tween(durationMillis = 800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = ScreenSize.setPercentageVertical(0.05f),
                        start = ScreenSize.setPercentageHorizontal(0.05f),
                        end = ScreenSize.setPercentageHorizontal(0.05f)
                    ),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Crossfade(targetState = isPlaying) { playing ->
                    Image(
                        painter = painterResource(
                            id = if (playing) R.drawable.ic_pause else R.drawable.ic_play
                        ), // Static image
                        contentDescription = "Pause",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .alpha(alphaToggle)
                            .clickable {
                                manager.onTogglePlayback(!isPlaying)
                            }
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_record), // Static image
                    contentDescription = "Record",
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .clickable {
                            if (!isSpeech) {
                                manager.onRecord()
                            }
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_keyboard), // Static image
                    contentDescription = "Keyboard",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .clickable {
                            manager.onType()
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolPreview() {
    ToolComponent(
        isSpeech = true,
        isPlaying = true,
        showTool = true,
        showTogglePlayback = true,
        manager = ShowToolManager(
            recordAction = {},
            typeAction = {},
            toggleAction = {}
        ))
}
