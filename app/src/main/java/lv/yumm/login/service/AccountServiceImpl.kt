package lv.yumm.login.service

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AccountService {

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInAnonymously()
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        Firebase.auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun registerAccount(email: String, password: String, displayName: String?, onResult: (Throwable?) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val update = userProfileChangeRequest { setDisplayName(displayName ?: "User") }
                    Firebase.auth.currentUser!!.updateProfile(update).addOnCompleteListener {
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
        val credential = EmailAuthProvider.getCredential(email, password)

        Firebase.auth.currentUser?.reauthenticate(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Firebase.auth.currentUser!!.delete().addOnCompleteListener{
                        onResult(it.exception)
                    }
                } else { onReAuthenticate(it.exception) }
            }
    }

    override fun editDisplayName(name: String, onResult: (Throwable?) -> Unit) {
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                onResult(task.exception)
            }
    }
}