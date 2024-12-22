package lv.yumm.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.LogService
import lv.yumm.login.service.StorageService
import lv.yumm.login.ui.LoginEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun createAnonymousAccount() {
        accountService.createAnonymousAccount {  }
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
            is LoginEvent.LogIn -> {

            }
        }
    }

}