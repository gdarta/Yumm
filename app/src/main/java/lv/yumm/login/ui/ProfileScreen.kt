package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lv.yumm.login.LoginUiState

@Composable
fun ProfileScreen(
    uiState: () -> LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = "Hello, ${uiState().displayName}"
        )
        LoginButton(text = "Sign out", modifier = Modifier.fillMaxWidth()) { onEvent(LoginEvent.SignOut()) }
    }
}