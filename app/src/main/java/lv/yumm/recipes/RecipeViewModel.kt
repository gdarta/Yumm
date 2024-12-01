package lv.yumm.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.recipes.data.DefaultRecipeRepository
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType

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

    fun createRecipe() {
        val recipeState = recipeUiState.value
        viewModelScope.launch {
            recipeRepository.create(
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

    fun onEvent(event: RecipeEvent) {
        when (event) {
            is RecipeEvent.CreateRecipe -> {
                _recipeUiState.update {
                    it.copy(title = event.title, description = event.description)
                }
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
        }
    }
}