package features.browse

import navigation.Screen

object BrowseScreen : Screen {
    private const val BROWSE_ROUTE = "browse"
    override fun route(): String = BROWSE_ROUTE
}
