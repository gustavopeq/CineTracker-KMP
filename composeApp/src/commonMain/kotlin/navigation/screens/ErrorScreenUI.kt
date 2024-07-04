package navigation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import common.ui.screen.ErrorScreen
import navigation.ScreenUI

class ErrorScreenUI : ScreenUI {
    @Composable
    override fun UI(navController: NavController) {
        ErrorScreen(
            onTryAgain = {
                navController.popBackStack()
            },
        )
    }
}
