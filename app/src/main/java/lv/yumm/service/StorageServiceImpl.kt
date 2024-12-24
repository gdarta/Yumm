package lv.yumm.service

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Recipe
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@Singleton
class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
    private val storage: FirebaseStorage
): StorageService {
    companion object {
        private const val RECIPES = "recipes"
        private const val SAVE_RECIPE_TRACE = "saveRecipe"
        private const val UPDATE_RECIPE_TRACE = "updateRecipe"
    }

    private val collection get() = firestore.collection(RECIPES)
        //todo .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

    override val recipes: Flow<List<Recipe>>
        get() = collection.dataObjects<Recipe>()

    private val uploading = MutableStateFlow<Boolean>(false)
    override val uploadingFlow: Flow<Boolean>
        get() = uploading

    override suspend fun getRecipe(id: String): Recipe? {
        return try {
            collection.document(id).get().await().toObject(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e("Error retrieving recipe with id: $id: ${e.message}")
            null
        }
    }

    override suspend fun insertRecipe(recipe: Recipe): Result<String> {
        //todo val updatedTask = task.copy(userId = auth.currentUserId)
        return try {
            uploading.emit(true)
            val tryUrl = getResizedImageUrl(recipe.imageUrl.toUri()).getOrThrow()
            val id = collection.add(recipe.copy(imageUrl = tryUrl.toString())).await().id
            uploading.emit(false)
            Result.success(id)
        } catch (e: Exception) {
            uploading.emit(false)
            Timber.e("Error adding recipe: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Throwable? {
        return try {
            uploading.emit(true)
            if (isContentUrl(recipe.imageUrl)) {
                val tryUrl = getResizedImageUrl(recipe.imageUrl.toUri()).getOrThrow()
                collection.document(recipe.id).set(recipe.copy(imageUrl = tryUrl.toString())).await()
            } else {
                collection.document(recipe.id).set(recipe).await()
            }
            uploading.emit(false)
            null
        } catch (e: Exception) {
            uploading.emit(false)
            Timber.e("Error updating recipe with id: ${recipe.id}, ${e.message}")
            e.cause
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Throwable? {
        return try {
            try {
                val photoStorageRef = storage.getReferenceFromUrl(recipe.imageUrl)
                photoStorageRef.delete().await()
            } catch (e: Exception) {
                Timber.e("Error deleting image resource: ${e.message}")
            }
            collection.document(recipe.id).delete().await()
            null
        } catch (e: Exception) {
            Timber.e("Error deleting recipe with id: ${recipe.id}, ${e.message}")
            e.cause
        }
    }

    override suspend fun uploadPhoto(uri: Uri): Throwable? {
        return try {
            uploading.emit(true)
            val storageRef = storage.reference.child("images/${uri.lastPathSegment}")
            storageRef.putFile(uri).await()
            uploading.emit(false)
            null
        } catch (e: Exception) {
            uploading.emit(false)
            Timber.e("Error uploading image: ${e.message}")
            e.cause
        }
    }

    private suspend fun getResizedImageUrl(uri: Uri): Result<Uri> {
        return try {
            uploading.emit(true)
            val resizedName = uri.lastPathSegment?.resizedName()
            val storageRef = storage.reference.child("images/${resizedName}")
            val downloadUrl = storageRef.downloadUrl.await()
            uploading.emit(false)
            Result.success(downloadUrl)
        } catch (e: Exception) {
            uploading.emit(false)
            Timber.e("Failure in getting resized image url: ${e.message}")
            Result.failure(e)
        }
    }

    private fun isContentUrl(url: String): Boolean {
        return url.startsWith("content://")
    }

    private fun isStorageUrl(url: String): Boolean {
        return url.startsWith("https://")
    }

    private fun String.resizedName(): String {
        val extIndex = this.lastIndexOf('.')
        return if (extIndex != -1) "${this.substring(0, extIndex)}_200x200${this.substring(extIndex)}" else "${this}_200x200"
    }
}