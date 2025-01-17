package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.recipes.IngredientOptions
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.Ingredient
import lv.yumm.ui.theme.recipeTextFieldColors
import timber.log.Timber

@Composable
fun EditIngredientsScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
    ) {
        val hasError = remember(uiState.ingredients) { mutableStateListOf(*uiState.ingredients.map { false }.toTypedArray()) }
        LazyColumn( //todo drag and reorder
            state = listState,
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(uiState.ingredients) { index, ingredient ->
                var isDeleteRevealed by remember { mutableStateOf(false) }
                SwipeableItemWithActions(
                    modifier = Modifier.padding(vertical = 10.dp),
                    shape = RoundedCornerShape(5.dp),
                    isLeftRevealed = isDeleteRevealed,
                    onLeftExpanded = { isDeleteRevealed = true },
                    onLeftCollapsed = { isDeleteRevealed = false },
                    leftAction = {
                        Surface(
                            onClick = {
                                onEvent(RecipeEvent.DeleteIngredient(index))
                                isDeleteRevealed = false
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(80.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                tint = Color.White,
                                contentDescription = "delete item"
                            )
                        }
                    }
                ) {
                    IngredientCard(IngredientOptions(),
                        ingredient,
                        nameError = uiState.ingredientErrorList[index].nameError,
                        amountError = uiState.ingredientErrorList[index].amountError,
                        measurementError = uiState.ingredientErrorList[index].unitError,
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
                                    ingredient.copy(amount = it.toFloatOrNull() ?: 0f)
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
            }
        }
        Button(
            content = { Text(text = "Add an ingredient") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onEvent(RecipeEvent.AddIngredient())
                scope.launch {
                    listState.animateScrollToItem(
                        index = (uiState.ingredients.size - 1).coerceAtLeast(
                            0
                        )
                    )
                }
            }
        )
        Button(
            content = { Text(text = "Save and return") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.ingredientScreenError,
            onClick = {
                onEvent(RecipeEvent.ValidateIngredients())
                navigateBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCard(
    uiState: IngredientOptions,
    ingredient: Ingredient,
    nameError: Boolean,
    amountError: Boolean,
    measurementError: Boolean,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onMeasurementChange: (String) -> Unit,
    name: String = "Ingredient"
) {
    var ingredientField by remember {
        mutableStateOf(
            TextFieldValue(
                text = ingredient.name,
                selection = TextRange(ingredient.name.length)
            )
        )
    }
    var amountField by remember {
        mutableStateOf(
            TextFieldValue(
                text = ingredient.amount.toString(),
                selection = TextRange(ingredient.amount.toString().length)
            )
        )
    }
    var unitField by remember {
        mutableStateOf(
            TextFieldValue(
                text = ingredient.unit,
                selection = TextRange(ingredient.unit.length)
            )
        )
    }
    LaunchedEffect(ingredient) {
        ingredientField = TextFieldValue(
            text = ingredient.name,
            selection = TextRange(ingredient.name.length)
        )
        amountField = TextFieldValue(
            text = if (ingredient.amount > 0f) {
                if (ingredient.amount % ingredient.amount.toInt() == 0f) ingredient.amount.toInt().toString() else ingredient.amount.toString()
            } else "",
            selection = TextRange(ingredient.amount.toString().length)
        )
        unitField = TextFieldValue(
            text = ingredient.unit,
            selection = TextRange(ingredient.unit.length)
        )
    }
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
                label = { Text(text = name) },
                modifier = Modifier.fillMaxWidth(),
                colors = recipeTextFieldColors(),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isError = nameError
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f),
                    isError = amountError
                )
                ExposedDropdownMenuFilter(
                    input = unitField,
                    label = "Unit",
                    filterOptions = { uiState.filteredMsrValues(it) },
                    onValueChange = {
                        unitField = it
                        onMeasurementChange(it.text)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f),
                    isError = measurementError
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    filterOptions: (String) -> List<String>,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
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
            keyboardOptions = keyboardOptions,
            maxLines = 1,
            label = { Text(text = label) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            colors = recipeTextFieldColors(),
            isError = isError
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