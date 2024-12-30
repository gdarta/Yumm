package lv.yumm.recipes.data

data class Recipe (
    val id: String = "",
    val authorUID: String = "",
    val public: Boolean = false,
    val title: String = "",
    val description: String = "",
    val directions: List<String> = emptyList(),
    val complexity: Int = -1,
    val duration: Long = 0, // in minutes
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
    val days = this / (24 * 60)
    val hours = (this % (24 * 60)) / 60
    val minutes = this % 60

    val result = buildString {
        if (days > 0) append("${days}d")
        if (hours > 0) append("${hours}h")
        if (minutes > 0 || (days == 0L && hours == 0L)) append("${minutes}min")
    }

    return result
}