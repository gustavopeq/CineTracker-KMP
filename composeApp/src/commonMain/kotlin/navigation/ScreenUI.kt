package navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

interface ScreenUI {
    @Composable
    fun UI(navController: NavController)
}
