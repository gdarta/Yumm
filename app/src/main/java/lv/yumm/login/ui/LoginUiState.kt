package lv.yumm.login.ui

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",

    val newEmail: String = "",
    val newEmailEmpty: Boolean = false,

    val emailEmpty: Boolean = false,
    val passwordEmpty: Boolean = false,
    val displayNameEmpty: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val credentialError: String? = null,

    val verificationScreenState: VerificationScreenUiState? = null,
    val resetPasswordScreenState: VerificationScreenUiState? = null
) {
    val hasError: Boolean = emailEmpty || passwordEmpty || displayNameEmpty || (credentialError != null)
}

data class VerificationScreenUiState(
    val email: String,
    val resendEmail: () -> Unit
)
