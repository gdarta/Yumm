package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import lv.yumm.R
import lv.yumm.recipes.RecipeCardUiState
import lv.yumm.recipes.RecipeEvent
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun HomeScreen(
    loading: Boolean,
    searchPhrase: String,
    onSearch: (String) -> Unit,
    recipes: List<RecipeCardUiState>,
    navigateToView: (String) -> Unit
) {
    Column(modifier = Modifier.imePadding()) {
        TextField(
            value = searchPhrase,
            onValueChange = {
                onSearch(it)
            },
            placeholder = {
                Text("Search...")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            maxLines = 1,
            colors = recipeTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        if (recipes.isNotEmpty()) {
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
        } else if (!loading) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ){
                Text(
                    text = "Nothing to see here....",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}