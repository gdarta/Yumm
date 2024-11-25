package lv.yumm.source.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import lv.yumm.recipes.data.source.LocalRecipe
import lv.yumm.recipes.data.source.RecipeDao
import lv.yumm.recipes.data.source.network.NetworkRecipe
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSource
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSource.Companion.SERVICE_LATENCY_IN_MILLIS

class FakeNetworkDataSource: RecipeNetworkDataSource {
    override val accessMutex = Mutex()

    override var recipes: List<NetworkRecipe> = emptyList()

    override suspend fun loadRecipes(): List<NetworkRecipe> {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return recipes
    }

    override suspend fun saveRecipes(newRecipes: List<NetworkRecipe>) {
        delay(SERVICE_LATENCY_IN_MILLIS)
        recipes = recipes + newRecipes
    }
}