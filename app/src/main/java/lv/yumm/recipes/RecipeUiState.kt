package lv.yumm.recipes

import androidx.compose.runtime.Immutable
import lv.yumm.lists.IngredientError
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.unitStrings
import lv.yumm.ui.state.ConfirmationDialogUiState

@Immutable
data class RecipeUiState (
    val id: String = "",
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,

    val userName: String? = null,

    val imageUrl: String = "",
    val category: RecipeType? = null,
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val directions: List<String> = emptyList(),
    val difficulty: Float = 0f,
    val duration: Long = 0,
    val portions: Int = 1,

    val ingredientErrorList: List<IngredientError> = emptyList(),

    val triedToSave: Boolean = false,

    val editDurationDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val confirmationDialog: ConfirmationDialogUiState? = null
) {
    val titleError: Boolean = this.title.isBlank() && this.triedToSave
    val categoryError: Boolean = this.category == null && this.triedToSave
    val difficultyError: Boolean = this.difficulty <= 0 && this.triedToSave
    val durationError: Boolean = this.duration <= 0 && this.triedToSave
    val ingredientsEmptyError: Boolean = this.ingredients.isEmpty() && this.triedToSave
    val directionsEmptyError: Boolean = this.directions.isEmpty() && this.triedToSave

    val ingredientScreenError: Boolean = ingredientErrorList.find { it.nameError || it.amountError || it.unitError } != null

    val editScreenHasError: Boolean = (titleError || categoryError || difficultyError || durationError || ingredientsEmptyError || directionsEmptyError)

    val hasOpenDialogs: Boolean = this.editDurationDialog || this.showErrorDialog || this.confirmationDialog != null
}

data class IngredientOptions(
    val amountOptionValues: List<String> = listOf("0.5", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
    val msrOptionValues: List<String> = unitStrings
) {
    fun filteredAmountValues(input: String): List<String> {
        return amountOptionValues.filter { it.startsWith(input) }
    }

    fun filteredMsrValues(input: String): List<String> {
        return msrOptionValues.filter { it.startsWith(input) }
    }
}

fun RecipeUiState.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        directions = this.directions,
        complexity = this.difficulty.toInt(),
        duration = this.duration,
        type = this.category,
        public = this.isPublic,
        portions = this.portions,
        authorName = this.userName
    )
}

fun Recipe.toRecipeUiState(): RecipeUiState {
    return RecipeUiState(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        description = this.description,
        ingredients = this.ingredients,
        directions = this.directions,
        difficulty = this.complexity.toFloat(),
        duration = this.duration,
        category = this.type,
        isPublic = this.public,
        triedToSave = false,
        portions = this.portions,
        userName = this.authorName
    )
}

data class RecipeCardUiState (
    val id: String,
    val title: String = "",
    val description: String = "",
    val difficulty: Int = -1,
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