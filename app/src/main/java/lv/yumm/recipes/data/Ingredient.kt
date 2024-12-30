package lv.yumm.recipes.data

data class Ingredient(
    val name: String = "",
    val amount: Float = 0f,
    val unit: String = "",
) {
    companion object{
        const val MILLIGRAM = "mg"
        const val GRAM = "g"
        const val KILOGRAM = "kg"
        const val MILLILITER = "ml"
        const val LITER = "l"
        const val PIECE = "pc"
        const val TBS = "tbs"
        const val TSP = "tsp"
        const val CUP = "cup"
    }
}

fun Ingredient.hasEmpty(): Boolean {
    return name.isBlank() || amount <= 0f || amount.isNaN() || unit.isBlank()
}

fun Ingredient.isEmpty(): Boolean {
    return name.isBlank() && amount <= 0f && amount.isNaN() && unit.isBlank()
}

fun String.isUnit(): String? {
    val validUnits = listOf(
        Ingredient.MILLIGRAM,
        Ingredient.GRAM,
        Ingredient.KILOGRAM,
        Ingredient.MILLILITER,
        Ingredient.LITER,
        Ingredient.PIECE,
        Ingredient.TBS,
        Ingredient.TSP,
        Ingredient.CUP
    )
    return validUnits.firstOrNull { it.equals(this, ignoreCase = true) }
}

val unitStrings: List<String> = listOf(
    Ingredient.MILLIGRAM,
    Ingredient.GRAM,
    Ingredient.KILOGRAM,
    Ingredient.MILLILITER,
    Ingredient.LITER,
    Ingredient.PIECE,
    Ingredient.TBS,
    Ingredient.TSP,
    Ingredient.CUP
)