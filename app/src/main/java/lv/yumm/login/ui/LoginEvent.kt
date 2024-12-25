package lv.yumm.login.ui

sealed class LoginEvent {
    class UpdateEmail(val email: String): LoginEvent()
    class UpdatePassword(val password: String): LoginEvent()
    class UpdateConfirmPassword(val password: String): LoginEvent()
    class LogIn(val navigateBack: () -> Unit): LoginEvent()
    class SignUp(val navigateBack: () -> Unit): LoginEvent()
}