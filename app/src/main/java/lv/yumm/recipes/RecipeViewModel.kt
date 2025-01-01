package lv.yumm.recipes

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.BaseViewModel
import lv.yumm.lists.IngredientError
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.isEmpty
import lv.yumm.service.StorageService
import lv.yumm.ui.state.ConfirmationDialogUiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService,
) : BaseViewModel() {
    // for querying all public recipes
    private val _searchPhrasePublic = MutableStateFlow("")
    val searchPhrasePublic = _searchPhrasePublic.asStateFlow()

    private val _filteredPublicRecipes = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val filteredPublicRecipes = _filteredPublicRecipes.asStateFlow()

    // for querying all user recipes
    private val _searchPhraseUser = MutableStateFlow("")
    val searchPhraseUser = _searchPhraseUser.asStateFlow()

    private val _filteredUserRecipes = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val filteredUserRecipes = _filteredUserRecipes.asStateFlow()

    // recipe streams from database
    private val _publicRecipeStream = storageService.publicRecipes
    private val _userRecipeStream = storageService.userRecipes

    private val _recipeList = MutableStateFlow<List<Recipe>>(emptyList())

    private val _userRecipeCardUiList = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val userRecipeCardUiList = _userRecipeCardUiList.asStateFlow()

    private val _publicRecipeCardUiList = MutableStateFlow<List<RecipeCardUiState>>(emptyList())
    val publicRecipeCardUiList = _publicRecipeCardUiList.asStateFlow()

    // UI state for single recipe
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
                Timber.d("Collected recipes $recipes")
                updateUiRecipeLists(recipes)
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
                postUserId(user)
                if (user != EMPTY_USER_ID){
                    storageService.refreshUserRecipes(user).collectLatest { recipes ->
                        updateUiRecipeLists(recipes)
                    }
                } else {
                    updateUiRecipeLists(emptyList())
                }
            }
        }
    }

    // update list of recipes for user
    private fun updateUiRecipeLists(recipes: List<Recipe>) {
        _userRecipeCardUiList.update {
            recipes.toRecipeCardUiState()
        }
        _recipeList.update {
            recipes
        }
    }

    // searching user recipes
    fun updateUserFilteredStream(recipes: List<Recipe>) {
        _filteredUserRecipes.update {
            recipes.toRecipeCardUiState()
        }
    }

    fun updateUserSearchPhrase(phrase: String) {
        _searchPhraseUser.value = phrase
    }

    suspend fun processSearchQueryForUserRecipeStream() : Flow<List<Recipe>> {
        return storageService.searchUserRecipes(_searchPhraseUser.value)
    }

    // searching all recipes
    fun updateFilteredStream(recipes: List<Recipe>) {
        _filteredPublicRecipes.update {
            recipes.toRecipeCardUiState()
        }
    }

    fun updateSearchPhrase(phrase: String) {
        _searchPhrasePublic.value = phrase
    }

    suspend fun processSearchQueryForPublicRecipeStream() : Flow<List<Recipe>> {
        return storageService.searchPublicRecipes(_searchPhrasePublic.value)
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
            storageService.refreshUserRecipes(currentUserId.value).collectLatest { recipes ->
                updateUiRecipeLists(recipes)
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
                if (storageService.deleteRecipe(recipe) != null) postMessage("Error deleting recipe")
            }
        }
    }

    fun setRecipeUiState(public: Boolean, id: String) {
        viewModelScope.launch {
            val recipe = if (public) storageService.getPublicRecipe(id) ?: Recipe() else storageService.getUserRecipe(id) ?: Recipe()
            _recipeUiState.update {
                recipe.toRecipeUiState().copy(ingredientErrorList = recipe.ingredients.map { IngredientError() })
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
                    uiState.copy(ingredients = uiState.ingredients + Ingredient(), ingredientErrorList = uiState.ingredientErrorList + IngredientError())
                }
            }
            is RecipeEvent.UpdateTitle -> {
                if (event.title.length <= 60){
                    _recipeUiState.update {
                        it.copy(title = event.title)
                    }
                }
            }
            is RecipeEvent.UpdateDescription -> {
                if (event.description.length <= 440){
                    _recipeUiState.update {
                        it.copy(description = event.description)
                    }
                }
            }
            is RecipeEvent.UpdateIngredient -> {
                val ingredient = event.ingredient
                val updatedIngredients = _recipeUiState.value.ingredients.toMutableList()
                updatedIngredients[event.index] = event.ingredient

                val updatedErrors = _recipeUiState.value.ingredientErrorList.toMutableList()
                // validating that input is not blank and that amount is a valid number
                updatedErrors[event.index] = IngredientError(
                    ingredient.name.isBlank(), (ingredient.amount <= 0f || ingredient.amount.isNaN()), ingredient.unit.isBlank()
                )
                _recipeUiState.update {
                    it.copy(ingredients = updatedIngredients, ingredientErrorList = updatedErrors)
                }
            }
            is RecipeEvent.DeleteIngredient -> {
                val updatedIngredients = _recipeUiState.value.ingredients.toMutableList()
                updatedIngredients.removeAt(event.index)
                val updatedErrors = _recipeUiState.value.ingredientErrorList.toMutableList()
                updatedErrors.removeAt(event.index)
                _recipeUiState.update {
                    it.copy(ingredients = updatedIngredients, ingredientErrorList = updatedErrors)
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
                            postMessage("Photo uploaded to server")
                        } else {
                            postMessage("Error uploading photo")
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
                        it.copy(portions = event.portions.coerceAtLeast(1))
                    }
                }
            }

            is RecipeEvent.ValidateIngredients -> {
                val ingredients = _recipeUiState.value.ingredients.filterNot { it.isEmpty() }
                val errorList =
                    _recipeUiState.value.ingredientErrorList.filterIndexed { index, item -> !_recipeUiState.value.ingredients[index].isEmpty() }
                _recipeUiState.update {
                    it.copy(ingredients = ingredients, ingredientErrorList = errorList)
                }
            }

            is RecipeEvent.ValidateDirections -> {
                val directions = _recipeUiState.value.directions.filterNot { it.isEmpty() }
                _recipeUiState.update {
                    it.copy(directions = directions)
                }
            }

            is RecipeEvent.UpdatePortionView -> {
                val recipe = _recipeUiState.value
                val multiplier = event.portions.coerceAtLeast(1) / recipe.portions.toFloat()
                val updatedIngredients = recipe.ingredients.map { ingredient ->
                    ingredient.copy(amount = ingredient.amount * multiplier)
                }
                _recipeUiState.update {
                    it.copy(portions = event.portions.coerceAtLeast(1), ingredients = updatedIngredients)
                }
            }
        }
    }
}