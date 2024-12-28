package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lv.yumm.login.ui.LoginUiState
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.loginTextFieldColors
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun LoginScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginFields(
            uiState = uiState,
            onEvent = { onEvent(it) }
        )
        LoginButton(text = "Log in") { onEvent(LoginEvent.LogIn()) }
        Text(
            text = "Do not have an account?"
        )
        LoginButton(text = "Create an account") { navigateToSignUp() }
    }
}

@Composable
fun LoginFields(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState().credentialError ?: "",
            color = MaterialTheme.colorScheme.error
        )
        LoginTextField(
            value = uiState().email,
            onValueChange = { onEvent(LoginEvent.UpdateEmail(it)) },
            label = "E-mail",
            isError = uiState().emailEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        LoginTextField(
            value = uiState().password,
            onValueChange = { onEvent(LoginEvent.UpdatePassword(it)) },
            label = "Password",
            isError = uiState().passwordEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions? = null,
    visualTransformation: VisualTransformation? = null
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = Typography.bodyMedium,
        colors = loginTextFieldColors(),
        maxLines = 1,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = errorText ?: "Field must not be empty!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        label = {
            Text(text = label)
        }
    )
}

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    text: String,
    onclick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onclick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(3.dp)
    ) {
        Text(text = text, style = Typography.titleMedium)
    }
}