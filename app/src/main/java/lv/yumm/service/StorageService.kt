package lv.yumm.service

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import lv.yumm.recipes.data.Recipe

interface StorageService {
    val recipes: Flow<List<Recipe>>
    val uploadingFlow: Flow<Boolean>

    suspend fun getRecipe(id: String): Recipe?
    suspend fun insertRecipe(recipe: Recipe): Result<String>
    suspend fun updateRecipe(recipe: Recipe): Throwable?
    suspend fun deleteRecipe(recipe: Recipe): Throwable?
    suspend fun uploadPhoto(uri: Uri): Throwable?
}