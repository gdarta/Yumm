package lv.yumm.lists.service

import kotlinx.coroutines.flow.Flow
import lv.yumm.lists.data.UserList

interface ListService {
    val uploadingFlow: Flow<Boolean>

    suspend fun updateList(list: UserList, onResult: (Throwable?) -> Unit)
}