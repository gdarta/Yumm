package lv.yumm.recipes.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.source.network.NetworkRecipe

@Entity (
    tableName = "recipe"
)
data class LocalRecipe (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val directions: List<String> = emptyList(),
    val complexity: Int = -1,
    val duration: Long = 0,
    val portions: Int = 0,
    val imageUrl: String = "",
    val type: RecipeType? = null,
    val ingredients: List<Ingredient> = emptyList<Ingredient>(),
)

fun LocalRecipe.toExternal() = Recipe(
    id = id.toString(),
    title = title,
    description = description,
    complexity = complexity,
    duration = duration,
    portions = portions,
    imageUrl = imageUrl,
    type = type,
    ingredients = ingredients,
    directions = directions,
)

fun List<LocalRecipe>.toExternal() = map { it.toExternal() }

fun Recipe.toLocal() = LocalRecipe(
    id = 0,
    title = title,
    description = description,
    complexity = complexity,
    duration = duration,
    portions = portions,
    imageUrl = imageUrl,
    type = type,
    ingredients = ingredients,
    directions = directions,
)

fun LocalRecipe.toNetwork() = NetworkRecipe(
    id = id,
    title = title,
    description = description,
    complexity = complexity,
    duration = duration,
    portions = portions,
    imageUrl = imageUrl,
    type = type,
    ingredients = ingredients,
    directions = directions,
)

fun List<LocalRecipe>.toNetwork() = map { it.toNetwork() }

fun NetworkRecipe.toLocal() = LocalRecipe(
    id = id,
    title = title,
    description = description,
    complexity = complexity,
    duration = duration,
    portions = portions,
    imageUrl = imageUrl,
    type = type,
    ingredients = ingredients,
    directions = directions,
)

fun List<NetworkRecipe>.toLocal() = map { it.toLocal() }