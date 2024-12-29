package lv.yumm.login.service

import kotlinx.coroutines.flow.Flow

interface AccountService {
    val loading: Flow<Boolean>
    fun createAnonymousAccount(onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun registerAccount(email: String, password: String, displayName: String, onResult: (Throwable?) -> Unit)
    fun signOut()
    fun deleteAccount(email: String, password: String, onReAuthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit)
    fun editDisplayName(name: String, onResult: (Throwable?) -> Unit)
    fun editEmail(oldEmail: String, newEmail: String, password: String, onReAuthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit)
    fun verifyBeforeUpdateEmail(email: String, onResult: (Throwable?) -> Unit)
    fun changePassword(
        email: String,
        password: String,
        newPassword: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    )
}