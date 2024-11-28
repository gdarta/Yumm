package lv.yumm.source


import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import lv.yumm.recipes.data.DefaultRecipeRepository
import lv.yumm.recipes.data.source.LocalRecipe
import lv.yumm.recipes.data.source.network.NetworkRecipe
import lv.yumm.recipes.data.source.toExternal
import lv.yumm.recipes.data.source.toLocal
import lv.yumm.source.local.FakeRecipeDao
import lv.yumm.source.network.FakeNetworkDataSource
import org.junit.Test

class DefaultRecipeRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    private val localRecipes = listOf(
        LocalRecipe(id = "1", title = "title1", description = "description1"),
        LocalRecipe(id = "2", title = "title2", description = "description2"),
    )

    private val localDataSource = FakeRecipeDao(localRecipes)
    private val networkDataSource = FakeNetworkDataSource()
    private val recipeRepository = DefaultRecipeRepository(
        localDataSource = localDataSource,
        networkDataSource = networkDataSource,
        dispatcher = testDispatcher,
        scope = testScope
    )

    @Test
    fun observeAll_exposesLocalData() = runTest {
        val recipes = recipeRepository.observeAll().first()
        assertEquals(localRecipes.toExternal(), recipes)
    }

    @Test
    fun onRecipeCreation_localAndNetworkAreUpdated() = testScope.runTest {
        val newRecipeId = recipeRepository.create(
            localRecipes[0].title,
            localRecipes[0].description
        )

        val localRecipes = localDataSource.observeAll().first()
        assertEquals(true, localRecipes.map { it.id }.contains(newRecipeId))

        val networkRecipes = networkDataSource.loadRecipes()
        assertEquals(true, networkRecipes.map { it.id }.contains(newRecipeId))
    }

    @Test
    fun onRefresh_localIsEqualToNetwork() = runTest {
        val networkRecipes = listOf(
            NetworkRecipe(id = "3", title = "title3", description = "desc3"),
            NetworkRecipe(id = "4", title = "title4", description = "desc4"),
        )
        networkDataSource.saveRecipes(networkRecipes)

        recipeRepository.refresh()

        assertEquals(networkRecipes.toLocal(), localDataSource.observeAll().first())
    }
}