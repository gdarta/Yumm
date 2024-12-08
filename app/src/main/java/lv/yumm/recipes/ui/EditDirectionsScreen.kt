package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.ui.theme.Typography

@Composable
fun EditDirectionsScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit,
    navigateBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(items = uiState.directions, key = {index, _ -> index}) { index, direction ->
            DirectionCard(
                text = direction,
                number = index + 1,
            ) {
                onEvent(RecipeEvent.UpdateDirection(index, it))
            }
        }
        item {
            Button(
                content = { Text(text = "Add a step") },
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(RecipeEvent.AddDirection()) }
            )
        }
        item {
            Button(
                content = { Text(text = "Return to recipe") },
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigateBack()
                }
            )
        }
    }
}

@Composable
fun DirectionCard(text: String, number: Int, updateDirection: (String) -> Unit) {
    var textField by remember { mutableStateOf(TextFieldValue(text = text, selection = TextRange(text.length))) }
    Row(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "${number}.",
            style = Typography.bodyLarge
        )
        Surface(
            shape = RoundedCornerShape(5.dp),
            shadowElevation = 3.dp,
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.background,
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = textField,
                onValueChange = {
                    textField = it
                    updateDirection(it.text)
                },
            )
        }
    }
}

@Preview
@Composable
fun DirectionCardPreview() {
    DirectionCard(
        text = "Take your peanuts and roast them in the oven for 20 minutes.",
        number = 1
    ) { }
}