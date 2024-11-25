@file:JvmName("RecipeNetworkDataSourceKt")

package lv.yumm.recipes.data.source.network

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import lv.yumm.recipes.data.Amount
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.Unit
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
                Ingredient("garlic cloves", Amount(3f, Unit.PIECE)),
                Ingredient("onions", Amount(2f, Unit.PIECE)),
                Ingredient("canned tomatoes", Amount(800f, Unit.MILLIGRAM)),
                Ingredient("lentils", Amount(100f, Unit.MILLIGRAM)),
                Ingredient("curry", Amount(2f, Unit.TBS)),
                Ingredient("chilli flakes", Amount(1f, Unit.TSP))
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
                Ingredient("cucumber", Amount(1f, Unit.PIECE)),
                Ingredient("tomatoes", Amount(4f, Unit.PIECE)),
                Ingredient("red onion", Amount(1f, Unit.PIECE)),
                Ingredient("olives", Amount(200f, Unit.GRAM)),
                Ingredient("salad cheese", Amount(150f, Unit.GRAM)),
                Ingredient("greek yogurt", Amount(400f, Unit.GRAM)),
                Ingredient("parsley", Amount(1f, Unit.TSP)),
                Ingredient("salt and pepper", Amount(1f, Unit.TSP)),
                Ingredient("basil", Amount(1f, Unit.TSP))
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
