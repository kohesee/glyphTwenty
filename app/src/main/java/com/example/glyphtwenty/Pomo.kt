package com.example.glyphtwenty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
private fun PomoScreen() {
    var isButton by remember{mutableStateOf(false)}
    val time = 20*60*1000L
    var currentTime by remember { mutableLongStateOf(time) }

    LaunchedEffect(key1 = isButton) {
        if(isButton){
            while(currentTime>0){
                delay(1000L)
                currentTime -= 1000L
            }
            isButton = false
        }
    }
    val progress = if (time>0) currentTime.toFloat() / time.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "${currentTime.toInt()/1000L}", textAlign = TextAlign.Center, color = Color.White, fontSize = 30.sp)
        Spacer(Modifier.height(40.dp))
        Box(){
            CircularProgressIndicator(
                progress = {
                    progress
                },
                Modifier.size(200.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 16.dp,
            )
        }
        Spacer(Modifier.height(40.dp))
        Button(onClick = {isButton = !isButton
        if (!isButton){
            currentTime = time
        }}) {
            Text(if (isButton)"Stop" else "Start")
        }
    }
}