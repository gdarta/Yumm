package lv.yumm.lists

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lv.yumm.BaseViewModel
import lv.yumm.lists.data.toUserList
import lv.yumm.lists.service.ListService
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.data.isEmpty
import lv.yumm.service.StorageService
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val storageService: ListService,
    private val recipeService: StorageService,
    private val accountService: AccountService,
) : BaseViewModel() {

    private val _listUiState = MutableStateFlow(ListUiState())
    val listUiState = _listUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ListUiState()
    )

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.UpdateTitle -> {
                _listUiState.update {
                    it.copy(title = event.title)
                }
            }

            is ListEvent.AddItem -> {
                _listUiState.update {
                    it.copy(
                        list = it.list + Ingredient(),
                        errorList = it.errorList + ItemError()
                    )
                }
            }

            is ListEvent.UpdateItem -> {
                val item = event.item
                val updatedItems = _listUiState.value.list.toMutableList()
                updatedItems[event.index] = item

                val updatedErrors = _listUiState.value.errorList.toMutableList()
                // validating that input is not blank and that amount is a valid number
                updatedErrors[event.index] = ItemError(
                    item.name.isBlank(), (item.amount <= 0f || item.amount.isNaN()), item.unit.isBlank()
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

            is ListEvent.ValidateAndSave -> {
                viewModelScope.launch {
                    _listUiState.update {
                        it.copy(
                            list = it.list.filterNot { it.isEmpty() },
                            errorList = it.errorList.filterIndexed { index, _ -> !it.list[index].isEmpty() })
                    }
                    storageService.updateList(_listUiState.value.toUserList()) {
                        if (it == null) postMessage("List is saved")
                        else postMessage("An error occurred")
                    }
                }
            }
        }
    }

}