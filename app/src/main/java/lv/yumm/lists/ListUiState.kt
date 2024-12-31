package lv.yumm.lists

import com.google.firebase.Timestamp
import lv.yumm.recipes.data.Ingredient

data class ListUiState (
    val isLoading: Boolean = false,

    val id: String = "",
    val title: String = "",
    val updatedAt: Timestamp? = null,
    val list: List<ListItem> = emptyList(),
    val errorList: List<IngredientError> = emptyList()
) {
    val hasError: Boolean = errorList.find { it.nameError || it.amountError || it.unitError } != null
}

data class IngredientError(
    val nameError: Boolean = false,
    val amountError: Boolean = false,
    val unitError: Boolean = false,
)

data class ListItem (
    val checked: Boolean = false,
    val ingredient: Ingredient = Ingredient()
)