package lv.yumm.recipes.data

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lv.yumm.recipes.data.di.ApplicationScope
import lv.yumm.recipes.data.di.DefaultDispatcher
import lv.yumm.recipes.data.source.RecipeDao
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSource
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSourceImpl
import lv.yumm.recipes.data.source.toExternal
import lv.yumm.recipes.data.source.toLocal
import lv.yumm.recipes.data.source.toNetwork
import java.util.UUID

class DefaultRecipeRepository @Inject constructor(
    private val localDataSource: RecipeDao,
    private val networkDataSource: RecipeNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) {
    fun observeAll() : Flow<List<Recipe>> {
        return localDataSource.observeAll().map { recipes ->
            recipes.toExternal()
        }
    }

    suspend fun create(
        title: String,
        description: String = "",
        directions: List<String> = emptyList(),
        complexity: Int = -1,
        duration: Long = 0,
        imageUrl: String = "",
        type: RecipeType? = null,
        ingredients: List<Ingredient> = emptyList(),
    ): String {
        val recipeId = withContext(dispatcher) { createId() }
        val recipe = Recipe(
            id = recipeId,
            title = title,
            description = description,
            directions = directions,
            complexity = complexity,
            duration = duration,
            imageUrl = imageUrl,
            type = type,
            ingredients = ingredients,
        )
        localDataSource.upsert(recipe.toLocal())
        saveRecipesToNetwork()
        return recipeId
    }

    suspend fun refresh() {
        val networkRecipes = networkDataSource.loadRecipes()
        localDataSource.deleteAll()
        val localRecipes = withContext(dispatcher) {
            networkRecipes.toLocal()
        }
        localDataSource.upsertAll(localRecipes)
    }

    private suspend fun saveRecipesToNetwork() {
        scope.launch {
            val localRecipes = localDataSource.observeAll().first()
            val networkRecipes = withContext(dispatcher) {
                localRecipes.toNetwork()
            }
            networkDataSource.saveRecipes(networkRecipes)
        }
    }

    private fun createId() : String {
        return UUID.randomUUID().toString()
    }
}