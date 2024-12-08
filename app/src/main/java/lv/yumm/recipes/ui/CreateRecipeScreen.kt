package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.Ingredient
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.YummTheme
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun CreateRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateToRecipesScreen: () -> Unit,
    navigateToEditIngredientsScreen: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TextField(
                value = uiState.title,
                onValueChange = {
                    onEvent(RecipeEvent.UpdateTitle(it))
                },
                textStyle = Typography.titleLarge,
                label = { Text(text = "Recipe Title") },
                singleLine = true,
                colors = recipeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TextField(
                value = uiState.description,
                onValueChange = {
                    onEvent(RecipeEvent.UpdateDescription(it))
                },
                label = { Text(text = "Recipe Description") },
                maxLines = 5,
                colors = recipeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                content = { Text(text = "Edit ingredients") },
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = { navigateToEditIngredientsScreen(uiState.id) }
            )
        }
        itemsIndexed(uiState.ingredients) { _, ingredient ->
            IngredientText(ingredient)
        }
        item {
            Button(
                content = { Text(text = "Save recipe") },
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(RecipeEvent.SaveRecipe())
                    navigateToRecipesScreen()
                }
            )
        }
    }
}

@Composable
fun IngredientText(ingredient: Ingredient) {
    Text(
        text = "${ingredient.name}, ${ingredient.amount} ${ingredient.unit}",
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Preview
@Composable
fun CreateRecipePreview() {
    YummTheme { CreateRecipeScreen(RecipeUiState(), {}, {}, {}) }
}