package features.home

import navigation.Screen

object HomeScreen : Screen {
    private const val HOME_ROUTE = "home"
    override fun route(): String = HOME_ROUTE
}
