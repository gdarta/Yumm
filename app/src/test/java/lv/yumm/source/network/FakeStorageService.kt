package lv.yumm.source.network

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.service.StorageService
import javax.inject.Inject

class FakeStorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
    private val storage: FirebaseStorage
): StorageService  {
    override val recipes: Flow<List<Recipe>>
        get() = flow { recipesFromDatabase }

    private var recipesFromDatabase = arrayOf<Recipe>(
        Recipe(
            id = "1",
            title = "Recipe 1",
            description = "Description 1",
            directions = listOf("Direction 1", "Direction 2"),
            complexity = 3,
            portions = 2,
            ingredients = listOf(Ingredient("Ingredient 1"), Ingredient("Ingredient 2")),
            imageUrl = "example.image.url"
        ),
        Recipe(
            id = "2",
            title = "Recipe 2",
            description = "Description 2",
            directions = listOf("Direction 1", "Direction 2"),
            complexity = 3,
            portions = 2,
            ingredients = listOf(Ingredient("Ingredient 1"), Ingredient("Ingredient 2")),
            imageUrl = "example.image.url"
        ),
        Recipe(
            id = "3",
            title = "Recipe 3",
            description = "Description 3",
            directions = listOf("Direction 1", "Direction 2"),
            complexity = 3,
            portions = 2,
            ingredients = listOf(Ingredient("Ingredient 1"), Ingredient("Ingredient 2")),
            imageUrl = "example.image.url"
        ),
    )

    override val uploadingFlow: Flow<Boolean>
        get() = flow { false }

    override suspend fun getRecipe(id: String): Recipe? {
        return recipesFromDatabase.find { it.id == id }
    }

    override suspend fun insertRecipe(recipe: Recipe): Result<String> {
        return try {
            recipesFromDatabase + recipe
            Result.success(recipesFromDatabase.size.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Throwable? {
        return try {
            val recipeToUpdate = recipesFromDatabase.find { it.id == recipe.id }
            recipeToUpdate?.let {
                recipesFromDatabase[recipesFromDatabase.indexOf(recipeToUpdate)] = recipe
                null
            } ?: throw Exception("ID does not exist")
        } catch (e: Exception) {
            e
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Throwable? {
        return try {
            val recipeToUpdate = recipesFromDatabase.find { it.id == recipe.id }
            recipeToUpdate?.let {
                recipesFromDatabase = recipesFromDatabase.filter {
                    it.id != recipe.id
                }.toTypedArray()
                null
            } ?: throw Exception("ID does not exist")
        } catch (e: Exception) {
            e
        }
    }

    override suspend fun uploadPhoto(uri: Uri): Throwable? {
        // do nothing
        return null
    }
}