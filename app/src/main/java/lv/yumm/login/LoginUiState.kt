package lv.yumm.login

data class LoginUiState(
    val isLoading: Boolean = false,

    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",

    val newEmailEmpty: Boolean = false,
    val newPasswordEmpty: Boolean = false,
    val newPasswordConfirmEmpty: Boolean = false,

    val emailEmpty: Boolean = false,
    val passwordEmpty: Boolean = false,
    val displayNameEmpty: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val credentialError: String? = null,

    val verificationScreenState: VerificationScreenUiState? = null,
) {
    val hasError: Boolean =
        emailEmpty || passwordEmpty || displayNameEmpty || newPasswordEmpty || newPasswordConfirmEmpty || (credentialError != null)
}

data class VerificationScreenUiState(
    val email: String,
    val resendEmail: () -> Unit
)
