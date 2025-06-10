package com.example.aiappsample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiappsample.R
import com.example.aiappsample.ui.theme.layer2
import com.example.aiappsample.ui.theme.layer3
import com.example.aiappsample.ui.theme.layer4
import com.example.aiappsample.util.ScreenSize

@Composable
fun GreetingScreen(
    onResult: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(layer2)
            .padding(horizontal = ScreenSize.setPercentageHorizontal(0.1f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = ScreenSize.setPercentageVertical(0.4f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_spectrum), // Replace with your image resource
                contentDescription = "Me",
                tint = layer3,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
            )
            Box(Modifier.padding(top = 15.dp))
            Text(
                text = "Welcome to Mindscape",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                color = layer3,
                fontWeight = FontWeight.W800,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.app_desc),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = layer3,
                fontWeight = FontWeight.W400,
            )
            Box(modifier = Modifier
                .padding(top = 40.dp))
            Button(
                onClick = {onResult()},
                modifier = Modifier
                    .width(ScreenSize.setPercentageHorizontal(0.4f))
                    .border(10.dp, layer4, RoundedCornerShape(20.dp)),
                colors = ButtonColors(
                    contentColor = layer4,
                    containerColor = layer4,
                    disabledContainerColor = layer4,
                    disabledContentColor = layer4
                ),
            ){
                Text(
                    text = "Continue",
                    color = layer2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreetingScreen({})
}