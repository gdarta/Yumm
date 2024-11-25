package lv.yumm.recipes.data.source.network

import kotlinx.coroutines.sync.Mutex

interface RecipeNetworkDataSource {
    companion object {
        const val SERVICE_LATENCY_IN_MILLIS = 2000L
    }

    val accessMutex: Mutex

    var recipes: List<NetworkRecipe>

    suspend fun loadRecipes(): List<NetworkRecipe>

    suspend fun saveRecipes(newRecipes: List<NetworkRecipe>)
}