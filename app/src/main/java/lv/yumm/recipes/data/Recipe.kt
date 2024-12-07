package lv.yumm.recipes.data

data class Recipe (
    val id: Long,
    val title: String = "",
    val description: String = "",
    val directions: List<String> = emptyList(),
    val complexity: Int = -1,
    val duration: Long = 0, // in milliseconds
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

fun Long.toTimestamp(): String {
    val hours = this / (1000 * 60 * 60)
    val minutes = (this / (1000 * 60)) % 60
    return if (hours > 0 && minutes == 0L) {
        "${hours}h"
    } else if (hours > 0) {
        "${hours}h${minutes}min"
    } else {
        "${minutes}min"
    }
}