package lv.yumm.login.service

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AccountService {

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInAnonymously()
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email.filterNot { it.isWhitespace() }, password.filterNot { it.isWhitespace() })
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email.filterNot { it.isWhitespace() }, password.filterNot { it.isWhitespace() })

        Firebase.auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun registerAccount(email: String, password: String, displayName: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email.filterNot { it.isWhitespace() }, password.filterNot { it.isWhitespace() })
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val update = userProfileChangeRequest { setDisplayName(displayName.filterNot { it.isWhitespace() }) }
                    Firebase.auth.currentUser?.updateProfile(update)?.addOnCompleteListener {
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

    override fun deleteAccount(email: String, password: String, onReAuthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email.filterNot { it.isWhitespace() }, password.filterNot { it.isWhitespace() })
        val user = Firebase.auth.currentUser

        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    user.delete().addOnCompleteListener{
                        onResult(it.exception)
                    }
                } else { onReAuthenticate(it.exception) }
            }
    }

    override fun editDisplayName(name: String, onResult: (Throwable?) -> Unit) {
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name.filterNot { it.isWhitespace() }
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                user.reload()
                onResult(task.exception)
            }
    }

    override fun editEmail(oldEmail: String, newEmail: String, password: String, onReAuthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(oldEmail.filterNot { it.isWhitespace() }, password.filterNot { it.isWhitespace() })
        val user = Firebase.auth.currentUser

        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    verifyBeforeUpdateEmail(newEmail.filterNot { it.isWhitespace() }) { error ->
                        onResult(error)
                    }
                } else { onReAuthenticate(it.exception) }
            }
    }

    override fun verifyBeforeUpdateEmail(email: String, onResult: (Throwable?) -> Unit) {
        Timber.d("verify before update email")
        val user = Firebase.auth.currentUser
        user?.verifyBeforeUpdateEmail(email.filterNot { it.isWhitespace() })?.addOnCompleteListener {
            Timber.d("verify before update email done")
            onResult(it.exception)
        }
    }
}