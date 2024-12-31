package lv.yumm.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import lv.yumm.ui.theme.Typography

@Composable
fun ResetPasswordScreen(
    email: String,
    resendEmail: () -> Unit,
    onBackPressed: () -> Unit
) {
    val text = remember {
        buildAnnotatedString {
            append("To proceed with resetting your password, open the link sent to your e-mail.")
            append("\n\n\n")
            append("A reset e-mail was sent to ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(email)
            }
            append(".")
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge
        )
        LoginButton(
            text = "Resend e-mail"
        ) { resendEmail() }
    }
}