package lv.yumm.service

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Recipe
import timber.log.Timber
import javax.inject.Inject

@Singleton
class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
    private val storage: FirebaseStorage
): StorageService {
    companion object {
        private const val RECIPES = "recipes"
        private const val USERS = "users"
        private const val PUBLIC_RECIPES = "public_recipes"
        private const val PUBLIC = "public"
        private const val PRIVATE = "private"
        private const val SAVE_RECIPE_TRACE = "saveRecipe"
        private const val UPDATE_RECIPE_TRACE = "updateRecipe"
    }

    private val collection get() = firestore.collection(RECIPES)
        //todo .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

    private val userCollection get() = firestore.collection(RECIPES).document(USERS).collection(Firebase.auth.currentUser?.uid ?: "col")

    private val publicCollection get() = firestore.collection(PUBLIC_RECIPES)

    override fun refreshUserRecipes(uid: String): Flow<List<Recipe>> {
        return firestore.collection(RECIPES).document(USERS).collection(uid).dataObjects<Recipe>()
    }
    override val userRecipes: Flow<List<Recipe>>
        get() = userCollection.dataObjects<Recipe>()

    override val publicRecipes: Flow<List<Recipe>>
        get() = publicCollection.dataObjects<Recipe>()

    private val uploading = MutableStateFlow<Boolean>(false)
    override val uploadingFlow: Flow<Boolean>
        get() = uploading

    override suspend fun getUserRecipe(id: String): Recipe? {
        return try {
            uploading.value = true
            val recipe = userCollection.document(id).get().addOnCompleteListener{
                uploading.value = false
            }.await().toObject(Recipe::class.java)
            recipe
        } catch (e: Exception) {
            uploading.value = false
            Timber.e("Error retrieving recipe with id: $id: ${e.message}")
            null
        }
    }

    override suspend fun getPublicRecipe(id: String): Recipe? {
        return try {
            uploading.value = true
            val recipe = publicCollection.document(id).get().addOnCompleteListener{
                uploading.value = false
            }.await().toObject(Recipe::class.java)
            recipe
        } catch (e: Exception) {
            uploading.value = false
            Timber.e("Error retrieving recipe with id: $id: ${e.message}")
            null
        }
    }

    // adds a new recipe, onResult is called three times: once for when url for image is retrieved,
    // once for when the recipe is saved and once for publishing it, if it is set to be public
    override  suspend fun insertRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit) {
        var updatedRecipe = recipe.copy(authorUID = Firebase.auth.currentUser?.uid ?: "col") // set user id for recipe
        uploading.emit(true)
        getResizedImageUrl(recipe.imageUrl.toUri()) { uri, error ->
            onResult(error)
            if (error == null)
                updatedRecipe = updatedRecipe.copy(imageUrl = uri.toString()) // set url for image
        }
        userCollection.add(updatedRecipe).addOnCompleteListener {
            updatedRecipe = updatedRecipe.copy(id = it.result.id)
        }.await()
        updateRecipe(updatedRecipe) { error -> // set id for recipe after upload
            onResult(error)
            if (error != null) {
                Timber.e("Error setting id for recipe: ${error.message}")
            } else if (recipe.public) {
                publishRecipe(updatedRecipe) {
                    onResult(it)
                }
            }
        }
        uploading.emit(false)
    }

    // updating given recipe if it exists, onResult is called twice if recipe is set to be public
    override suspend fun updateRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit) {
        uploading.emit(true)
        var updatedRecipe = recipe.copy(authorUID = Firebase.auth.currentUser?.uid ?: "col")
        if (isContentUrl(recipe.imageUrl)) {
            getResizedImageUrl(recipe.imageUrl.toUri()) { uri, error ->
                // if new image, get download url
                if (error == null)
                    updatedRecipe = updatedRecipe.copy(imageUrl = uri.toString())
            }
        }
        userCollection.document(recipe.id).set(updatedRecipe).addOnCompleteListener {
            onResult(it.exception)
            if (it.exception != null) {
                Timber.e("Error updating recipe with id: ${recipe.id}, ${it.exception?.message}")
            } else if (recipe.public) {
                publishRecipe(updatedRecipe) {
                    if (it != null) Timber.e("Error publishing recipe with id: ${recipe.id}, ${it.message}")
                    onResult(it)
                }
            }
        }
        uploading.emit(false)
    }

    // deletes recipe, returns error if action failed
    // if deleting image did not succeed, does not throw error
    override suspend fun deleteRecipe(recipe: Recipe): Throwable? {
        return try {
            try {
                val photoStorageRef = storage.getReferenceFromUrl(recipe.imageUrl)
                photoStorageRef.delete().await()
            } catch (e: Exception) {
                Timber.e("Error deleting image resource: ${e.message}")
            }
            userCollection.document(recipe.id).delete().addOnCompleteListener{
                Timber.d("Deleted private recipe with ${it.exception} and ${recipe}")
                if (recipe.public) publicCollection.document(recipe.id).delete().addOnCompleteListener{
                    Timber.d("Completed deleting public recipe with: ${it.exception}")
                }
            }.await()
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

    override fun publishRecipe(recipe: Recipe, onResult: (Throwable?) -> Unit) {
        publicCollection.document(recipe.id).set(recipe).addOnCompleteListener {
            onResult(it.exception)
        }
    }

    private suspend fun getResizedImageUrl(uri: Uri, onResult: (Uri?, Throwable?) -> Unit) {
        uploading.emit(true)
        val resizedName = uri.lastPathSegment?.resizedName()
        val storageRef = storage.reference.child("images/${resizedName}")
        storageRef.downloadUrl.addOnCompleteListener {
            uploading.value = false
            if (it.isSuccessful) {
                onResult(it.result, null)
            } else {
                onResult(null, it.exception)
                Timber.e("Failure in getting resized image url: ${it.exception?.message}")
            }
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