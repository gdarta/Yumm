package lv.yumm.lists

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.BaseViewModel
import lv.yumm.lists.data.ListItem
import lv.yumm.lists.data.UserList
import lv.yumm.lists.data.toUiState
import lv.yumm.lists.data.toUserList
import lv.yumm.lists.service.ListService
import lv.yumm.login.service.AccountService
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.isEmpty
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val storageService: ListService,
    private val accountService: AccountService,
) : BaseViewModel() {

    private val _listUiState = MutableStateFlow(ListUiState())
    val listUiState = combine(
        _listUiState,
        storageService.uploadingFlow
    ) { uiState, isLoading ->
        uiState.copy(isLoading = isLoading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ListUiState()
    )

    private val _userListsUiState = MutableStateFlow<List<ListUiState>>(emptyList())
    val userListsUiState = _userListsUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            storageService.userLists.collectLatest {
                _userListsUiState.value = it.toUiState()
            }
        }
        // listen to auth state changes
        viewModelScope.launch {
            accountService.currentUser.collectLatest { uid ->
                postUserId(uid)
                if (uid != EMPTY_USER_ID) {
                    storageService.refreshUserLists(uid).collectLatest {
                        _userListsUiState.value = it.toUiState()
                    }
                } else {
                    _userListsUiState.value = emptyList<ListUiState>()
                }
            }
        }
    }

    private fun combineIngredientLists(list1: List<Ingredient>, list2: List<Ingredient>): List<Ingredient> {
        val combinedMap = mutableMapOf<Pair<String, String>, Ingredient>()

        (list1 + list2).forEach { ingredient ->
            val key = Pair(ingredient.name.lowercase(), ingredient.unit.lowercase())
            val existing = combinedMap[key]
            if (existing != null) {
                combinedMap[key] = existing.copy(amount = existing.amount + ingredient.amount)
            } else {
                combinedMap[key] = ingredient
            }
        }

        return combinedMap.values.toList()
    }

    private fun setListToUi(list: UserList) {
        _listUiState.value = list.toUiState().copy(errorList = list.list.map { IngredientError() } )
    }

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.CreateNewList -> {
                setListToUi(UserList())
            }

            is ListEvent.OpenList -> {
                viewModelScope.launch {
                    val list = storageService.getList(event.id) ?: UserList()
                    setListToUi(list)
                }
            }

            is ListEvent.DeleteList -> {
                viewModelScope.launch {
                    storageService.deleteList(event.id) {
                        if (it == null) postMessage("List was deleted")
                        else postMessage("Operation failed")
                    }
                }
            }

            is ListEvent.UpdateTitle -> {
                _listUiState.update {
                    it.copy(titleError = null)
                }
                if (event.title.length < 50) {
                    _listUiState.update {
                        it.copy(title = event.title)
                    }
                } else {
                    _listUiState.update {
                        it.copy(titleError = "Title must be under 50 characters.")
                    }
                }
            }

            is ListEvent.AddItem -> {
                _listUiState.update {
                    it.copy(
                        list = it.list + ListItem(),
                        errorList = it.errorList + IngredientError()
                    )
                }
            }

            is ListEvent.UpdateItem -> {
                val item = event.item
                val updatedItems = _listUiState.value.list.toMutableList()
                updatedItems[event.index] = ListItem(ingredient = item)

                val updatedErrors = _listUiState.value.errorList.toMutableList()
                // validating that input is not blank and that amount is a valid number
                updatedErrors[event.index] = IngredientError(
                    item.name.isBlank() || item.name.length > 50,
                    (item.amount <= 0f || item.amount.isNaN()),
                    item.unit.isBlank() || item.unit.length > 20
                )

                _listUiState.update {
                    it.copy(list = updatedItems, errorList = updatedErrors)
                }
            }

            is ListEvent.DeleteItem -> {
                val updatedItems = _listUiState.value.list.toMutableList()
                updatedItems.removeAt(event.index)

                val updatedErrors = _listUiState.value.errorList.toMutableList()
                updatedErrors.removeAt(event.index)
                _listUiState.update {
                    it.copy(list = updatedItems, errorList = updatedErrors)
                }
            }

            is ListEvent.CheckItem -> {
                viewModelScope.launch {
                    val updatedItems = _listUiState.value.list.toMutableList()
                    updatedItems[event.index] =
                        updatedItems[event.index].copy(checked = event.checked)

                    _listUiState.update {
                        it.copy(list = updatedItems)
                    }

                    storageService.updateList(_listUiState.value.toUserList()) {}
                }
            }

            is ListEvent.ValidateAndSave -> {
                viewModelScope.launch {
                    // when updating or inserting a list, do not save empty items
                    _listUiState.update {
                        it.copy(
                            list = it.list.filterNot { it.ingredient.isEmpty() },
                            errorList = it.errorList.filterIndexed { index, _ -> !it.list[index].ingredient.isEmpty() })
                    }
                    storageService.updateList(_listUiState.value.toUserList()) {
                        if (it == null) postMessage("List is saved")
                        else postMessage("List was dismissed")
                    }
                }
            }

            is ListEvent.AddIngredientsToUserList -> {
                viewModelScope.launch {
                    storageService.getList(event.listId)?.let { userList ->
                        val newList = combineIngredientLists(userList.list.map { it.ingredient }, event.ingredients)
                        storageService.updateList(userList.copy(list = newList.map { ListItem(false, it) } )) {
                            if (it == null) postMessage("List was updated")
                            else postMessage("List was dismissed")
                        }
                    } ?: postMessage("Could not retrieve list")
                }
            }

            is ListEvent.CreateListFromIngredients -> {
                viewModelScope.launch {
                    val newUserList = UserList(
                        title = event.title,
                        list = event.ingredients.map { ListItem(false, it) }
                    )
                    storageService.updateList(newUserList) {
                        if (it == null) postMessage("New list created")
                        else postMessage("List was dismissed")
                    }
                }
            }
        }
    }
}