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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState

@Composable
fun EditDirectionsScreen(
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
        horizontalAlignment = Alignment.CenterHorizontally,
        ){
        LazyColumn( //todo drag and reorder
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = uiState.directions,
                key = { index, _ -> index }) { index, direction ->
                val textField = TextFieldValue(text = direction, selection = TextRange(direction.length))
                DirectionCard(
                    textField = textField,
                    number = index + 1,
                    updateDirection = {
                        onEvent(RecipeEvent.UpdateDirection(index, it))
                    },
                    deleteDirection = {
                        onEvent(RecipeEvent.DeleteDirection(index))
                    }
                )
            }
        }
        Button(
            content = { Text(text = "Add a step") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(RecipeEvent.AddDirection())
                scope.launch {
                    listState.animateScrollToItem(index = (uiState.directions.size - 1).coerceAtLeast(0))
                }
            }
        )
        Button(
            content = { Text(text = "Save and return") },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(RecipeEvent.ValidateDirections())
                navigateBack()
            }
        )
    }
}

@Composable
fun DirectionCard(textField: TextFieldValue, number: Int, updateDirection: (String) -> Unit, deleteDirection: () -> Unit) {
    var isDeleteRevealed by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_list_reorder),
            contentDescription = null,
        )
        SwipeableItemWithActions(
            shape = RoundedCornerShape(5.dp),
            isLeftRevealed = isDeleteRevealed,
            onLeftExpanded = { isDeleteRevealed = true },
            onLeftCollapsed = { isDeleteRevealed = false },
            leftAction = {
                Surface(
                    onClick = {
                        deleteDirection()
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
        ){
            Surface(
                shape = RoundedCornerShape(5.dp),
                shadowElevation = 3.dp,
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.background,
            ) {
                TextField(
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .fillMaxWidth(),
                    value = textField,
                    onValueChange = {
                        updateDirection(it.text)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun DirectionCardPreview() {
    DirectionCard(
        textField = TextFieldValue("Take your peanuts and roast them in the oven for 20 minutes."),
        number = 1,
        {}
    ) { }
}