package lv.yumm.login.ui

sealed class LoginEvent {
    class UpdateEmail(val email: String): LoginEvent()
    class UpdatePassword(val password: String): LoginEvent()
    class UpdateConfirmPassword(val password: String): LoginEvent()
    class UpdateDisplayName(val name: String): LoginEvent()

    class LogIn(): LoginEvent()
    class SignUp(): LoginEvent()
    class SignOut(): LoginEvent()
    class DeleteAccount(): LoginEvent()

    class EditEmail(val email: String): LoginEvent()
    class EditPassword(val password: String, val confirmPassword: String, val goBack: () -> Unit): LoginEvent()
    class EditName(): LoginEvent()

    class ClearVerifyScreen(): LoginEvent()
}