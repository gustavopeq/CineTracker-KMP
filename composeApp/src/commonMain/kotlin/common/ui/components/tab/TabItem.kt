package common.ui.components.tab

import org.jetbrains.compose.resources.StringResource

interface TabItem {
    val tabResId: StringResource?
    val tabName: String?
    var tabIndex: Int
}
