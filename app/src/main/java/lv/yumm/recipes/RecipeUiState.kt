package lv.yumm.recipes

data class RecipeUiState (
    val isLoading: Boolean = false,
    val uploadLink: String = "",
    val amountOptionValues: List<String> = listOf("1/2", "1", "2", "3", "4", "5", "6"),
    val msrOptionValues: List<String> = listOf("kg", "mg", "gr", "ml", "l")
) {
    fun filteredAmountValues(input: String): List<String> {
        return amountOptionValues.filter { it.startsWith(input) }
    }

    fun filteredMsrValues(input: String): List<String> {
        return msrOptionValues.filter { it.startsWith(input) }
    }
}