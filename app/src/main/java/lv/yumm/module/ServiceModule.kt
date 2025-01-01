package lv.yumm.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import lv.yumm.lists.service.ListService
import lv.yumm.lists.service.ListServiceImpl
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.AccountServiceImpl
import lv.yumm.service.StorageService
import lv.yumm.service.StorageServiceImpl

@Module
@InstallIn(ViewModelComponent::class)
object ServiceModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun provideListService(
        firestore: FirebaseFirestore,
        auth: AccountService
    ): ListService {
        return ListServiceImpl(firestore, auth)
    }

    @Provides
    fun provideStorageService(
        firestore: FirebaseFirestore,
        auth: AccountService,
        storage: FirebaseStorage
    ): StorageService {
        return StorageServiceImpl(firestore, auth, storage)
    }

    @Provides
    fun provideAccountService(
        auth: FirebaseAuth
    ): AccountService {
        return AccountServiceImpl(auth)
    }
}