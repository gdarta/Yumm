package lv.yumm.lists

import com.google.firebase.Timestamp
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.hasEmpty
import lv.yumm.recipes.data.isEmpty

data class ListUiState (
    val id: String = "",
    val title: String = "",
    val updatedAt: Timestamp? = null,
    val list: List<Ingredient> = emptyList(),
    val errorList: List<ItemError> = emptyList()
) {
    val hasError: Boolean = errorList.find { it.nameError || it.amountError || it.unitError } != null
}

data class ItemError(
    val nameError: Boolean = false,
    val amountError: Boolean = false,
    val unitError: Boolean = false,
)