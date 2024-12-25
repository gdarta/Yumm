package lv.yumm.login.service

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AccountService {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun registerAccount(email: String, password: String, displayName: String?, onResult: (Throwable?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val update = userProfileChangeRequest { setDisplayName(displayName) }
                    auth.currentUser!!.updateProfile(update).addOnCompleteListener {
                        onResult(it.exception)
                    }
                } else {
                    onResult(it.exception)
                }
            }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun deleteAccount(email: String, password: String, onReauthenticate: (Throwable?) -> Unit, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        auth.currentUser?.reauthenticate(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser!!.delete().addOnCompleteListener{
                        onResult(it.exception)
                    }
                } else { onReauthenticate(it.exception) }
            }
    }
}