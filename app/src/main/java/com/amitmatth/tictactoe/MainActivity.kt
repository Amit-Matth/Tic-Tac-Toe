package com.amitmatth.tictactoe

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.amitmatth.tictactoe.ui.theme.TicTacToeTheme

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock the screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {

            val snackbarHostState = remember { SnackbarHostState() }

            TicTacToeTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { innerPadding ->
                   PlaygroundScreen(paddingValues = innerPadding, snackbarHostState = snackbarHostState)
                }
            }
        }
    }
}







