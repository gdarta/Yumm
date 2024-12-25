package lv.yumm.login.service

interface AccountService {
    fun createAnonymousAccount(onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun registerAccount(email: String, password: String, displayName: String, onResult: (Throwable?) -> Unit)
    fun signOut()
    fun deleteAccount(email: String, password: String, onReauthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit)
}