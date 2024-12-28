package lv.yumm.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lv.yumm.BaseViewModel
import lv.yumm.login.service.AccountService
import lv.yumm.service.LogService
import lv.yumm.service.StorageService
import lv.yumm.login.ui.LoginEvent
import lv.yumm.login.ui.LoginUiState
import lv.yumm.login.ui.VerificationScreenUiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : BaseViewModel() {

    private val _loginUiState = MutableStateFlow(
        LoginUiState(
            displayName = Firebase.auth.currentUser?.displayName ?: "",
            email = Firebase.auth.currentUser?.email ?: ""
        )
    )
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
            if (_loginUiState.value.displayName.isNotBlank()) {
                accountService.registerAccount(
                    email,
                    password,
                    _loginUiState.value.displayName
                ) { onError(it) }
            } else {
                _loginUiState.update { it.copy(displayNameEmpty = true) }
            }
        }
    }

    fun signOut() {
        accountService.signOut()
    }

    fun delete(onReAuthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        if (isInputNotNull(email, password)) {
            accountService.deleteAccount(
                email, password,
                onReAuthenticate = onReAuthenticate,
                onResult = onResult
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
                displayNameEmpty = false,
                credentialError = null,
                newEmailEmpty = false
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
                            handleApiError(error)
                        }
                    }
                }
            }
            is LoginEvent.SignUp -> {
                if (!_loginUiState.value.hasError) {
                    register { error ->
                        if (error != null) {
                            error as FirebaseAuthException
                            handleApiError(error)
                        }
                    }
                }
            }
            is LoginEvent.DeleteAccount -> {
                delete(
                    onReAuthenticate = { error ->
                        if (error != null) {
                            error as FirebaseAuthException
                            handleApiError(error)
                        }
                    },
                    onResult = {
                        reloadUser()
                    }
                )
            }
            is LoginEvent.EditEmail -> {
                if (event.email.isNotBlank()) {
                    accountService.editEmail(
                        oldEmail = _loginUiState.value.email,
                        newEmail = event.email,
                        password = _loginUiState.value.password,
                        onReAuthenticate = { error ->
                            if (error != null) {
                                error as FirebaseAuthException
                                handleApiError(error)
                            }
                        },
                        onResult = {
                            reloadUser()
                            _loginUiState.update {
                                it.copy(
                                    verificationScreenState = VerificationScreenUiState(
                                        email = event.email,
                                        resendEmail = {
                                            accountService.verifyBeforeUpdateEmail(event.email) {
                                                postMessage("E-mail sent")
                                            }
                                        }
                                    )
                                )
                            }
                        }
                    )
                } else {
                    _loginUiState.update {
                        it.copy(newEmailEmpty = true)
                    }
                }
            }
            is LoginEvent.EditName -> {
                if (_loginUiState.value.displayName.isNotBlank()) {
                    accountService.editDisplayName(
                        _loginUiState.value.displayName,
                    ) {
                        reloadUser()
                    }
                } else {
                    _loginUiState.update {
                        it.copy(displayName = Firebase.auth.currentUser?.displayName ?: "User")
                    }
                }
            }
            is LoginEvent.EditPassword -> {

            }
            is LoginEvent.SignOut -> {
                signOut()
                _loginUiState.update {
                    LoginUiState()
                }
            }
            is LoginEvent.ClearVerifyScreen -> {
                _loginUiState.update {
                    it.copy(
                        verificationScreenState = null,
                        resetPasswordScreenState = null
                    )
                }
            }
        }
    }

    private fun reloadUser() {
        Firebase.auth.currentUser?.reload()?.addOnCompleteListener{
            Timber.d("Reloaded user")
        }
    }

    private fun handleApiError(error: FirebaseAuthException) {
        _loginUiState.update {
            it.copy(credentialError = getApiErrorText(error))
        }
    }


    private fun getApiErrorText(error: FirebaseAuthException): String {
        return when (error.errorCode) {
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", "ERROR_EMAIL_ALREADY_IN_USE" -> "This e-mail is already taken."
            "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_EMAIL", "ERROR_WRONG_PASSWORD", "ERROR_USER_MISMATCH", "" -> "Incorrect e-mail or password."
            "ERROR_USER_NOT_FOUND" -> "A user with these credentials does not exist."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Try again later."
            else -> "An error occurred."
        }
    }

}