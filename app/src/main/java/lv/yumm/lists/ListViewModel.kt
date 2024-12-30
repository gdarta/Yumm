package lv.yumm.lists

import dagger.hilt.android.lifecycle.HiltViewModel
import lv.yumm.BaseViewModel
import lv.yumm.lists.service.ListService
import lv.yumm.login.service.AccountService
import lv.yumm.service.StorageService
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val storageService: ListService,
    private val recipeService: StorageService,
    private val accountService: AccountService,
) : BaseViewModel() {

}