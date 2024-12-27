package lv.yumm.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginUiState
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun RegisterScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToLogIn: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTextField(
            value = uiState().email,
            onValueChange = { onEvent(LoginEvent.UpdateEmail(it)) },
            label = "E-mail",
            isError = uiState().emailEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        LoginTextField(
            value = uiState().displayName ?: "",
            onValueChange = { onEvent(LoginEvent.UpdateDisplayName(it)) },
            label = "Display name"
        )
        LoginTextField(
            value = uiState().password,
            onValueChange = { onEvent(LoginEvent.UpdatePassword(it)) },
            label = "Password",
            isError = uiState().passwordEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        LoginTextField(
            value = uiState().confirmPassword,
            onValueChange = { onEvent(LoginEvent.UpdateConfirmPassword(it)) },
            label = "Confirm password",
            isError = uiState().confirmPasswordError,
            errorText = "Password does not match",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        LoginButton(text = "Sign up") { onEvent(LoginEvent.SignUp()) }
        Text(
            text = "Already have an account? Sign in",
            style = Typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable {
                navigateToLogIn()
            }
        )
    }
}