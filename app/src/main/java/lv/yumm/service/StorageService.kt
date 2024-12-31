package lv.yumm.service

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import lv.yumm.recipes.data.Recipe

interface StorageService {
    val userRecipes: Flow<List<Recipe>>
    val publicRecipes: Flow<List<Recipe>>
    val uploadingFlow: Flow<Boolean>

    fun refreshUserRecipes(uid: String): Flow<List<Recipe>>

    suspend fun searchPublicRecipes(searchPhrase: String): Flow<List<Recipe>>
    suspend fun searchUserRecipes(searchPhrase: String): Flow<List<Recipe>>

    suspend fun getPublicRecipe(id: String): Recipe?
    suspend fun getUserRecipe(id: String): Recipe?

    suspend fun updateRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit)
    suspend fun insertRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit)
    suspend fun deleteRecipe(recipe: Recipe): Throwable?
    suspend fun uploadPhoto(uri: Uri): Throwable?
    fun publishRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit)
}