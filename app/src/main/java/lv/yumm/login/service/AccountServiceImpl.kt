package lv.yumm.login.service

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.text.isWhitespace

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AccountService {
    companion object{
        const val EMPTY_USER_ID = "col"
    }

    private val _loading = MutableStateFlow<Boolean>(false)
    override val loading: Flow<Boolean>
        get() = _loading

    override val currentUser: Flow<String>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.uid ?: EMPTY_USER_ID)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener {
                _loading.value = false
                onResult(it.exception)
            }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        _loading.value = true
        auth.signInWithEmailAndPassword(
            email.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })
            .addOnCompleteListener {
                _loading.value = false
                onResult(it.exception)
            }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        _loading.value = true
        val credential = EmailAuthProvider.getCredential(email.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })

        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener {
                _loading.value = false
                onResult(it.exception)
            }
    }

    override fun registerAccount(
        email: String,
        password: String,
        displayName: String,
        onResult: (Throwable?) -> Unit
    ) {
        _loading.value = true
        auth.createUserWithEmailAndPassword(
            email.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })
            .addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    val update =
                        userProfileChangeRequest { setDisplayName(displayName.filterNot { it.isWhitespace() }) }
                    auth.currentUser?.updateProfile(update)?.addOnCompleteListener {
                        onResult(it.exception)
                    }
                } else {
                    onResult(it.exception)
                }
            }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    override fun deleteAccount(
        email: String,
        password: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        _loading.value = true
        val credential = EmailAuthProvider.getCredential(email.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })
        val user = Firebase.auth.currentUser

        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    user.delete().addOnCompleteListener {
                        onResult(it.exception)
                    }
                } else {
                    onReAuthenticate(it.exception)
                }
            }
    }

    override fun editDisplayName(name: String, onResult: (Throwable?) -> Unit) {
        _loading.value = true
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name.filterNot { it.isWhitespace() }
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                _loading.value = false
                user.reload()
                onResult(task.exception)
            }
    }

    override fun editEmail(
        oldEmail: String,
        newEmail: String,
        password: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        _loading.value = true
        val credential = EmailAuthProvider.getCredential(oldEmail.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })
        val user = Firebase.auth.currentUser

        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    verifyBeforeUpdateEmail(newEmail.filterNot { it.isWhitespace() }) { error ->
                        onResult(error)
                    }
                } else {
                    onReAuthenticate(it.exception)
                }
            }
    }

    override fun verifyBeforeUpdateEmail(email: String, onResult: (Throwable?) -> Unit) {
        _loading.value = true
        val user = Firebase.auth.currentUser
        user?.verifyBeforeUpdateEmail(email.filterNot { it.isWhitespace() })
            ?.addOnCompleteListener {
                onResult(it.exception)
                _loading.value = false
            }
    }

    override fun changePassword(
        email: String,
        password: String,
        newPassword: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        _loading.value = true
        val credential = EmailAuthProvider.getCredential(email.filterNot { it.isWhitespace() },
            password.filterNot { it.isWhitespace() })
        val user = Firebase.auth.currentUser

        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    user.updatePassword(newPassword.filterNot { it.isWhitespace() })
                        .addOnCompleteListener { error ->
                            onResult(it.exception)
                        }
                } else {
                    onReAuthenticate(it.exception)
                }
            }
    }

    override fun resetPassword(email: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onResult(task.exception)
            }
    }
}