package common.domain.models.list

import database.model.ListEntity

data class ListItem(
    val id: Int,
    val name: String,
    val isDefault: Boolean = false,
)

fun ListEntity.toListItem(): ListItem {
    return ListItem(
        id = this.listId,
        name = this.listName,
        isDefault = this.isDefault,
    )
}
