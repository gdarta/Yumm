package lv.yumm.login.ui

sealed class LoginEvent {
    class UpdateEmail(val email: String): LoginEvent()
    class UpdatePassword(val password: String): LoginEvent()
    class UpdateConfirmPassword(val password: String): LoginEvent()
    class UpdateDisplayName(val name: String): LoginEvent()

    class LogIn(val navigateBack: () -> Unit): LoginEvent()
    class SignUp(val navigateBack: () -> Unit): LoginEvent()
    class SignOut(): LoginEvent()

    class EditEmail(val email: String): LoginEvent()
    class EditPassword(val password: String): LoginEvent()
    class EditName(val name: String): LoginEvent()
}