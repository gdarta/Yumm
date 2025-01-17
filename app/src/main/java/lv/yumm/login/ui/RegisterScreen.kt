package lv.yumm.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginEvent
import lv.yumm.login.LoginUiState
import lv.yumm.ui.theme.Typography

@Composable
fun RegisterScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToLogIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState().credentialError ?: "",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        LoginTextField(
            value = uiState().email,
            onValueChange = { onEvent(LoginEvent.UpdateEmail(it)) },
            label = "E-mail",
            isError = uiState().emailEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        LoginTextField(
            value = uiState().displayName,
            onValueChange = { onEvent(LoginEvent.UpdateDisplayName(it)) },
            label = "Display name",
            isError = uiState().displayNameEmpty
        )
        PasswordTextField(
            value = uiState().password,
            onValueChange = { onEvent(LoginEvent.UpdatePassword(it)) },
            label = "Password",
            isError = uiState().passwordEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        PasswordTextField(
            value = uiState().confirmPassword,
            onValueChange = { onEvent(LoginEvent.UpdateConfirmPassword(it)) },
            label = "Confirm password",
            isError = uiState().confirmPasswordError,
            errorText = "Password does not match",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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