package lv.yumm

import app.cash.turbine.test
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lv.yumm.data.FakeListService
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListViewModel
import lv.yumm.lists.data.UserList
import lv.yumm.lists.service.ListService
import lv.yumm.lists.service.ListServiceImpl
import lv.yumm.login.service.AccountService
import lv.yumm.recipes.data.Ingredient
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.time.Duration

class ListViewModelTest {
    private val accountService: AccountService = mock(AccountService::class.java)
    private val firestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)

    private lateinit var storageService: ListService
    private lateinit var viewModel: ListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storageService = FakeListService(firestore)
        viewModel = ListViewModel(storageService, accountService)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun listViewModel_createNewListFromRecipe_userListAdded() = runTest(testDispatcher) {
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

        viewModel.onEvent(ListEvent.CreateListFromIngredients(title = "new", ingredients = ingredients))
        advanceUntilIdle()

        storageService.userLists.test {
            println(awaitItem())
            //assertEquals(4, awaitItem().size)
        }
    }

}