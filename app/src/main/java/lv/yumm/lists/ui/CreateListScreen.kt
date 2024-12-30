package lv.yumm.lists.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListUiState
import lv.yumm.recipes.IngredientOptions
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.ui.IngredientCard
import lv.yumm.recipes.ui.SwipeableItemWithActions
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun CreateListScreen (
    navigateBack: () -> Unit,
    uiState: () -> ListUiState,
    onEvent: (ListEvent) -> Unit
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
        LazyColumn( //todo drag and reorder
            state = listState,
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TextField(
                    value = uiState().title,
                    onValueChange = {
                        onEvent(ListEvent.UpdateTitle(it))
                    },
                    textStyle = Typography.titleLarge,
                    label = { Text(text = "Title") },
                    singleLine = true,
                    colors = recipeTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            itemsIndexed(uiState().list) { index, item ->
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
                                onEvent(ListEvent.DeleteItem(index))
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
                        item.ingredient,
                        name = "Item",
                        nameError = uiState().errorList[index].nameError,
                        amountError = uiState().errorList[index].amountError,
                        measurementError = uiState().errorList[index].unitError,
                        onNameChange = {
                            onEvent(
                                ListEvent.UpdateItem(
                                    index,
                                    item.ingredient.copy(name = it)
                                )
                            )
                        },
                        onAmountChange = {
                            onEvent(
                                ListEvent.UpdateItem(
                                    index,
                                    item.ingredient.copy(amount = it.toFloatOrNull() ?: 0f)
                                )
                            )
                        },
                        onMeasurementChange = {
                            onEvent(
                                ListEvent.UpdateItem(
                                    index,
                                    item.ingredient.copy(unit = it)
                                )
                            )
                        })
                }
            }
        }
        Button(
            content = { Text(text = "Add an item") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onEvent(ListEvent.AddItem())
                scope.launch {
                    listState.animateScrollToItem(index = (uiState().list.size - 1).coerceAtLeast(0))
                }
            }
        )
        Button(
            content = { Text(text = "Save and return") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState().hasError,
            onClick = {
                onEvent(ListEvent.ValidateAndSave())
                navigateBack()
            }
        )
    }
}