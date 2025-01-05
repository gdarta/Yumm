package lv.yumm.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import lv.yumm.lists.data.ListItem
import lv.yumm.lists.data.UserList
import lv.yumm.lists.service.ListService
import lv.yumm.recipes.data.Ingredient
import okhttp3.internal.toImmutableList

class FakeListService @Inject constructor(
    private val firestore: FirebaseFirestore
): ListService {
    companion object {
        val LIST_ID_1 = UserList(
            id = "1",
            authorUID = "user",
            title = "test1",
            list = listOf(
                ListItem(
                    checked = false,
                    Ingredient(
                        "siers",
                        2f,
                        "kg"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "piens",
                        1f,
                        "l"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "milti",
                        0.5f,
                        "kg"
                    )
                )
            )
        )
    }

    val userListDatabase = MutableStateFlow(listOf<UserList>(
        LIST_ID_1,
        UserList(
            id = "2",
            authorUID = "user",
            title = "test2",
            list = listOf(
                ListItem(
                    checked = false,
                    Ingredient(
                        "siers",
                        3f,
                        "kg"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "piens",
                        700f,
                        "ml"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "maize",
                        3f,
                        "klaipi"
                    )
                )
            )
        ),
        UserList(
            id = "3",
            authorUID = "user",
            title = "test3",
            list = listOf(
                ListItem(
                    checked = false,
                    Ingredient(
                        "cits siers",
                        2f,
                        "kg"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "cits piens",
                        1f,
                        "l"
                    )
                ),
                ListItem(
                    checked = false,
                    Ingredient(
                        "citi milti",
                        0.5f,
                        "kg"
                    )
                )
            )
        )
    ))

    override val uploadingFlow: Flow<Boolean> = flow { false }
    override val userLists: Flow<List<UserList>> = userListDatabase.asStateFlow()

    override fun refreshUserLists(uid: String): Flow<List<UserList>> {
        return userLists
    }

    override suspend fun getList(id: String): UserList? {
        return userListDatabase.value.find { it.id == id }
    }

    override suspend fun updateList(list: UserList, onResult: (Throwable?) -> Unit) {
        val elementToUpdate = userListDatabase.value.find { it.id == list.id }
        val userListMutable = userListDatabase.value.toMutableList()
        if (elementToUpdate != null) {  // if element already exists, replace it in list
            val indexToUpdate = userListDatabase.value.indexOf(elementToUpdate)
            userListMutable[indexToUpdate] = list
        } else { // if is not found, add new element
            userListMutable.add(list.copy(id = "4"))
        }
        userListDatabase.value = (userListMutable.toImmutableList())
    }

    override suspend fun deleteList(id: String, onResult: (Throwable?) -> Unit) {
        // do nothing
    }

    override fun deleteListsByUserId(uid: String, onResult: (Throwable?) -> Unit) {
        // do nothing
    }
}