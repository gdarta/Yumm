package lv.yumm.lists.data

import com.google.firebase.Timestamp
import lv.yumm.lists.ListItem
import lv.yumm.lists.ListUiState
import lv.yumm.recipes.data.Ingredient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserList(
    val id: String = "",
    val title: String = "",
    val updatedAt: Timestamp? = null,
    val list: List<ListItem> = emptyList()
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

fun List<UserList>.toUiState(): List<ListUiState> {
    return this.map { it.toUiState() }
}

fun Long.toFormattedDate(): String {
    val outputFormat = SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH)
    return try {
        val date = Date(this * 1000) // Convert seconds to milliseconds
        outputFormat.format(date)
    } catch (e: Exception) {
        // Return a placeholder if conversion fails
        "Invalid Date"
    }
}

