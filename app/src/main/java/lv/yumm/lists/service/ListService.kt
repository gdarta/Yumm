package lv.yumm.lists.service

import kotlinx.coroutines.flow.Flow
import lv.yumm.lists.data.UserList

interface ListService {
    val uploadingFlow: Flow<Boolean>
    val userLists: Flow<List<UserList>>

    fun refreshUserLists(uid: String): Flow<List<UserList>>

    suspend fun getList(id: String): UserList?
    suspend fun updateList(list: UserList, onResult: (Throwable?) -> Unit)
    suspend fun deleteList(id: String, onResult: (Throwable?) -> Unit)

    fun deleteListsByUserId(uid: String, onResult: (Throwable?) -> Unit)
}