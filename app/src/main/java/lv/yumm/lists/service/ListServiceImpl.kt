package lv.yumm.lists.service

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import lv.yumm.lists.data.UserList
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Recipe
import lv.yumm.service.StorageService
import javax.inject.Inject

@Singleton
class ListServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
): ListService {
    companion object {
        private const val LISTS = "lists"
    }

    private val listCollection = firestore.collection(LISTS)

    override val userLists: Flow<List<UserList>>
        get() = listCollection.document(Firebase.auth.currentUser?.uid ?: "col").collection(LISTS)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .dataObjects<UserList>()

    private val _loading = MutableStateFlow(false)
    override val uploadingFlow: Flow<Boolean>
        get() = _loading

    override fun refreshUserLists(uid: String): Flow<List<UserList>> {
        return listCollection.document(uid).collection(LISTS).dataObjects<UserList>()
    }

    override suspend fun updateList(list: UserList, onResult: (Throwable?) -> Unit) {
        Firebase.auth.currentUser?.let { user ->
            val userLists = listCollection.document(user.uid).collection(LISTS)
            if (list.id.isBlank()) {
                val id = userLists.add(list)
                    .await().id
                userLists.document(id).set(list.copy(id = id, updatedAt = Timestamp.now())).addOnCompleteListener {
                    onResult(it.exception)
                }.await()
            } else {
                userLists.document(list.id).set(list.copy(updatedAt = Timestamp.now())).addOnCompleteListener {
                    onResult(it.exception)
                }.await()
            }
        }
    }
}