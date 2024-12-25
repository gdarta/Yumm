package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
    onEvent: (LoginEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginFields(
            email = uiState().email,
            onEmailChange = { onEvent(LoginEvent.UpdateEmail(it)) },
            password = uiState().password,
            onPasswordChange = { onEvent(LoginEvent.UpdatePassword(it)) },
        )
        LoginButton(text = "Log in") { onEvent(LoginEvent.LogIn(navigateBack)) }
        Text(
            text = "Do not have an account?"
        )
        LoginButton(text = "Create an account") { navigateToSignUp() }
    }
}

@Composable
fun LoginFields(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { onEmailChange(it) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "E-mail")
            }
        )
        TextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            textStyle = Typography.bodyMedium,
            colors = recipeTextFieldColors(),
            label = {
                Text(text = "Password")
            }
        )
    }
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