package common.ui.screen

import navigation.Screen

object ErrorScreen : Screen {
    private const val ERROR_ROUTE = "error"
    override fun route(): String = ERROR_ROUTE
}
