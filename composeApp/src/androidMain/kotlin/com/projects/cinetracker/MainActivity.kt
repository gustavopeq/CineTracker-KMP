package com.projects.cinetracker

import MainAppView
import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import auth.platform.AuthCallbackHandler
import auth.platform.RecoveryHandler
import common.util.platform.initHaptics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        initHaptics(this)
        handleAuthCallback(intent)

        setContent {
            MainAppView()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthCallback(intent)
    }

    private fun handleAuthCallback(intent: Intent) {
        val uri = intent.data ?: return
        if (uri.scheme == "com.projects.cinetracker" && uri.host == "auth-callback") {
            val fragment = uri.fragment
            if (fragment != null) {
                val params = fragment.split("&").associate { param ->
                    val parts = param.split("=", limit = 2)
                    parts[0] to (parts.getOrNull(1) ?: "")
                }
                if (params["type"] == "recovery") {
                    val accessToken = params["access_token"]
                    if (accessToken != null) {
                        RecoveryHandler.handleRecoveryCallback(accessToken)
                    }
                } else {
                    AuthCallbackHandler.handleCallback(fragment)
                }
            } else {
                AuthCallbackHandler.handleError("No auth data in callback")
            }
        }
    }
}
