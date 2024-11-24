package lv.yumm.recipes.data

data class Amount (
    val amount: Float = 0f,
    val unit: Unit,
)

//todo: add custom units?
enum class Unit {
    MILLIGRAM,
    GRAM,
    KILOGRAM,
    MILLILITER,
    LITER,
    PIECE,
    TBS,
    TSP,
    CUP,
}
