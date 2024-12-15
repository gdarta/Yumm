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
import lv.yumm.recipes.data.source.toExternal
import timber.log.Timber
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

    private val _recipeCardUiList = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val recipeCardUiList = _recipeCardUiList.asStateFlow()

    private val _recipeUiState = MutableStateFlow(RecipeUiState())
    val recipeUiState = _recipeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _recipeStream.collect { recipes ->
                _recipeCardUiList.update {
                    recipes.toRecipeCardUiState()
                }
            }
        }
    }

    fun updateRecipe() {
        val recipeState = recipeUiState.value
        viewModelScope.launch {
            recipeRepository.upsert(
                id = recipeState.id,
                title = recipeState.title,
                description = recipeState.description,
                directions = recipeState.directions,
                complexity = recipeState.difficulty.toInt(),
                duration = recipeState.duration,
                imageUrl = recipeState.imageUrl,
                type = recipeState.type,
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

    fun deleteRecipe(id: Long) {
        viewModelScope.launch {
            val recipe = recipeRepository.getLocalRecipe(id)
            recipeRepository.deleteRecipe(recipe)
        }
    }

    fun setRecipeUiState(id: Long) {
        viewModelScope.launch {
            val recipe = recipeRepository.getLocalRecipe(id)
            _recipeUiState.update {
                recipe.toExternal().toRecipeUiState()
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
            is RecipeEvent.UpdateTitle -> {
                _recipeUiState.update {
                    it.copy(title = event.title)
                }
            }
            is RecipeEvent.UpdateDescription -> {
                _recipeUiState.update {
                    it.copy(description = event.description)
                }
            }
            is RecipeEvent.UpdateIngredient -> {
                val updatedIngredients = _recipeUiState.value.ingredients.toMutableList()
                updatedIngredients[event.index] = event.ingredient
                _recipeUiState.update {
                    it.copy(ingredients = updatedIngredients)
                }
            }
            is RecipeEvent.AddDirection -> {
                _recipeUiState.update {
                    it.copy(directions = it.directions + "")
                }
            }
            is RecipeEvent.UpdateDirection -> {
                val updatedDirections = _recipeUiState.value.directions.toMutableList()
                updatedDirections[event.index] = event.direction
                _recipeUiState.update {
                    it.copy(directions = updatedDirections)
                }
            }
            is RecipeEvent.SaveRecipe -> {
                updateRecipe()
            }
            is RecipeEvent.DeleteRecipe -> {
                deleteRecipe(event.id)
            }
            is RecipeEvent.SetRecipeToUi -> {
                setRecipeUiState(event.id)
            }
            is RecipeEvent.UpdateDifficulty -> {
                _recipeUiState.update {
                    it.copy(difficulty = event.difficulty)
                }
            }
            is RecipeEvent.UpdateDuration -> {
                val newDuration = (event.duration[0] * 24 * 60) + (event.duration[1] * 60) + event.duration[2]
                _recipeUiState.update {
                    it.copy(duration = newDuration.toLong())
                }
            }
            is RecipeEvent.SetDurationDialog -> {
                _recipeUiState.update {
                    it.copy(editDurationDialog = event.open)
                }
            }
            is RecipeEvent.UploadPicture -> {
                _recipeUiState.update {
                    it.copy(imageUrl = event.uri)
                }
            }
            is RecipeEvent.UpdateCategory -> {
                _recipeUiState.update {
                    it.copy(type = event.type)
                }
            }
            is RecipeEvent.OnCardClicked -> {
                _recipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isDeleteRevealed = false,
                        isEditRevealed = false
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnEditRevealed -> {
                _recipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isEditRevealed = true
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnDeleteRevealed -> {
                _recipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isDeleteRevealed = true
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnEditCollapsed -> {
                _recipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isEditRevealed = false
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnDeleteCollapsed -> {
                _recipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isDeleteRevealed = false
                    )
                    updatedList
                }
            }
        }
    }
}