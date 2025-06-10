package com.example.aiappsample.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow

object PermissionUtil {
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun checkAudioPermission(): MutableStateFlow<Boolean> {
        var isGranted = MutableStateFlow(false)
        val context = LocalContext.current

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                isGranted.value = permissions.values.all { it }
            }
        )

        // Initialize the ActivityResultLauncher
        val manageExternalStoragePermissionLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            isGranted.value = true
                        } else {
                            isGranted.value = false
                        }
                    } else {
                        isGranted.value = true
                    }
                }

        LaunchedEffect(true) {
            val permissionsNeeded = mutableListOf<String>()

            // Check if permissions are granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }

            // If permissions are needed, request them
            if (permissionsNeeded.isNotEmpty()) {
                requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
            } else {
                // For Android 10 (API 29) and above, request the necessary permissions for scoped storage.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        isGranted.value = true
                    } else {
                        // Request permission to manage external storage
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.setData(String.format("package:%s", context.packageName).toUri())
                        manageExternalStoragePermissionLauncher.launch(intent)
                    }
                } else {
                    isGranted.value = true
                }
            }
        }
        return isGranted
    }
}