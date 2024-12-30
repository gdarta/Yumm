package lv.yumm.lists.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lv.yumm.R
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListUiState
import lv.yumm.lists.data.toFormattedDate
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import lv.yumm.login.ui.LoginButton
import lv.yumm.recipes.ui.SwipeableItemWithActions
import lv.yumm.ui.theme.Typography

@Composable
fun ListScreen(
    currentUserId: String,
    navigateToLogin: () -> Unit,
    lists: List<ListUiState>,
    onEvent: (ListEvent) -> Unit
) {
    if (currentUserId != EMPTY_USER_ID && lists.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(10.dp)
        ) {
            itemsIndexed(items = lists, key = { _, list -> list.id }) { index, list ->
                var isDeleteRevealed by remember { mutableStateOf(false) }
                SwipeableItemWithActions(
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp),
                    isLeftRevealed = isDeleteRevealed,
                    onLeftExpanded = { isDeleteRevealed = true },
                    onLeftCollapsed = { isDeleteRevealed = false },
                    leftAction = {
                        Surface(
                            onClick = {
                                onEvent(ListEvent.DeleteList(list.id))
                                isDeleteRevealed = false
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(80.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                tint = Color.White,
                                contentDescription = "delete item"
                            )
                        }
                    }
                ) {
                    Surface(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 5.dp,
                        shadowElevation = 5.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (list.title.isNotBlank()) list.title else list.updatedAt?.seconds?.toFormattedDate()
                                ?: "...",
                            style = Typography.titleMedium,
                            modifier = Modifier.padding(15.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    } else if (currentUserId != EMPTY_USER_ID) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "Empty here, click + to create a shopping list...",
                style = Typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "To save and create shopping lists, log in or create an account",
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
            LoginButton(
                text = "Log in"
            ) { navigateToLogin() }
        }
    }
}