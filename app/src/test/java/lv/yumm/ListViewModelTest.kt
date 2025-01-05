package lv.yumm

import app.cash.turbine.test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lv.yumm.data.FakeAccountService
import lv.yumm.data.FakeListService
import lv.yumm.data.FakeListService.Companion.LIST_ID_1
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListViewModel
import lv.yumm.lists.service.ListService
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.service.StorageService
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class ListViewModelTest {
    private val auth: FirebaseAuth = mock(FirebaseAuth::class.java)
    private val firestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val recipes: StorageService = mock(StorageService::class.java)

    private lateinit var accountService: AccountService
    private lateinit var storageService: ListService
    private lateinit var viewModel: ListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storageService = FakeListService(firestore)
        accountService = FakeAccountService(auth, recipes, storageService)
        viewModel = ListViewModel(storageService, accountService)
    }

    val ingredients = listOf(
        Ingredient(
            "apelsīnu sula",
            1f,
            "L"
        ),
        Ingredient(
            "ābolu sula",
            1f,
            "L"
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun listViewModel_createNewListFromRecipe_userListAdded() = runTest(testDispatcher) {
        viewModel.onEvent(ListEvent.CreateListFromIngredients(title = "new", ingredients = ingredients))
        advanceUntilIdle()

        storageService.userLists.test {
            assertEquals(4, awaitItem().size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun listViewModel_addIngredientsToListSameIngredients_listSizeSame() = runTest(testDispatcher) {
        val list = LIST_ID_1

        val sizeBefore = list.list.size

        viewModel.onEvent(ListEvent.AddIngredientsToUserList(ingredients = list.list.map {it.ingredient}, "1"))
        advanceUntilIdle()

        storageService.userLists.test {
            assertEquals(sizeBefore, awaitItem().find { it.id == "1" }?.list?.size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun listViewModel_addIngredientsToListDifferentIngredients_listChanged() = runTest(testDispatcher) {
        val list = LIST_ID_1

        val sizeBefore = list.list.size

        viewModel.onEvent(ListEvent.AddIngredientsToUserList(ingredients = ingredients, "1"))
        advanceUntilIdle()

        storageService.userLists.test {
            assertNotEquals(sizeBefore, awaitItem().find { it.id == "1" }?.list?.size)
        }
    }

}