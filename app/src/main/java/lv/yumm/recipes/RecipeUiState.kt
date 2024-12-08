package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.unitStrings

data class RecipeUiState (
    val id: Long = -1,
    val isLoading: Boolean = false,
    val imageUrl: String = "",
    val uploadLink: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val amountOptionValues: List<String> = listOf("1/2", "1", "2", "3", "4", "5", "6"),
    val msrOptionValues: List<String> = unitStrings,
    val directions: List<String> = emptyList(),
    val difficulty: Float = 0f,
) {
    fun filteredAmountValues(input: String): List<String> {
        return amountOptionValues.filter { it.startsWith(input) }
    }

    fun filteredMsrValues(input: String): List<String> {
        return msrOptionValues.filter { it.startsWith(input) }
    }
}

fun Recipe.toRecipeUiState(): RecipeUiState {
    return RecipeUiState(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        directions = this.directions,
        difficulty = this.complexity.toFloat()
    )
}

data class RecipeCardUiState (
    val id: Long,
    val title: String = "",
    val description: String = "",
    val complexity: Int = -1,
    val type: RecipeType? = null,
    val duration: Long = 0,
    val imageUrl: String = "",
    val isDeleteRevealed: Boolean = false,
    val isEditRevealed: Boolean = false,
)

fun Recipe.toRecipeCardUiState(): RecipeCardUiState {
    return RecipeCardUiState(
        this.id,
        this.title,
        this.description,
        this.complexity,
        this.type,
        this.duration,
        this.imageUrl,
    )
}

fun List<Recipe>.toRecipeCardUiState(): List<RecipeCardUiState> {
    return map { it.toRecipeCardUiState() }
}