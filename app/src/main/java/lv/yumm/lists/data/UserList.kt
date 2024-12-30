package lv.yumm.lists.data

import com.google.firebase.Timestamp
import com.google.type.DateTime
import lv.yumm.lists.ListUiState
import lv.yumm.recipes.data.Ingredient

data class UserList(
    val id: String = "",
    val title: String = "",
    val updatedAt: Timestamp? = null,
    val list: List<Ingredient> = emptyList()
)

fun ListUiState.toUserList(): UserList {
    return UserList(
        id = this.id,
        title = this.title,
        list = this.list,
        updatedAt = this.updatedAt
    )
}

fun UserList.toUiState(): ListUiState {
    return ListUiState(
        id = this.id,
        title = this.title,
        list = this.list,
        updatedAt = this.updatedAt
    )
}

