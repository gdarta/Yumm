package lv.yumm.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import lv.yumm.BaseViewModel
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.hasEmpty
import lv.yumm.recipes.data.isEmpty
import lv.yumm.service.StorageService
import lv.yumm.ui.state.ConfirmationDialogUiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val storageService: StorageService,
    private val accountService: AccountService,
    //private val recipeRepository: DefaultRecipeRepository,
) : BaseViewModel() {
    private val _publicRecipeStream = storageService.publicRecipes
    private val _userRecipeStream = storageService.userRecipes

    private val _currentUserId = MutableStateFlow("")
    val currentUserId = _currentUserId.asStateFlow()

    val recipeStream: StateFlow<List<Recipe>> = _userRecipeStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    private val _recipeList = MutableStateFlow<List<Recipe>>(emptyList())

    private val _userRecipeCardUiList = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val userRecipeCardUiList = _userRecipeCardUiList.asStateFlow()

    private val _publicRecipeCardUiList = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val publicRecipeCardUiList = _publicRecipeCardUiList.asStateFlow()

    private val _recipeUiState = MutableStateFlow(RecipeUiState())
    val recipeUiState = combine(
        _recipeUiState,
        storageService.uploadingFlow
    ) { uiState, isLoading ->
        uiState.copy(isLoading = isLoading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecipeUiState()
    )

    init {
        viewModelScope.launch {
            _userRecipeStream.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList(),
            ).collectLatest { recipes ->
                _userRecipeCardUiList.update {
                    recipes.toRecipeCardUiState()
                }
                _recipeList.update {
                    recipes
                }
            }
        }
        viewModelScope.launch {
            _publicRecipeStream.collectLatest { recipes ->
                _publicRecipeCardUiList.update {
                    recipes.toRecipeCardUiState()
                }
            }
        }
        viewModelScope.launch {
            accountService.currentUser.collectLatest { user ->
                // when auth state changes, query the database again
                // a bit stinky
                _currentUserId.value = user
                if (user != "col"){
                    storageService.refreshUserRecipes(user).collectLatest { recipes ->
                        _userRecipeCardUiList.update {
                            recipes.toRecipeCardUiState()
                        }
                        _recipeList.update {
                            recipes
                        }
                    }
                } else {
                    _userRecipeCardUiList.update {
                        emptyList()
                    }
                    _recipeList.update {
                        emptyList()
                    }
                }
            }
        }
    }

    fun onError(error: Throwable?) {
        error?.let {
            Timber.e("Error happened with message: ${it.message}")
        }
    }

    fun insertNewOrUpdate(public: Boolean) {
        val recipeState = recipeUiState.value.copy(isPublic = public)
        viewModelScope.launch {
            if (recipeState.id.isBlank()) {
                storageService.insertRecipe(recipeState.toRecipe()) {
                    if (it != null) {
                        postMessage("Error adding recipe")
                    }
                }
            } else {
                storageService.updateRecipe(recipeState.toRecipe()) {
                    if (it != null) postMessage("Error updating recipe")
                }
            }
        }
    }

    fun createRecipe() {
        _recipeUiState.update {
            RecipeUiState(id = "")
        }
    }

    fun deleteRecipe(id: String) {
        viewModelScope.launch {
            val recipe = storageService.getUserRecipe(id)
            recipe?.let {
                onError(storageService.deleteRecipe(recipe)?.cause)
            }
        }
    }

    fun setRecipeUiState(public: Boolean, id: String) {
        viewModelScope.launch {
            val recipe = if (public) storageService.getPublicRecipe(id) ?: Recipe() else storageService.getUserRecipe(id) ?: Recipe()
            _recipeUiState.update {
                recipe.toRecipeUiState()
            }
        }
    }

    fun saveRecipe(public: Boolean, navigateBack: () -> Unit) {
        viewModelScope.launch {
            _recipeUiState.update {
                it.copy(triedToSave = true)
            }
            if (!_recipeUiState.value.editScreenHasError) {
                insertNewOrUpdate(public)
                navigateBack()
            } else {
                _recipeUiState.update {
                    if (!it.hasOpenDialogs) {
                        it.copy(showErrorDialog = true)
                    } else it
                }
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
            is RecipeEvent.DeleteIngredient -> {
                val updatedIngredients = _recipeUiState.value.ingredients.toMutableList()
                updatedIngredients.removeAt(event.index)
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
            is RecipeEvent.DeleteDirection -> {
                val updateDirections = _recipeUiState.value.directions.toMutableList()
                updateDirections.removeAt(event.index)
                _recipeUiState.update {
                    it.copy(directions = updateDirections)
                }
            }
            is RecipeEvent.SaveRecipe -> {
                saveRecipe(event.public) { event.navigateBack() }
            }
            is RecipeEvent.DeleteRecipe -> {
                deleteRecipe(event.id)
            }
            is RecipeEvent.SetRecipeToUi -> {
                setRecipeUiState(event.public, event.id)
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
            is RecipeEvent.SetErrorDialog -> {
                _recipeUiState.update {
                    it.copy(showErrorDialog = event.open)
                }
            }
            is RecipeEvent.UploadPicture -> {
                event.uri?.let {
                    viewModelScope.launch {
                        _recipeUiState.update {
                            it.copy(imageUrl = event.uri.toString())
                        }
                        val uploadResult = storageService.uploadPhoto(event.uri)
                        if (uploadResult == null){
                            //todo
                        } else {
                            onError(uploadResult)
                        }
                    }
                }
            }
            is RecipeEvent.UpdateCategory -> {
                _recipeUiState.update {
                    it.copy(category = event.type)
                }
            }
            is RecipeEvent.OnCardClicked -> {
                _userRecipeCardUiList.update {
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
                _userRecipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isEditRevealed = true
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnDeleteRevealed -> {
                _userRecipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isDeleteRevealed = true
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnEditCollapsed -> {
                _userRecipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isEditRevealed = false
                    )
                    updatedList
                }
            }
            is RecipeEvent.OnDeleteCollapsed -> {
                _userRecipeCardUiList.update {
                    val updatedList = it.toMutableList()
                    val index = updatedList.indexOf(updatedList.find {event.id == it.id})
                    updatedList[index] = updatedList[index].copy(
                        isDeleteRevealed = false
                    )
                    updatedList
                }
            }
            is RecipeEvent.HandleBackPressed -> {
                val currentState = _recipeUiState.value
                if (!currentState.hasOpenDialogs && currentState != _recipeList.replayCache[0].find { it.id == currentState.id}?.toRecipeUiState()) {
                    _recipeUiState.update {
                        it.copy(
                            confirmationDialog = ConfirmationDialogUiState(
                                title = "Save recipe?",
                                description = "Do you want to save your recent changes?",
                                cancelButtonText = "Don't save",
                                confirmButtonText = "Save ",
                                onCancelButtonClick = {
                                    _recipeUiState.update {
                                        it.copy(confirmationDialog = null)
                                    }
                                    event.navigateBack()
                                },
                                onConfirmButtonClick = {
                                    _recipeUiState.update {
                                        it.copy(confirmationDialog = null)
                                    }
                                    saveRecipe(false) { event.navigateBack() }
                                },
                                onDismissDialog = {
                                    _recipeUiState.update {
                                        it.copy(confirmationDialog = null)
                                    }
                                }
                            )
                        )
                    }
                } else {
                    event.navigateBack()
                }
            }

            is RecipeEvent.UpdatePortions -> {
                event.portions?.let {
                    _recipeUiState.update {
                        it.copy(portions = event.portions)
                    }
                }
            }

            is RecipeEvent.ValidateIngredients -> {
                val ingredients = _recipeUiState.value.ingredients.filterNot { it.isEmpty() || it.hasEmpty() }
                _recipeUiState.update {
                    it.copy(ingredients = ingredients)
                }
            }

            is RecipeEvent.ValidateDirections -> {
                val directions = _recipeUiState.value.directions.filterNot { it.isEmpty() }
                _recipeUiState.update {
                    it.copy(directions = directions)
                }
            }
        }
    }
}