package lv.yumm.recipes

sealed class RecipeEvent {
    class CreateRecipe(): RecipeEvent()
}