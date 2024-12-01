package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
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
import lv.yumm.ui.theme.TopBar
import lv.yumm.ui.theme.YummTheme
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun CreateRecipeScreen(uiState: RecipeUiState, onEvent: (RecipeEvent) -> Unit, navigateToRecipesScreen: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopBar("Create a Recipe") }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ){
            LazyColumn(
                modifier = Modifier.padding(all = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = { Text(text = "Recipe Title") },
                        singleLine = true,
                        colors = recipeTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
                item {
                    TextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = { Text(text = "Recipe Description") },
                        maxLines = 5,
                        colors = recipeTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
                itemsIndexed(uiState.ingredients) { index, ingredient ->
                    IngredientCard(uiState,
                        ingredient,
                        onNameChange = {
                            onEvent(
                                RecipeEvent.UpdateIngredient(
                                    index,
                                    ingredient.copy(name = it)
                                )
                            )
                        },
                        onAmountChange = {
                            onEvent(
                                RecipeEvent.UpdateIngredient(
                                    index,
                                    ingredient.copy(amount = if (it.isNotEmpty()) (it.toFloatOrNull()) else null)
                                )
                            )
                        },
                        onMeasurementChange = {
                            onEvent(
                                RecipeEvent.UpdateIngredient(
                                    index,
                                    ingredient.copy(unit = it)
                                )
                            )
                        })
                }
                item {
                    Button(
                        content = { Text(text = "Add an ingredient") },
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { onEvent(RecipeEvent.AddIngredient()) }
                    )
                }
                item {
                    Button(
                        content = { Text(text = "Save recipe") },
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = {
                            onEvent(RecipeEvent.CreateRecipe(title, description))
                            navigateToRecipesScreen()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCard(
    uiState: RecipeUiState,
    ingredient: Ingredient,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onMeasurementChange: (String) -> Unit
) {
    Column (
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        TextField(
            value = ingredient.name,
            onValueChange = {
                onNameChange(it)
            },
            label = { Text(text = "Ingredient")},
            modifier = Modifier,
            colors = recipeTextFieldColors()
        )
        Row {
            ExposedDropdownMenuFilter(
                input = ingredient.amount?.toString() ?: "",
                label = "Amount",
                filterOptions = { uiState.filteredAmountValues(it) },
                onValueChange = { onAmountChange(it) },
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuFilter(
                input = ingredient.unit,
                label = "Measurement",
                filterOptions = { uiState.filteredMsrValues(it) },
                onValueChange = { onMeasurementChange(it) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuFilter(input: String = "", label: String = "", filterOptions: (String) -> List<String>, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = input,
            onValueChange = {
                onValueChange(it)
                if(filterOptions(input).isNotEmpty()){ expanded = true }
            },
            label = { Text(text = label) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            colors = recipeTextFieldColors()
        )
        if (filterOptions(input).isNotEmpty()){
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filterOptions(input).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.toString()) },
                        onClick = {
                            expanded = false
                            onValueChange(option)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateRecipePreview() {
    YummTheme { CreateRecipeScreen(RecipeUiState(), {}, {}) }
}