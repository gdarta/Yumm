package lv.yumm.recipes.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import lv.yumm.recipes.data.source.RecipeDao
import lv.yumm.recipes.data.source.RecipeDatabase
import javax.inject.Singleton

class DataModules {
    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @Singleton
        @Provides
        fun provideDataBase(@ApplicationContext context: Context): RecipeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                RecipeDatabase::class.java,
                "Recipes.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideRecipeDao(database: RecipeDatabase) : RecipeDao = database.recipeDao()
    }
}