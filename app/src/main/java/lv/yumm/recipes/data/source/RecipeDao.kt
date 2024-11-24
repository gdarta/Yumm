package lv.yumm.recipes.data.source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun observeAll(): Flow<List<LocalRecipe>>

    @Upsert
    suspend fun upsert(task: LocalRecipe)

    @Upsert
    suspend fun upsertAll(tasks: List<LocalRecipe>)

//    @Query("UPDATE recipe SET isCompleted = :completed WHERE id = :taskId")
//    suspend fun updateCompleted(taskId: String, completed: Boolean)

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()
}