package lv.yumm.recipes

data class RecipeUiState (
    val uploadLink: String = "",
    val amountOptionValues: List<String> = emptyList()
) {
    fun filteredAmountValues(input: String): List<String> {
        return amountOptionValues.filter { it.startsWith(input) }
    }
}