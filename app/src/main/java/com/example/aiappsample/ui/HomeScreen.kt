package com.example.aiappsample.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aiappsample.R
import com.example.aiappsample.domain.WhisperDomain
import com.example.aiappsample.interfaces.ShowToolManager
import com.example.aiappsample.ui.component.ToolComponent
import com.example.aiappsample.ui.component.TypingDialog
import com.example.aiappsample.ui.theme.layer2
import com.example.aiappsample.ui.theme.layer3
import com.example.aiappsample.ui.theme.layer4
import com.example.aiappsample.util.ScreenSize
import com.example.aiappsample.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onResult: (
        sessionId: String,
        questionText: String) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState = viewModel.uiState
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.setAnimate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer3)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {

        ShowDescription(
            box = this,
            isAnimate = uiState.isAnimate,
            helloText = uiState.helloText,
            whispersDomain = uiState.whispersDomain
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToolComponent(
                isSpeech = uiState.isSpeech,
                isPlaying = false,
                showTool = uiState.showTool,
                showTogglePlayback = false,
                manager = ShowToolManager(
                    recordAction = {
                        onResult(uiState.sessionId, "")
                    },
                    typeAction = {
                        showDialog = true
                    },
                    toggleAction = {}
                )
            )
        }

        // Display the dialog when showDialog is true
        TypingDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onSave = { text ->
                onResult(uiState.sessionId, text)
            }
        )
    }
}

@Composable
fun ShowDescription(
    box: BoxScope,
    isAnimate: Boolean,
    helloText: String,
    whispersDomain: List<WhisperDomain>) {
    val density = LocalDensity.current
    val visibleItems = remember { mutableStateListOf<Int>() }

    box.apply{
        AnimatedVisibility(
            modifier = Modifier
                .padding(top = ScreenSize.setPercentageVertical(0.1f)),
            visible = isAnimate,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            Column{
                Image(
                    painter = painterResource(id = R.drawable.ic_me), // Replace with your image resource
                    contentDescription = "Me",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = 100f,
                                ),
                            ) {
                                // Slide in from 40 dp from the top.
                                with(density) { -40.dp.roundToPx() }
                            })
                )
                Text(
                    text = helloText,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W500
                    ),
                    modifier = Modifier
                        .padding(top = 10.dp)
                )
                Box(Modifier.padding(top = 40.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(whispersDomain) {index, whisper ->
                        LaunchedEffect(index) {
                            delay((index * 1000).toLong()) // Stagger the delay based on the index
                            visibleItems.add(index) // Mark item as visible
                        }

                        // Animate the item visibility and its position
                        AnimatedVisibility(
                            visible = index in visibleItems, // Only show if the item is marked as visible
                            enter = fadeIn(tween(durationMillis = 1500)) + slideInVertically(initialOffsetY = { -it }),
                            exit = fadeOut(tween(durationMillis = 300)) + slideOutVertically(targetOffsetY = { it })
                        ) {
                            HistoryItem(whisper)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(whisperDomain: WhisperDomain) {
    Card(
        modifier = Modifier
            .fillMaxWidth(), // Make the card fill the available width
        shape = RoundedCornerShape(8.dp), // Rounded corners for the card
    ) {
        Row(
            modifier = Modifier
                .background(layer4)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically // Center vertically
        ) {
            Image(
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(20.dp)),
                painter = painterResource(id = R.drawable.ic_history), // Static image
                contentDescription = "Static Image",
            )
            Text(
                text = whisperDomain.text,
                style = TextStyle(
                    fontSize = 22.sp
                ),
                color = layer2,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer3)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToolComponent(
                isSpeech = false,
                isPlaying = false,
                showTool = true,
                showTogglePlayback = false,
                manager = ShowToolManager(
                    recordAction = {},
                    typeAction = {},
                    toggleAction = {}
                )
            )
        }
    }
}