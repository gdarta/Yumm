package lv.yumm.recipes.ui

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.Ingredient
import lv.yumm.ui.theme.RatingBar
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.YummTheme
import lv.yumm.ui.theme.recipeTextFieldColors
import timber.log.Timber

@Composable
fun CreateRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateToRecipesScreen: () -> Unit,
    navigateToEditIngredientsScreen: (Long) -> Unit,
    navigateToEditDirectionsScreen: (Long) -> Unit,
) {
    var difficulty by remember { mutableFloatStateOf(0f) }
    val diffColor = MaterialTheme.colorScheme.tertiaryContainer
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
            EditRow {
                Text(
                    text = "Difficulty:  ${uiState.difficulty.toInt()}",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                RatingBar(
                    rating = uiState.difficulty,
                    ratingStep = 1f,
                    starSize = 50.dp,
                    unratedContent = {
                        Icon(
                            modifier = Modifier.alpha(.2f),
                            painter = painterResource(R.drawable.ic_cookie_outlined),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = null
                        )
                    },
                    ratedContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_cookie_outlined),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = null
                        )
                    },
                    onRatingChanged = {
                        difficulty = it
                        onEvent(RecipeEvent.UpdateDifficulty(it))
                    }
                )
            }
        }
        item {
            DurationPicker(uiState.duration) { onEvent(RecipeEvent.UpdateDuration(it)) }
        }
        item {
            EditRow {
                Text(
                    text = "Ingredients:",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    content = { Text(text = "Edit ingredients") },
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { navigateToEditIngredientsScreen(uiState.id) }
                )
            }
        }
        itemsIndexed(uiState.ingredients) { _, ingredient ->
            IngredientText(ingredient)
        }
        item {
            EditRow {
                Text(
                    text = "Directions:",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    content = { Text(text = "Edit directions", modifier = Modifier) },
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { navigateToEditDirectionsScreen(uiState.id) }
                )
            }
        }
        itemsIndexed(uiState.directions) { index, direction ->
            Text(
                text = "${index + 1}. $direction",
                color = MaterialTheme.colorScheme.onBackground
            )
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
fun EditRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }
}

@Composable
fun IngredientText(ingredient: Ingredient) {
    Text(
        text = "${ingredient.name}, ${ingredient.amount} ${ingredient.unit}",
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun DurationPicker(initialDuration: Long, setTime: (List<Long>) -> Unit) {
    val minutes = remember { (0..59).toList() }
    val hours = remember { (0..23).toList() }
    val days = remember { (0..99).toList() }
    var selectedTime by remember { mutableStateOf(listOf(initialDuration / (24 * 60), (initialDuration % (24 * 60)) / 60, initialDuration % 60)) } // indexes for lists
    LaunchedEffect(selectedTime) {
        setTime(selectedTime)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
    ) {
        TimePickerLazyColumn(days, selectedTime[0].toInt()) {
            selectedTime = selectedTime.toMutableList().apply { this[0] = it }
        }
        TimePickerLazyColumn(hours, selectedTime[1].toInt()) {
            selectedTime = selectedTime.toMutableList().apply { this[1] = it }
        }
        TimePickerLazyColumn(minutes, selectedTime[2].toInt()) {
            selectedTime = selectedTime.toMutableList().apply { this[2] = it }
        }
    }
}

@Composable
fun TimePickerLazyColumn(items: List<Int>, initialIndex: Int, updateState: (Long) -> Unit) {
    val scope = rememberCoroutineScope()
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    var chosenNumber by remember { mutableStateOf(initialIndex) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect {
                chosenNumber = items[(it + 1) % items.size]
                updateState(chosenNumber.toLong())
            }
    }
    LaunchedEffect(Unit) {
        scope.launch {
            listState.scrollToItem(initialIndex - 1 + items.size)
        }
    }
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        modifier = Modifier
            .height(pixelsToDp(pixels = itemHeightPixels.intValue * 3) + 10.dp)
    ) {
        items(count = Int.MAX_VALUE / 2, itemContent = { item ->
            val index = item % items.size
            Text(
                text = if (items[index] > 9) "${items[index]}" else "0${items[index]}",
                style = Typography.bodyLarge,
                modifier = Modifier.onSizeChanged { size ->
                    itemHeightPixels.intValue = size.height
                }
            )
        })
    }
}


@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }

@Preview
@Composable
fun DurationPickerPreview() {
    DurationPicker(2000L, {})
}

@Preview(showBackground = true)
@Composable
fun CreateRecipePreview() {
    YummTheme { CreateRecipeScreen(RecipeUiState(), {}, {}, {}, {}) }
}