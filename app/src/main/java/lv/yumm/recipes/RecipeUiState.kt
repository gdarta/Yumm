package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.unitStrings

data class RecipeUiState (
    val isLoading: Boolean = false,
    val uploadLink: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = listOf(Ingredient()),
    val amountOptionValues: List<String> = listOf("1/2", "1", "2", "3", "4", "5", "6"),
    val msrOptionValues: List<String> = unitStrings
) {
    fun filteredAmountValues(input: String): List<String> {
        return amountOptionValues.filter { it.startsWith(input) }
    }

    fun filteredMsrValues(input: String): List<String> {
        return msrOptionValues.filter { it.startsWith(input) }
    }
}