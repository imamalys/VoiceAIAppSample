package com.example.aiappsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.aiappsample.network.NetworkMonitor
import com.example.aiappsample.ui.component.NoNetwork
import com.example.aiappsample.ui.theme.VoiceAIAppSampleTheme
import com.example.aiappsample.ui.theme.layer1
import com.example.aiappsample.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(layer1.toArgb())
        )
        setContent {
            VoiceAIAppSampleTheme {
                MyApp(networkMonitor)
            }
        }
    }
}

@Composable
fun MyApp(networkMonitor: NetworkMonitor) {
    val isConnected by networkMonitor.isConnected.collectAsState()

    // Request permissions when the screen is created
    PermissionUtil.checkAudioPermission()

    if (isConnected) {
        NavigationApp()
    } else {
        NoNetwork()
    }
}