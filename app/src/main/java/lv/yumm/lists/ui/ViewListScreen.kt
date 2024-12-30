package lv.yumm.lists.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListUiState
import lv.yumm.lists.data.toFormattedDate
import lv.yumm.recipes.ui.EditRow
import lv.yumm.ui.theme.Typography
import java.util.Locale

@Composable
fun ViewListScreen(
    uiState: ListUiState,
    onEvent: (ListEvent) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 30.dp)
    ) {
        item {
            Text(
                text = if (uiState.title.isNotBlank()) uiState.title else uiState.updatedAt?.seconds?.toFormattedDate()
                    ?: "...",
                style = Typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        itemsIndexed(uiState.list) { index, item ->
            EditRow(modifier = Modifier.clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                onEvent(ListEvent.CheckItem(index, !item.checked))
            }) {
                Text(
                    text = "${item.ingredient.name}, ${
                        String.format(
                            Locale("en"),
                            "%.1f",
                            item.ingredient.amount
                        )
                    } ${item.ingredient.unit}",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.alpha(if (item.checked) 0.4f else 1f)
                )
                Checkbox(
                    checked = item.checked,
                    onCheckedChange = { onEvent(ListEvent.CheckItem(index, it)) })
            }
        }
    }
}