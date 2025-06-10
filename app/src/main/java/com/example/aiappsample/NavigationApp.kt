package com.example.aiappsample

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aiappsample.ui.GreetingScreen
import com.example.aiappsample.ui.HomeScreen
import com.example.aiappsample.ui.ListenScreen
import com.example.aiappsample.ui.ResultScreen
import com.example.aiappsample.ui.TextProcessingScreen
import com.example.aiappsample.viewmodel.ShareViewModel

@Composable
fun NavigationApp() {
    val viewModel = hiltViewModel<ShareViewModel>()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.GREETING
    ) {
        composable(Route.GREETING) {
            GreetingScreen(onResult = {
                navController.navigate(Route.HOME) {
                    popUpTo(Route.GREETING) {
                        inclusive = true
                    }
                }
            })
        }

        composable(Route.HOME) { backStackEntry ->
            HomeScreen(onResult = { sessionId, questionText ->
                if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    viewModel.setSessionId(sessionId)
                    if (questionText != "") {
                        viewModel.setQuestionText(questionText)
                        navController.navigate(Route.TEXT_PROCESSING) {
                            popUpTo(Route.HOME) {
                                inclusive = true
                            }
                        }
                    } else {
                        viewModel.setQuestionText("")
                        navController.navigate(Route.LISTEN) {
                            popUpTo(Route.HOME) {
                                inclusive = true
                            }
                        }
                    }
                }
            })
        }

        composable(Route.TEXT_PROCESSING) {
            TextProcessingScreen(
                viewModelShare =  viewModel
            ) { answer ->
                viewModel.setAnswerText(answer)
                navController.navigate(Route.RESULT) {
                    popUpTo(Route.LISTEN) {
                        inclusive = true
                    }
                }
            }
        }

        composable(Route.LISTEN) {
            ListenScreen(
                viewModelShare = viewModel
            ) { answer ->
                viewModel.setAnswerText(answer)
                navController.navigate(Route.RESULT) {
                    popUpTo(Route.LISTEN) {
                        inclusive = true
                    }
                }
            }
        }

        composable(Route.RESULT) {
            ResultScreen(
                viewModelShare = viewModel
            )
        }
    }
}

object Route {
    const val GREETING = "greeting"
    const val HOME = "home"
    const val LISTEN = "listen"
    const val TEXT_PROCESSING = "text_processing"
    const val RESULT = "result"
}