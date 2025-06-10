package com.example.aiappsample.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aiappsample.R
import com.example.aiappsample.util.ScreenSize

@Composable
fun LoadingComponent(
    box: BoxScope,
    showLoading: Boolean,
    questionText: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.progress_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // This will make the animation loop forever
    )
    val alphaTime by animateFloatAsState(
        targetValue = if (questionText == "") 0f else 1f,
        animationSpec = tween(durationMillis = 1000) // 1 sec fade-out
    )
    box.apply{
        AnimatedVisibility(
            visible = showLoading,
            modifier = Modifier
                .align(Alignment.Center),
            enter = scaleIn(animationSpec = tween(1000)),
            exit = scaleOut(animationSpec = tween(1000))
        ) {
            Column {
                LottieAnimation(
                    modifier = Modifier
                        .size(ScreenSize.setPercentageHorizontal(0.7f)),
                    composition = composition,
                    progress = { progress }
                )
                Text(
                    text = questionText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                        .alpha(alphaTime)
                )
            }
        }
    }
}