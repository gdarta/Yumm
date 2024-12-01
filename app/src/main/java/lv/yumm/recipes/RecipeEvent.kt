package lv.yumm.recipes

import lv.yumm.recipes.data.Ingredient
import org.junit.runner.Description

sealed class RecipeEvent {
    class CreateRecipe(val title: String, val description: String = ""): RecipeEvent()
    class AddIngredient(): RecipeEvent()
    class UpdateIngredient(val index: Int, val ingredient: Ingredient): RecipeEvent()
}