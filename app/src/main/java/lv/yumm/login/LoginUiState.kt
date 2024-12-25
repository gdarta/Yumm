package lv.yumm.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
