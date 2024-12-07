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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.Ingredient
import lv.yumm.ui.theme.BottomNavBar
import lv.yumm.ui.theme.TopBar
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.YummTheme
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun CreateRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateToRecipesScreen: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TextField(
                value = title,
                onValueChange = {
                    title = it
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
                value = description,
                onValueChange = {
                    description = it
                },
                label = { Text(text = "Recipe Description") },
                maxLines = 5,
                colors = recipeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        itemsIndexed(uiState.ingredients) { index, ingredient ->
            IngredientCard(uiState,
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
                            ingredient.copy(amount = it.toFloatOrNull())
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
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(RecipeEvent.AddIngredient()) }
            )
        }
        item {
            Button(
                content = { Text(text = "Save recipe") },
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(RecipeEvent.CreateRecipe(title, description))
                    navigateToRecipesScreen()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCard(
    uiState: RecipeUiState,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onMeasurementChange: (String) -> Unit
) {
    var ingredient by remember { mutableStateOf("") }
    var ingredientField by remember { mutableStateOf(
        TextFieldValue(
            text = ingredient,
            selection = TextRange(ingredient.length)
        )
    ) }
    var amount by remember { mutableStateOf("") }
    var amountField by remember { mutableStateOf(
        TextFieldValue(
            text = amount,
            selection = TextRange(amount.length)
        )
    ) }
    var unit by remember { mutableStateOf("") }
    var unitField by remember { mutableStateOf(
        TextFieldValue(
            text = unit,
            selection = TextRange(unit.length)
        )
    ) }
    Surface(
        shape = RoundedCornerShape(5.dp),
        shadowElevation = 3.dp,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(all = 10.dp)
        ) {
            TextField(
                value = ingredientField,
                onValueChange = {
                    ingredientField = it
                    onNameChange(it.text)
                },
                label = { Text(text = "Ingredient") },
                modifier = Modifier.fillMaxWidth(),
                colors = recipeTextFieldColors()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ExposedDropdownMenuFilter(
                    input = amountField,
                    label = "Amount",
                    filterOptions = { uiState.filteredAmountValues(it) },
                    onValueChange = {
                        amountField = it
                        onAmountChange(it.text)
                    },
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuFilter(
                    input = unitField,
                    label = "Unit",
                    filterOptions = { uiState.filteredMsrValues(it) },
                    onValueChange = {
                        unitField = it
                        onMeasurementChange(it.text)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuFilter(
    input: TextFieldValue,
    label: String = "",
    filterOptions: (String) -> List<String>,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
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
                if (filterOptions(input.text).isNotEmpty()) {
                    expanded = true
                }
            },
            maxLines = 1,
            label = { Text(text = label) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            colors = recipeTextFieldColors()
        )
        if (filterOptions(input.text).isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filterOptions(input.text).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.toString()) },
                        onClick = {
                            expanded = false
                            onValueChange(
                                TextFieldValue(
                                    text = option,
                                    selection = TextRange(option.length)
                                )
                            )
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