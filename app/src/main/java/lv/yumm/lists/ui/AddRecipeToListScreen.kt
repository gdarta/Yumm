package lv.yumm.lists.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListUiState
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import lv.yumm.login.ui.LoginButton
import lv.yumm.login.ui.LoginToProceedScreen
import lv.yumm.recipes.data.Ingredient
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun AddRecipeToListScreen(
    currentUserId: String,
    navigateToLogin: () -> Unit,
    recipeIngredients: List<Ingredient>,
    userLists: List<ListUiState>,
    onEvent: (ListEvent) -> Unit
) {
    if (currentUserId != EMPTY_USER_ID){
        var title by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add items to one of your existing lists:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                items(userLists) { list ->
                    UserListCard(
                        onClick = {
                            onEvent(ListEvent.AddIngredientsToUserList(recipeIngredients, list.id))
                        },
                        title = list.title,
                        updatedAt = list.updatedAt
                    )
                }
            }
            Text(
                text = "... or create a new list:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextField(
                value = title,
                onValueChange = { title = it },
                textStyle = Typography.titleMedium,
                label = { Text(text = "Title") },
                singleLine = true,
                colors = recipeTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
            LoginButton(text = "Create new list", modifier = Modifier.padding(bottom = 30.dp)) {
                onEvent(ListEvent.CreateListFromIngredients(title, recipeIngredients))
            }
        }
    } else {
        LoginToProceedScreen(
            navigateToLogin,
            "To save and create lists, log in or create an account"
        )
    }
}