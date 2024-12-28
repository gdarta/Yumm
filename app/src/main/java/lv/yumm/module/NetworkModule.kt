package lv.yumm.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSource
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRecipeNetworkDataSource(): RecipeNetworkDataSource {
        return RecipeNetworkDataSourceImpl()
    }
}