package common.domain.models.list

import database.model.ListEntity

data class ListItem(
    val id: Int,
    val name: String,
)

fun ListEntity.toListItem(): ListItem {
    return ListItem(
        id = this.listId,
        name = this.listName,
    )
}
