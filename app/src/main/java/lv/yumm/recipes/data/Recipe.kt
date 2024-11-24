package lv.yumm.recipes.data

data class Recipe (
    val id: String,
    val title: String = "",
    val description: String = "",
    val directions: List<String> = emptyList(),
    val complexity: Int = -1,
    val duration: Int = 0, // in milliseconds
    val portions: Int = 0,
    val imageUrl: String = "",
    val type: RecipeType? = null,
    val ingredients: List<Ingredient> = emptyList<Ingredient>(),
)

enum class RecipeType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SALAD,
    DESSERT,
    SNACK
}