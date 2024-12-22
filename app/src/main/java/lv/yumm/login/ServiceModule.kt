package lv.yumm.login

import dagger.Module
import dagger.Binds
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.AccountServiceImpl
import lv.yumm.login.service.LogService
import lv.yumm.login.service.LogServiceImpl
import lv.yumm.login.service.StorageService
import lv.yumm.login.service.StorageServiceImpl

@Module
@InstallIn(ViewModelComponent::class)
object ServiceModule {
    @Provides
    fun provideStorageService(): StorageService {
        return StorageServiceImpl()
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