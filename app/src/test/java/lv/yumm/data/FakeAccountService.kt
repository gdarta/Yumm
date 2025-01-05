package lv.yumm.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lv.yumm.lists.service.ListService
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.service.StorageService
import javax.inject.Inject

class FakeAccountService @Inject constructor(
    private val auth: FirebaseAuth,
    private val recipes: StorageService,
    private val lists: ListService
) : AccountService {
    override val loading: Flow<Boolean>
        get() = flow { false }
    override val currentUser: Flow<String>
        get() = flow { "user" }

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun authenticate(
        email: String,
        password: String,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun linkAccount(
        email: String,
        password: String,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun registerAccount(
        email: String,
        password: String,
        displayName: String,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

    override fun deleteAccount(
        email: String,
        password: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun editDisplayName(
        name: String,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun editEmail(
        oldEmail: String,
        newEmail: String,
        password: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun verifyBeforeUpdateEmail(
        email: String,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        email: String,
        password: String,
        newPassword: String,
        onReAuthenticate: (Throwable?) -> Unit,
        onResult: (Throwable?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(email: String, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }
}