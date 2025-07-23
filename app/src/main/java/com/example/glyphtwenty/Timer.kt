package com.example.glyphtwenty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun Timer() {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.size(200.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 16.dp,

                )
        }
        Spacer(modifier = Modifier.height(40.dp))

        Row(modifier = Modifier.fillMaxWidth(),Arrangement.SpaceEvenly) {
            val textState = rememberTextFieldState()
            BasicTextField(
                modifier = Modifier

                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiary)

                ,
                state= textState,
                lineLimits = TextFieldLineLimits.SingleLine
            )
            BasicTextField(
                modifier = Modifier
                    .size(100.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiary),
                textStyle = TextStyle.Default.copy(fontSize = 48.sp),

                decorator = { innerTextField ->
                    Box(contentAlignment = Alignment.Center){
                        if(textState.text.isEmpty()){
                            Text(text = "00", fontSize = 48.sp,color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                        } else {
                            innerTextField()
                        }
                    }
                },
                state= textState
            )
            BasicTextField(
                modifier = Modifier

                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiary)

                ,
                state= textState
            )
        }
    }
}