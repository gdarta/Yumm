package lv.yumm

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import lv.yumm.recipes.data.source.LocalRecipe
import lv.yumm.recipes.data.source.RecipeDatabase
import org.junit.Before
import org.junit.Test

class RecipeDaoTest {

    private lateinit var database: RecipeDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertTaskAndGetTasks() = runTest {

        val recipe = LocalRecipe(
            title = "title",
            description = "description",
            id = "id",
        )

        database.recipeDao().upsert(recipe)

        val recipes = database.recipeDao().observeAll().first()

        assertEquals(1, recipes.size)
    }
}