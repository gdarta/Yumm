package lv.yumm.lists.service

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import lv.yumm.lists.data.UserList
import timber.log.Timber
import javax.inject.Inject

@Singleton
class ListServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ListService {
    companion object {
        private const val LISTS = "lists"
    }

    private val listCollection = firestore.collection(LISTS)

    override val userLists: Flow<List<UserList>>
        get() = listCollection
            .whereEqualTo("authorUID", Firebase.auth.currentUser?.uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .dataObjects<UserList>()

    private val _loading = MutableStateFlow(false)
    override val uploadingFlow: Flow<Boolean>
        get() = _loading

    override fun refreshUserLists(uid: String): Flow<List<UserList>> {
        return listCollection
            .whereEqualTo("authorUID", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .dataObjects<UserList>()
    }

    override suspend fun getList(id: String): UserList? {
        return Firebase.auth.currentUser?.let { user ->
            _loading.value = true
            if (id.isNotBlank()) {
                val list = listCollection.document(id).get()
                    .addOnCompleteListener {
                        _loading.value = false
                    }.await().toObject(UserList::class.java)
                if (list?.authorUID == user.uid) list else null
            } else null
        }
    }

    override suspend fun updateList(list: UserList, onResult: (Throwable?) -> Unit) {
        if (list.list.isNotEmpty()) { // do not save list if it is empty
            Firebase.auth.currentUser?.let { user -> // allow updates for lists only for authenticated users
                _loading.value = true
                if (list.id.isBlank()) {
                    val id = listCollection.add(list)
                        .await().id
                    listCollection.document(id).set(list.copy(id = id, updatedAt = Timestamp.now(), authorUID = user.uid))
                        .addOnCompleteListener {
                            _loading.value = false
                            onResult(it.exception)
                        }.await()
                } else {
                    listCollection.document(list.id).set(list.copy(updatedAt = Timestamp.now(), authorUID = user.uid))
                        .addOnCompleteListener {
                            _loading.value = false
                            onResult(it.exception)
                        }.await()
                }
            }
        } else {
            onResult(Exception("No items provided"))
        }
    }

    override suspend fun deleteList(id: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.currentUser?.let { user ->
            listCollection.document(id).delete().addOnCompleteListener {
                onResult(it.exception)
            }.await()
        }
    }

    override fun deleteListsByUserId(uid: String, onResult: (Throwable?) -> Unit) {
        // Perform deletion
        listCollection.whereEqualTo("authorUID", uid).get()
            .addOnSuccessListener { querySnapshot ->
                val batch = firestore.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                batch.commit().addOnCompleteListener {
                    onResult(it.exception)
                }
                .addOnSuccessListener {
                Timber.i("Successfully deleted all lists for user: $uid")
            }.addOnFailureListener { exception ->
                Timber.e("Error deleting records: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            onResult(exception)
            Timber.e("Error querying documents: ${exception.message}")
        }
    }
}