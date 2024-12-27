package lv.yumm.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lv.yumm.login.service.AccountService
import lv.yumm.service.LogService
import lv.yumm.service.StorageService
import lv.yumm.login.ui.LoginEvent
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState(
        displayName = Firebase.auth.currentUser?.displayName,
        email = Firebase.auth.currentUser?.email ?: ""
    ))
    val loginUiState = _loginUiState.asStateFlow()

    fun createAnonymousAccount() {
        accountService.createAnonymousAccount {  }
    }

    fun authenticate(onError: (Throwable?) -> Unit) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        if (isInputNotNull(email, password)) {
            accountService.authenticate(
                email,
                password
            ) { onError(it) }
        }
    }

    fun register(onError: (Throwable?) -> Unit) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        val confirmPassword = _loginUiState.value.confirmPassword
        if (password != confirmPassword) {
            _loginUiState.update {
                it.copy(confirmPasswordError = true)
            }
            return
        }
        if (isInputNotNull(email, password)) {
            accountService.registerAccount(
                email,
                password,
                _loginUiState.value.displayName
            ) { onError(it) }
        }
    }

    fun signOut() {
        accountService.signOut()
    }

    fun delete(onReAuthenticate: (Throwable?) -> Unit, onError: (Throwable?) -> Unit) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        if (isInputNotNull(email, password)) {
            accountService.deleteAccount(
                email, password,
                onReAuthenticate = onReAuthenticate,
                onResult = onError
            )
        }
    }

    private fun isInputNotNull(email: String, password: String): Boolean {
        if (email.isNotBlank() && password.isNotBlank()){
            return true
        } else if (email.isBlank()) {
            _loginUiState.update {
                it.copy(emailEmpty = true)
            }
            return false
        } else {
            _loginUiState.update {
                it.copy(passwordEmpty = true)
            }
            return false
        }
    }

    private fun clearErrors() {
        _loginUiState.update {
            it.copy(
                emailEmpty = false,
                passwordEmpty = false,
                confirmPasswordError = false,
                credentialError = null
            )
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> {
                clearErrors()
                _loginUiState.update {
                    it.copy(email = event.email)
                }
            }
            is LoginEvent.UpdatePassword -> {
                clearErrors()
                _loginUiState.update {
                    it.copy(password = event.password)
                }
            }
            is LoginEvent.UpdateConfirmPassword -> {
                clearErrors()
                _loginUiState.update {
                    it.copy(confirmPassword = event.password)
                }
            }
            is LoginEvent.UpdateDisplayName -> {
                clearErrors()
                _loginUiState.update {
                    it.copy(displayName = event.name)
                }
            }
            is LoginEvent.LogIn -> {
                if (!_loginUiState.value.hasError) {
                    authenticate { error ->
                        if (error != null) {
                            error as FirebaseAuthException
                            _loginUiState.update {
                                it.copy(credentialError = error.message)
                            }
                        }
                    }
                }
            }
            is LoginEvent.SignUp -> {
                if (!_loginUiState.value.hasError) {
                    register { error ->
                        if (error != null) {
                            error as FirebaseAuthException
                            _loginUiState.update {
                                it.copy(credentialError = error.message)
                            }
                        }
                    }
                }
            }
            is LoginEvent.DeleteAccount -> {
                delete(
                    onReAuthenticate = {},
                    onError = {}
                )
            }
            is LoginEvent.EditEmail -> {

            }
            is LoginEvent.EditName -> {
                accountService.editDisplayName(
                    _loginUiState.value.displayName ?: "User",
                ) {
                    //todo
                }
            }
            is LoginEvent.EditPassword -> {

            }
            is LoginEvent.SignOut -> {
                signOut()
            }
        }
    }

}