package lv.yumm.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.recipes.data.DefaultRecipeRepository
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val recipeRepository: DefaultRecipeRepository,
) : ViewModel () {
    private val _recipeStream = recipeRepository.observeAll()
    val recipeStream: StateFlow<List<Recipe>> = _recipeStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    private val _recipeUiState = MutableStateFlow(RecipeUiState())
    val recipeUiState = _recipeUiState.asStateFlow()

    fun updateRecipe() {
        val recipeState = recipeUiState.value
        viewModelScope.launch {
            recipeRepository.upsert(
                id = recipeState.id,
                title = recipeState.title,
                description = recipeState.description,
                directions = emptyList(), //todo
                complexity = 1, //todo
                duration = 3600000L, //todo
                imageUrl = "https://images.squarespace-cdn.com/content/v1/57879a6cbebafb879f256735/1712832754805-I7IJ7FRXF629FN3PIS3O/KC310124-27.jpg", //todo
                type = RecipeType.LUNCH, //todo
                ingredients = recipeState.ingredients
                )
        }
    }

    fun createRecipe() {
        viewModelScope.launch {
            val newId = recipeRepository.createNew()
            _recipeUiState.update {
                RecipeUiState(id = newId)
            }
        }
    }

    fun onEvent(event: RecipeEvent) {
        when (event) {
            is RecipeEvent.CreateRecipe -> {
                createRecipe()
            }
            is RecipeEvent.AddIngredient -> {
                _recipeUiState.update { uiState ->
                    uiState.copy(ingredients = uiState.ingredients + Ingredient())
                }
            }
            is RecipeEvent.UpdateIngredient -> {
                val updatedIngredients = _recipeUiState.value.ingredients.toMutableList()
                updatedIngredients[event.index] = event.ingredient
                _recipeUiState.update {
                    it.copy(ingredients = updatedIngredients)
                }
            }
            is RecipeEvent.SaveRecipe -> {
                _recipeUiState.update {
                    it.copy(
                        title = event.title,
                        description = event.description
                    )
                }
                updateRecipe()
            }
        }
    }
}