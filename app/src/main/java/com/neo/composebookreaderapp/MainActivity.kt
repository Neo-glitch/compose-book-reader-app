package com.neo.composebookreaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neo.composebookreaderapp.navigation.ReaderNavigation
import com.neo.composebookreaderapp.ui.theme.ComposeBookReaderAppTheme
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBookReaderAppTheme {
                ReaderApp()
            }
        }
    }
}
@ExperimentalComposeUiApi
@Composable
fun ReaderApp(){
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 46.dp)) {
        
        Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            ReaderNavigation()

        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeBookReaderAppTheme {

    }
}