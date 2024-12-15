package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.RecipeType

sealed class RecipeEvent {
    class CreateRecipe(): RecipeEvent()
    class DeleteRecipe(val id: Long): RecipeEvent()
    class SaveRecipe(): RecipeEvent()
    class SetRecipeToUi(val id: Long): RecipeEvent()
    class AddIngredient(): RecipeEvent()
    class UpdateTitle(val title: String): RecipeEvent()
    class UpdateDescription(val description: String): RecipeEvent()
    class UpdateIngredient(val index: Int, val ingredient: Ingredient): RecipeEvent()
    class AddDirection(): RecipeEvent()
    class UpdateDirection(val index: Int, val direction: String): RecipeEvent()
    class UpdateDifficulty(val difficulty: Float): RecipeEvent()
    class UpdateDuration(val duration: List<Long>): RecipeEvent()
    class SetDurationDialog(val open: Boolean): RecipeEvent()
    class UploadPicture(val uri: String): RecipeEvent()
    class UpdateCategory(val type: RecipeType): RecipeEvent()
}