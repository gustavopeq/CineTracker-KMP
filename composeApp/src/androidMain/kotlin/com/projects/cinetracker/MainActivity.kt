package com.projects.cinetracker

import MainAppView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.projects.cinetracker.system.SetStatusBarColor
import common.ui.theme.PrimaryBlackColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SetStatusBarColor(PrimaryBlackColor)
            MainAppView()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MainAppView()
}
