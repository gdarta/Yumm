package lv.yumm.login.ui

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginEvent
import lv.yumm.login.LoginUiState
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.loginTextFieldColors
import lv.yumm.R

@Composable
fun LoginScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToResetPassword: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(top = 50.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginFields(
            uiState = uiState,
            onEvent = { onEvent(it) }
        )
        Text(
            text = "Forgot password?",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                navigateToResetPassword()
            }
        )
        LoginButton(text = "Log in", modifier = Modifier.padding(bottom = 30.dp)) { onEvent(LoginEvent.LogIn()) }
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
        PasswordTextField(
            value = uiState().password,
            onValueChange = { onEvent(LoginEvent.UpdatePassword(it)) },
            label = "Password",
            isError = uiState().passwordEmpty,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions? = null,
) {
    var visible by remember { mutableStateOf(false) }
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = Typography.bodyMedium,
        colors = loginTextFieldColors(),
        maxLines = 1,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
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
        },
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible }
            ) {
                if (visible) {
                    Icon(
                        painter = painterResource(R.drawable.ic_not_visible),
                        contentDescription = "Set password not visible",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_visible),
                        contentDescription = "Set password visible",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
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