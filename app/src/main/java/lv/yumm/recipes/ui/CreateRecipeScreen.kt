package lv.yumm.recipes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lv.yumm.GalleryAndCameraLauncher
import lv.yumm.R
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.RatingBar
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.YummTheme
import lv.yumm.ui.theme.recipeTextFieldColors
import java.util.Locale

@Composable
fun CreateRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateToRecipesScreen: () -> Unit,
    navigateToEditIngredientsScreen: (Long) -> Unit,
    navigateToEditDirectionsScreen: (Long) -> Unit,
) {
    var difficulty by remember { mutableFloatStateOf(0f) }
    if (uiState.editDurationDialog) {
        EditDurationDialog(
            uiState.duration,
            { onEvent(RecipeEvent.UpdateDuration(it)) },
            { onEvent(RecipeEvent.SetDurationDialog(false)) }
        )
    }
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
                label = { Text(text = "Title") },
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
                label = { Text(text = "Description") },
                maxLines = 5,
                colors = recipeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            EditRow {
                AsyncImage(
                    model = uiState.imageUrl,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_pasta_filled),
                    fallback = painterResource(R.drawable.ic_pasta_filled),
                    error = painterResource(R.drawable.ic_pasta_filled), // todo icon for placeholder brr
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
                )
                GalleryAndCameraLauncher(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                ) {
                    onEvent(RecipeEvent.UploadPicture(it))
                }
            }
        }
        item {
            var expanded by remember { mutableStateOf(false) }
            EditRow {
                Text(
                    text = "Category:",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box{
                    Surface(
                        onClick = { expanded = !expanded },
                        color = MaterialTheme.colorScheme.tertiary,
                        shadowElevation = 5.dp,
                        shape = RoundedCornerShape(20.dp)
                    ){
                        Text(
                            text = uiState.type?.name ?: "Choose type...",
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(all = 10.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        RecipeType.entries.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    onEvent(RecipeEvent.UpdateCategory(type))
                                    expanded = false
                                          },
                                text = { Text(text = type.name) }
                            )
                        }
                    }
                }
            }
        }
        item {
            EditRow {
                Text(
                    text = "Difficulty:  ${uiState.difficulty.toInt()}",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                RatingBar(
                    modifier = Modifier.weight(1f),
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
            EditRow {
                Text(
                    text = "Duration: ${uiState.duration.toTimestamp()}",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    content = { Text(text = "Edit duration") },
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { onEvent(RecipeEvent.SetDurationDialog(true)) }
                )
            }
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
fun EditDurationDialog(
    initialDuration: Long,
    setTime: (List<Long>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        DurationPicker(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .height(200.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(20.dp), initialDuration
        ) { setTime(it) }
    }
}

@Composable
fun DurationPicker(modifier: Modifier, initialDuration: Long, setTime: (List<Long>) -> Unit) {
    val minutes = remember { (0..59).toList() }
    val hours = remember { (0..23).toList() }
    val days = remember { (0..99).toList() }
    var selectedTime by remember {
        mutableStateOf(
            listOf(
                initialDuration / (24 * 60),
                (initialDuration % (24 * 60)) / 60,
                initialDuration % 60
            )
        )
    }
    LaunchedEffect(selectedTime) {
        setTime(selectedTime)
    }
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        TimePickerLazyColumn(
            Modifier
                .weight(1f), "Days", days, selectedTime[0].toInt()
        ) {
            selectedTime = selectedTime.toMutableList().apply { this[0] = it }
        }
        TimePickerLazyColumn(
            Modifier
                .weight(1f), "Hours", hours, selectedTime[1].toInt()
        ) {
            selectedTime = selectedTime.toMutableList().apply { this[1] = it }
        }
        TimePickerLazyColumn(
            Modifier
                .weight(1f), "Minutes", minutes, selectedTime[2].toInt()
        ) {
            selectedTime = selectedTime.toMutableList().apply { this[2] = it }
        }
    }
}

@Composable
fun TimePickerLazyColumn(
    modifier: Modifier,
    label: String,
    items: List<Int>,
    initialIndex: Int,
    updateState: (Long) -> Unit
) {
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
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
                    fontSize = if (items[index] == chosenNumber) 30.sp else 26.sp,
                    fontWeight = if (items[index] == chosenNumber) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier
                        .alpha(
                            if (items[index] == chosenNumber) 1f else .5f
                        )
                        .onSizeChanged { size ->
                            itemHeightPixels.intValue = size.height
                        }
                )
            })
        }
    }
}


@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }

@Preview
@Composable
fun DurationPickerPreview() {
    EditDurationDialog(5000L, {}) { }
}

@Preview(showBackground = true)
@Composable
fun CreateRecipePreview() {
    YummTheme { CreateRecipeScreen(RecipeUiState(), {}, {}, {}, {}) }
}