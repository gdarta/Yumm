package lv.yumm.login.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import lv.yumm.ui.EditProfileAction
import lv.yumm.ui.theme.Typography
import timber.log.Timber

@Composable
fun ActionAuthorizeScreen(
    infoText: String,
    actionText: String,
    actionType: EditProfileAction,
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    val currentUser = remember { mutableStateOf(Firebase.auth.currentUser) }
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser.value = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        onDispose {
            Firebase.auth.removeAuthStateListener(authStateListener)
        }
    }

    BackHandler(true) {
        navigateToProfileScreen()
        onEvent(LoginEvent.ClearVerifyScreen())
    }

    val newEmail = remember { mutableStateOf("") }
    if (currentUser.value != null && uiState().verificationScreenState == null && uiState().resetPasswordScreenState == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (actionType == EditProfileAction.EMAIL) {
                LoginTextField(
                    value = newEmail.value,
                    onValueChange = {
                        newEmail.value = it
                    },
                    label = "New e-mail",
                    isError = uiState().newEmailEmpty
                )
            }
            Text(
                text = infoText,
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
            LoginFields(
                uiState = uiState,
                onEvent = { onEvent(it) }
            )
            LoginButton(text = actionText) {
                when (actionType) {
                    EditProfileAction.EMAIL -> {
                        onEvent(LoginEvent.EditEmail(newEmail.value))
                    }
                    EditProfileAction.DELETE -> {
                        onEvent(LoginEvent.DeleteAccount())
                    }
                    EditProfileAction.PASSWORD -> {
                        onEvent(LoginEvent.EditPassword())
                    }
                }
            }
        }
    } else if (uiState().verificationScreenState != null) {
        VerifyEmailScreen(
            email = uiState().verificationScreenState?.email ?: "...",
            resendEmail = {
                uiState().verificationScreenState?.resendEmail()
            },
            onBackPressed = {
                onEvent(LoginEvent.ClearVerifyScreen())
            }
        )
    } else if (uiState().resetPasswordScreenState != null) {
        ResetPasswordScreen(
            email = uiState().resetPasswordScreenState?.email ?: "...",
            resendEmail = {
                uiState().resetPasswordScreenState?.resendEmail()
            },
            onBackPressed = {
                onEvent(LoginEvent.ClearVerifyScreen())
            }
        )
    } else {
        LoginScreen(uiState, onEvent, navigateToProfileScreen)
    }
}