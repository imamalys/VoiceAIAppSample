package com.example.aiappsample.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ScreenSize {

    // Function to get screen padding based on a percentage of screen width and height
    @Composable
    fun getScreenSize(): Pair<Dp, Dp> {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp

        //first width, second height
        return screenWidth.dp to screenHeight.dp
    }

    @Composable
    fun setPercentageHorizontal(percentage: Float): Dp {
        val paddingVertical = (getScreenSize().first * percentage)

        return paddingVertical
    }

    @Composable
    fun setPercentageVertical(percentage: Float): Dp {
        val paddingVertical = (getScreenSize().second * percentage)

        return paddingVertical
    }
}