package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginUiState
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun LoginScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = uiState().email,
            onValueChange = { onEvent(LoginEvent.UpdateEmail(it)) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "E-mail")
            }
        )
        TextField(
            value = uiState().password,
            onValueChange = { onEvent(LoginEvent.UpdatePassword(it)) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "Password")
            }
        )
        Button(
            onClick = {
                onEvent(LoginEvent.LogIn())
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Log in", style = Typography.titleMedium)
        }
    }
}