package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import org.junit.runner.Description

sealed class RecipeEvent {
    class CreateRecipe(): RecipeEvent()
    class DeleteRecipe(val id: Long): RecipeEvent()
    class SaveRecipe(): RecipeEvent()
    class SetRecipeToUi(val id: Long): RecipeEvent()
    class AddIngredient(): RecipeEvent()
    class UpdateTitle(val title: String): RecipeEvent()
    class UpdateDescription(val description: String): RecipeEvent()
    class UpdateIngredient(val index: Int, val ingredient: Ingredient): RecipeEvent()
}