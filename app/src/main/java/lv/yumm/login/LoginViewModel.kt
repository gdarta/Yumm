package lv.yumm.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lv.yumm.login.service.AccountService
import lv.yumm.service.LogService
import lv.yumm.service.StorageService
import lv.yumm.login.ui.LoginEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState(
        displayName = accountService.currentUser?.displayName,
        email = accountService.currentUser?.email ?: ""
    ))
    val loginUiState = _loginUiState.asStateFlow()

    val currentUser = accountService.currentUser

    fun createAnonymousAccount() {
        accountService.createAnonymousAccount {  }
    }

    fun authenticate(onError: (Throwable?) -> Unit) {
        accountService.authenticate(_loginUiState.value.email, _loginUiState.value.password) { onError(it) }
    }

    fun register(onError: (Throwable?) -> Unit) {
        accountService.registerAccount(_loginUiState.value.email, _loginUiState.value.password, _loginUiState.value.displayName) { onError(it) }
    }

    fun signOut() {
        accountService.signOut()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> {
                _loginUiState.update {
                    it.copy(email = event.email)
                }
            }
            is LoginEvent.UpdatePassword -> {
                _loginUiState.update {
                    it.copy(password = event.password)
                }
            }
            is LoginEvent.UpdateConfirmPassword -> {
                _loginUiState.update {
                    it.copy(confirmPassword = event.password)
                }
            }
            is LoginEvent.UpdateDisplayName -> {
                _loginUiState.update {
                    it.copy(displayName = event.name)
                }
            }
            is LoginEvent.LogIn -> {
                authenticate { error ->
                    if (error == null) {
                        event.navigateBack()
                    }
                }
            }
            is LoginEvent.SignUp -> {
                register { error ->
                    if (error == null) {
                        event.navigateBack()
                    }
                }
            }
            is LoginEvent.EditEmail -> {

            }
            is LoginEvent.EditName -> {

            }
            is LoginEvent.EditPassword -> {

            }
            is LoginEvent.SignOut -> {
                signOut()
            }
        }
    }

}