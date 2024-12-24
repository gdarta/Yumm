package lv.yumm.recipes

import android.net.Uri
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.RecipeType

sealed class RecipeEvent {
    class CreateRecipe(): RecipeEvent()
    class DeleteRecipe(val id: String): RecipeEvent()
    class SaveRecipe(val navigateBack: () -> Unit): RecipeEvent()
    class SetRecipeToUi(val id: String): RecipeEvent()
    class AddIngredient(): RecipeEvent()
    class UpdateTitle(val title: String): RecipeEvent()
    class UpdateDescription(val description: String): RecipeEvent()
    class UpdateIngredient(val index: Int, val ingredient: Ingredient): RecipeEvent()
    class DeleteIngredient(val index: Int): RecipeEvent()
    class AddDirection(): RecipeEvent()
    class UpdateDirection(val index: Int, val direction: String): RecipeEvent()
    class DeleteDirection(val index: Int): RecipeEvent()
    class UpdateDifficulty(val difficulty: Float): RecipeEvent()
    class UpdateDuration(val duration: List<Long>): RecipeEvent()
    class UploadPicture(val uri: Uri?): RecipeEvent()
    class UpdateCategory(val type: RecipeType): RecipeEvent()

    class SetDurationDialog(val open: Boolean): RecipeEvent()
    class SetErrorDialog(val open: Boolean): RecipeEvent()

    class HandleBackPressed(val navigateBack: () -> Unit): RecipeEvent()

    class OnCardClicked(val id: String): RecipeEvent()
    class OnDeleteRevealed(val id: String): RecipeEvent()
    class OnEditRevealed(val id: String): RecipeEvent()
    class OnDeleteCollapsed(val id: String): RecipeEvent()
    class OnEditCollapsed(val id: String): RecipeEvent()
}