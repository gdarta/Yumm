package lv.yumm.source.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import lv.yumm.recipes.data.source.LocalRecipe
import lv.yumm.recipes.data.source.RecipeDao

class FakeRecipeDao(initialRecipes: List<LocalRecipe>) : RecipeDao {

    private val _recipes = initialRecipes.toMutableList()
    private val recipesStream = MutableStateFlow(_recipes.toList())

    override fun observeAll(): Flow<List<LocalRecipe>> = recipesStream
    override suspend fun insert(recipe: LocalRecipe): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(recipe: LocalRecipe) {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(task: LocalRecipe) {
        _recipes.removeIf { it.id == task.id }
        _recipes.add(task)
        recipesStream.emit(_recipes)
    }

    override suspend fun upsertAll(recipes: List<LocalRecipe>) {
        val newRecipeIds = recipes.map { it.id }
        _recipes.removeIf { newRecipeIds.contains(it.id) }
        _recipes.addAll(recipes)
    }

    override suspend fun getRecipe(id: Long): LocalRecipe {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        _recipes.clear()
        recipesStream.emit(_recipes)
    }
}