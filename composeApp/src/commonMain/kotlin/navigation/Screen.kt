package navigation

import androidx.navigation.NamedNavArgument

interface Screen {
    fun route(): String
    val arguments: List<NamedNavArgument>
        get() = emptyList()
}
