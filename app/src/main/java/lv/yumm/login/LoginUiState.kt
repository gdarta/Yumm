package lv.yumm.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String? = null,

    val emailEmpty: Boolean = false,
    val passwordEmpty: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val credentialError: String? = null
) {
    val hasError: Boolean = emailEmpty || passwordEmpty || (credentialError != null)
}
