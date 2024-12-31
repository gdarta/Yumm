package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import lv.yumm.recipes.RecipeCardUiState
import lv.yumm.recipes.RecipeEvent
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun HomeScreen(
    searchPhrase: String,
    onSearch: (String) -> Unit,
    recipes: List<RecipeCardUiState>,
    navigateToView: (String) -> Unit,
    onEvent: (RecipeEvent) -> Unit
) {
    Column() {
        TextField(
            value = searchPhrase,
            onValueChange = {
                onSearch(it)
            },
            label = { Text(text = "Description") },
            maxLines = 5,
            colors = recipeTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            itemsIndexed(items = recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
                RecipeCard(recipe, Modifier) {
                    navigateToView(recipe.id)
                }
            }
        }
    }
}