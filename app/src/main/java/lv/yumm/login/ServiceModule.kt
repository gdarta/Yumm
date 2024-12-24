package lv.yumm.login

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
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.AccountServiceImpl
import lv.yumm.service.LogService
import lv.yumm.service.LogServiceImpl
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
    fun provideStorageService(
        firestore: FirebaseFirestore,
        auth: AccountService,
        storage: FirebaseStorage
    ): StorageService {
        return StorageServiceImpl(firestore, auth, storage)
    }

    @Provides
    fun provideAccountService(): AccountService {
        return AccountServiceImpl()
    }

    @Provides
    fun provideLogService(impl: LogServiceImpl): LogService {
        return LogServiceImpl()
    }

}