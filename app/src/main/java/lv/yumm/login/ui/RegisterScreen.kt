package lv.yumm.login.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginUiState
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun RegisterScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToLogIn: () -> Unit
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
            value = uiState().displayName ?: "User",
            onValueChange = { onEvent(LoginEvent.UpdateDisplayName(it)) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "Display name")
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
        TextField(
            value = uiState().confirmPassword,
            onValueChange = { onEvent(LoginEvent.UpdateConfirmPassword(it)) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "Confirm password")
            }
        )
        LoginButton(text = "Sign up") { onEvent(LoginEvent.SignUp(navigateBack)) }
        Text(
            text = "Already have an account? Sign in",
            style = Typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable {
                navigateToLogIn()
            }
        )
    }
}