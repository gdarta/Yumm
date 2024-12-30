package lv.yumm.lists.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Singleton
import lv.yumm.login.service.AccountService
import lv.yumm.service.StorageService
import javax.inject.Inject

@Singleton
class ListServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
): ListService {

}