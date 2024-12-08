package lv.yumm.recipes.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun observeAll(): Flow<List<LocalRecipe>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recipe: LocalRecipe) : Long

    @Delete
    suspend fun delete(recipe: LocalRecipe)

    @Upsert
    suspend fun upsert(recipe: LocalRecipe)

    @Upsert
    suspend fun upsertAll(recipes: List<LocalRecipe>)

    @Query("SELECT * from recipe WHERE id = :id")
    suspend fun getRecipe(id: Long): LocalRecipe

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()
}