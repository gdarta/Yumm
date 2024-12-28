package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import lv.yumm.R
import lv.yumm.recipes.ui.EditRow
import lv.yumm.ui.EditProfileAction
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.recipeTextFieldColors

@Composable
fun ProfileScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToAction: (String, String, EditProfileAction) -> Unit
) {
    val currentUser = remember { mutableStateOf(Firebase.auth.currentUser) }
    DisposableEffect(currentUser) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser.value = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        onDispose {
            Firebase.auth.removeAuthStateListener(authStateListener)
        }
    }

    val register = remember { mutableStateOf(false) }

    if (currentUser.value != null) {
        currentUser.value?.let {
            ProfileInfo(
                it,
                uiState,
                onEvent = { onEvent(it) },
                navigateToAction = { info, text, onEvent ->
                    navigateToAction(
                        info,
                        text,
                        onEvent
                    )
                }
            )
        }
    } else if (!register.value) {
        LoginScreen(uiState, onEvent) { register.value = true }
    } else {
        RegisterScreen(uiState, onEvent) { register.value = false }
    }
}

@Composable
fun ProfileInfo(
    currentUser: FirebaseUser,
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    navigateToAction: (String, String, EditProfileAction) -> Unit,
) {
    val editName = remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        EditRow {
            if (editName.value) {
                TextField(
                    value = uiState().displayName,
                    onValueChange = { onEvent(LoginEvent.UpdateDisplayName(it)) },
                    textStyle = Typography.headlineMedium,
                    colors = recipeTextFieldColors(),
                )
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = {
                        editName.value = !editName.value
                        onEvent(LoginEvent.EditName())
                    },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save"
                        )
                    }
                )
            } else {
                Text(
                    text = "Hello, ${uiState().displayName}",
                    style = Typography.headlineMedium
                )
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = {
                        editName.value = !editName.value
                    },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit"
                        )
                    }
                )
            }
        }
        EditRow {
            Text(
                text = "Email: ${currentUser.email}",
                style = Typography.bodyLarge
            )
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                onClick = {
                    navigateToAction(
                        "To change your e-mail address, verify your current profile info",
                        "Change e-mail",
                        EditProfileAction.EMAIL
                    )
                },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }
            )
        }
        LoginButton(
            text = "Sign out",
            modifier = Modifier.fillMaxWidth()
        ) { onEvent(LoginEvent.SignOut()) }
        LoginButton(text = "Change password", modifier = Modifier.fillMaxWidth()) {
            navigateToAction(
                "To change your password, verify your current profile info",
                "Change password",
                EditProfileAction.PASSWORD
            )
        }
        LoginButton(text = "Delete profile", modifier = Modifier.fillMaxWidth()) {
            navigateToAction(
                "To proceed with the deletion of your account, verify your identity. By deleting your account, all of your data will be deleted.",
                "Delete account",
                EditProfileAction.DELETE
            )
        }
    }
}