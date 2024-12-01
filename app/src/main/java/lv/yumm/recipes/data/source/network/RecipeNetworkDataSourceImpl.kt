@file:JvmName("RecipeNetworkDataSourceKt")

package lv.yumm.recipes.data.source.network

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Ingredient.Companion.GRAM
import lv.yumm.recipes.data.Ingredient.Companion.MILLIGRAM
import lv.yumm.recipes.data.Ingredient.Companion.PIECE
import lv.yumm.recipes.data.Ingredient.Companion.TBS
import lv.yumm.recipes.data.Ingredient.Companion.TSP
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.source.network.RecipeNetworkDataSource.Companion.SERVICE_LATENCY_IN_MILLIS

class RecipeNetworkDataSourceImpl @Inject constructor() : RecipeNetworkDataSource {

    // A mutex is used to ensure that reads and writes are thread-safe.
    override val accessMutex = Mutex()
    override var recipes = listOf(
        NetworkRecipe(
            id = "LENTILS",
            title = "Best Lentil Soup Recipe",
            description = "I got this recipe from my mother. She got it from her mother.",
            directions = listOf(
                "Cook garlic and onion in pot",
                "Add tomatoes and spices",
                "Add lentils",
                "Boil for 20 minutes"
            ),
            complexity = 1,
            duration = 40*60000,
            imageUrl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.connoisseurusveg.com%2Fmediterranean-red-lentil-soup%2F&psig=AOvVaw3hSo_uoOQDfjvKj4YnXAa6&ust=1732561864115000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCODMtIfW9YkDFQAAAAAdAAAAABAE",
            type = RecipeType.DINNER,
            ingredients = listOf(
                Ingredient("garlic cloves", 3f, PIECE),
                Ingredient("onions", 2f, PIECE),
                Ingredient("canned tomatoes", 800f, MILLIGRAM),
                Ingredient("lentils", 100f, MILLIGRAM),
                Ingredient("curry", 2f, TBS),
                Ingredient("chilli flakes", 1f, TSP)
            ),
            portions = 4,
        ),
        NetworkRecipe(
            id = "GREEK",
            title = "Greek Salad Yum Yummy",
            description = "I would love to go to Greece someday! Alas, I can make this salad at home.",
            directions = listOf(
                "Finely cut up your veggies and combine.",
                "For the tzatziki sauce, combine greek yogurt, 100 grams of salad cheese, lemon juice and your spices.",
                "Cut up olives in half",
                "Combine everything and garnish with salad cheese.",
            ),
            complexity = 2,
            duration = 30*60000,
            portions = 4,
            type = RecipeType.SALAD,
            ingredients = listOf(
                Ingredient("cucumber", 1f, PIECE),
                Ingredient("tomatoes", 4f, PIECE),
                Ingredient("red onion", 1f, PIECE),
                Ingredient("olives", 200f, GRAM),
                Ingredient("salad cheese", 150f, GRAM),
                Ingredient("greek yogurt", 400f, GRAM),
                Ingredient("parsley", 1f, TSP),
                Ingredient("salt and pepper", 1f, TSP),
                Ingredient("basil", 1f, TSP)
            )
        )
    )

    override suspend fun loadRecipes(): List<NetworkRecipe> = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return recipes
    }

    override suspend fun saveRecipes(newRecipes: List<NetworkRecipe>) = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        recipes = recipes + newRecipes
    }
}
