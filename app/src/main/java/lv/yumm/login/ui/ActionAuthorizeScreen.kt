package lv.yumm.login.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import lv.yumm.ui.EditProfileAction
import lv.yumm.ui.theme.Typography
import timber.log.Timber

@OptIn(ExperimentalLayoutApi::class)
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
    val newPassword = remember { mutableStateOf("") }
    val newPasswordConfirm = remember { mutableStateOf("") }
    if (currentUser.value != null && uiState().verificationScreenState == null) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = infoText,
                    style = Typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                LoginFields(
                    uiState = uiState,
                    onEvent = { onEvent(it) }
                )
                if (actionType == EditProfileAction.PASSWORD) {
                    LoginTextField(
                        value = newPassword.value,
                        onValueChange = {
                            newPassword.value = it
                        },
                        label = "New password",
                        isError = uiState().newPasswordEmpty,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    LoginTextField(
                        value = newPasswordConfirm.value,
                        onValueChange = {
                            newPasswordConfirm.value = it
                        },
                        label = "Confirm new password",
                        isError = uiState().newPasswordConfirmEmpty,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
                if (actionType == EditProfileAction.EMAIL) {
                    LoginTextField(
                        value = newEmail.value,
                        onValueChange = {
                            newEmail.value = it
                        },
                        label = "New e-mail",
                        isError = uiState().newEmailEmpty,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            }
            val imeVisible = WindowInsets.isImeVisible
            LoginButton(modifier = Modifier.padding(bottom = if (imeVisible) 5.dp else 50.dp),text = actionText) {
                when (actionType) {
                    EditProfileAction.EMAIL -> {
                        onEvent(LoginEvent.EditEmail(newEmail.value))
                    }

                    EditProfileAction.DELETE -> {
                        onEvent(LoginEvent.DeleteAccount())
                    }

                    EditProfileAction.PASSWORD -> {
                        onEvent(
                            LoginEvent.EditPassword(
                                newPassword.value,
                                newPasswordConfirm.value,
                                navigateToProfileScreen
                            )
                        )
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
    } else {
        LoginScreen(uiState, onEvent, navigateToProfileScreen)
    }
}