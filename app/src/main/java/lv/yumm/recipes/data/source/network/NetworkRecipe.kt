package lv.yumm.recipes.data.source.network

import androidx.room.PrimaryKey
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.RecipeType

data class NetworkRecipe (
    @PrimaryKey val id: String,
    val title: String = "",
    val description: String = "",
    val directions: List<String> = emptyList(),
    val complexity: Int = -1,
    val duration: Int = 0,
    val portions: Int = 0,
    val imageUrl: String = "",
    val type: RecipeType? = null,
    val ingredients: List<Ingredient> = emptyList<Ingredient>(),
)