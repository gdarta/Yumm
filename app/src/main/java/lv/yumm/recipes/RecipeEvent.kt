package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import org.junit.runner.Description

sealed class RecipeEvent {
    class CreateRecipe(): RecipeEvent()
    class SaveRecipe(val title: String, val description: String = ""): RecipeEvent()
    class AddIngredient(): RecipeEvent()
    class UpdateIngredient(val index: Int, val ingredient: Ingredient): RecipeEvent()
}