package lv.yumm.login

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.BaseViewModel
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.service.StorageService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) : BaseViewModel() {

    private val _loginUiState = MutableStateFlow(
        LoginUiState(
            displayName = Firebase.auth.currentUser?.displayName ?: "",
            email = Firebase.auth.currentUser?.email ?: ""
        )
    )
    val loginUiState = combine(
        _loginUiState,
        accountService.loading
    ) { uiState, isLoading ->
        uiState.copy(isLoading = isLoading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoginUiState()
    )

    init {
        viewModelScope.launch {
            accountService.currentUser.collectLatest {
                postUserId(it)
            }
        }
    }

    fun createAnonymousAccount() {
        accountService.createAnonymousAccount { }
    }

    fun authenticate(onError: (Throwable?) -> Unit) {
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        if (isInputNotNull(email, password)) {
            accountService.authenticate(
                email,
                password
            ) {
                onError(it)
                if (it == null) {
                    _loginUiState.update {
                        it.copy(
                            displayName = Firebase.auth.currentUser?.displayName ?: "User",
                            password = ""
                        )
                    }
                }
            }
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
        _loginUiState.update { LoginUiState() }
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

    private fun editEmail(email: String) {
        accountService.editEmail(
            oldEmail = _loginUiState.value.email,
            newEmail = email,
            password = _loginUiState.value.password,
            onReAuthenticate = { error ->
                if (error != null) {
                    error as FirebaseAuthException
                    handleApiError(error)
                }
            },
            onResult = {
                if (it == null) {
                    reloadUser()
                    _loginUiState.update {
                        it.copy(
                            verificationScreenState = VerificationScreenUiState(
                                email = email,
                                resendEmail = {
                                    accountService.verifyBeforeUpdateEmail(email) {
                                        postMessage("E-mail sent")
                                    }
                                }
                            )
                        )
                    }
                } else {
                    _loginUiState.update {
                        it.copy(credentialError = "Invalid e-mail address")
                    }
                }
            }
        )
    }

    private fun isInputNotNull(
        email: String? = null,
        password: String? = null,
        confirmPassword: String? = null,
        newPassword: String? = null,
        newPasswordConfirm: String? = null
    ): Boolean {
        var allInputsValid = true

        email?.let {
            if (it.isEmpty()) {
                allInputsValid = false
                _loginUiState.update {
                    it.copy(emailEmpty = true)
                }
            }
        }

        password?.let {
            if (it.isEmpty()) {
                allInputsValid = false
                _loginUiState.update {
                    it.copy(passwordEmpty = true)
                }
            }
        }

        confirmPassword?.let {
            if (it.isEmpty()) {
                allInputsValid = false
                _loginUiState.update {
                    it.copy(confirmPasswordError = true)
                }
            }
        }

        newPassword?.let {
            if (it.isEmpty()) {
                allInputsValid = false
                _loginUiState.update {
                    it.copy(newPasswordEmpty = true)
                }
            }
        }

        newPasswordConfirm?.let {
            if (it.isEmpty()) {
                allInputsValid = false
                _loginUiState.update {
                    it.copy(newPasswordConfirmEmpty = true)
                }
            }
        }

        return allInputsValid
    }

    private fun clearErrors() {
        _loginUiState.update {
            it.copy(
                emailEmpty = false,
                passwordEmpty = false,
                confirmPasswordError = false,
                displayNameEmpty = false,
                credentialError = null,
                newEmailEmpty = false,
                newPasswordEmpty = false,
                newPasswordConfirmEmpty = false
            )
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> {
                clearErrors()
                if (event.email.length <= 50) {
                    _loginUiState.update {
                        it.copy(email = event.email)
                    }
                }
            }

            is LoginEvent.UpdatePassword -> {
                clearErrors()
                if (event.password.length <= 50) {
                    _loginUiState.update {
                        it.copy(password = event.password)
                    }
                }
            }

            is LoginEvent.UpdateConfirmPassword -> {
                clearErrors()
                if (event.password.length <= 50) {
                    _loginUiState.update {
                        it.copy(confirmPassword = event.password)
                    }
                }
            }

            is LoginEvent.UpdateDisplayName -> {
                clearErrors()
                if (event.name.length <= 50) {
                    _loginUiState.update {
                        it.copy(displayName = event.name)
                    }
                }
            }

            is LoginEvent.UpdateNewEmail -> {
                clearErrors()
                if (event.email.length <= 50) {
                    _loginUiState.update {
                        it.copy(newEmail = event.email)
                    }
                }
            }

            is LoginEvent.UpdateNewPassword -> {
                clearErrors()
                if (event.password.length <= 50) {
                    _loginUiState.update {
                        it.copy(newPassword = event.password)
                    }
                }
            }

            is LoginEvent.UpdateNewPasswordConfirm -> {
                clearErrors()
                if (event.password.length <= 50) {
                    _loginUiState.update {
                        it.copy(newPasswordConfirm = event.password)
                    }
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
                        } else {
                            _loginUiState.update {
                                it.copy(password = "")
                            }
                            postMessage("Account created")
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
                    onResult = { error ->
                        if (error == null) {
                            _loginUiState.update { LoginUiState() }
                            postMessage("Account deleted")
                        } else {
                            postMessage("Deletion failed")
                        }
                    }
                )
            }

            is LoginEvent.EditEmail -> {
                if (!_loginUiState.value.hasError && isInputNotNull(
                        email = _loginUiState.value.email,
                        password = _loginUiState.value.password
                    )
                ) {
                    editEmail(event.email)
                } else if (event.email.isBlank()) {
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
                if (isInputNotNull(
                        newPassword = event.password,
                        newPasswordConfirm = event.confirmPassword,
                        email = _loginUiState.value.email,
                        password = _loginUiState.value.password
                    )
                ) {
                    if (event.password == event.confirmPassword) {
                        accountService.changePassword(
                            email = _loginUiState.value.email,
                            password = _loginUiState.value.password,
                            newPassword = event.password,
                            onReAuthenticate = { error ->
                                if (error != null) {
                                    error as FirebaseAuthException
                                    handleApiError(error)
                                }
                            },
                            onResult = {
                                if (it == null) {
                                    postMessage("Password is updated")
                                    event.goBack()
                                } else {
                                    handleApiError(it as FirebaseAuthException)
                                }
                            }
                        )
                    } else {
                        _loginUiState.update {
                            it.copy(credentialError = "Password confirmation does not match")
                        }
                    }
                }
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
                        verificationScreenState = null
                    )
                }
            }

            is LoginEvent.ResetPassword -> {
                if (_loginUiState.value.email.length <= 50){
                    if (_loginUiState.value.email.isNotBlank()) {
                        accountService.resetPassword(_loginUiState.value.email) {
                            if (it == null) postMessage("Reset e-mail sent")
                            else handleApiError(it as FirebaseAuthException)
                        }
                    } else {
                        _loginUiState.update {
                            it.copy(emailEmpty = true)
                        }
                    }
                }
            }
        }
    }

    private fun reloadUser() {
        Firebase.auth.currentUser?.reload()?.addOnCompleteListener {
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
            "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_MISMATCH", "" -> "Incorrect e-mail or password."
            "ERROR_USER_NOT_FOUND" -> "A user with these credentials does not exist."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Try again later."
            "ERROR_WEAK_PASSWORD" -> "Password must be at least 6 characters long."
            "ERROR_INVALID_EMAIL" -> "Invalid e-mail."
            else -> "An error occurred."
        }
    }

}