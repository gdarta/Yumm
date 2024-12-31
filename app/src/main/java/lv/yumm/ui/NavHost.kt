package lv.yumm.ui

import androidx.annotation.Keep
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import lv.yumm.R
import lv.yumm.lists.ListEvent
import lv.yumm.lists.ListViewModel
import lv.yumm.lists.ui.AddRecipeToListScreen
import lv.yumm.lists.ui.CreateListScreen
import lv.yumm.lists.ui.ListScreen
import lv.yumm.lists.ui.ViewListScreen
import lv.yumm.login.LoginViewModel
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID
import lv.yumm.login.ui.ActionAuthorizeScreen
import lv.yumm.login.ui.ProfileScreen
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.recipes.toRecipeCardUiState
import lv.yumm.recipes.ui.CreateRecipeScreen
import lv.yumm.recipes.ui.EditDirectionsScreen
import lv.yumm.recipes.ui.EditIngredientsScreen
import lv.yumm.recipes.ui.HomeScreen
import lv.yumm.recipes.ui.RecipesScreen
import lv.yumm.recipes.ui.ViewRecipeScreen
import lv.yumm.ui.state.FloatingActionButtonState
import lv.yumm.ui.state.RightTopBarButtonState
import lv.yumm.ui.theme.BottomNavBar
import lv.yumm.ui.theme.TopBar
import timber.log.Timber

@Keep
enum class EditProfileAction {
    DELETE,
    EMAIL,
    PASSWORD
}

@Serializable
object RecipesScreen

@Serializable
object HomeScreen

@Serializable
object CreateRecipe

@Serializable
object EditIngredients

@Serializable
object EditDirections

@Serializable
object ViewRecipe

@Serializable
object SplashScreen

@Serializable
object ProfileScreen

@Serializable
data class ActionAuthorizeScreen(
    val infoText: String,
    val actionText: String,
    val event: EditProfileAction
)

@Serializable
object CreateListScreen

@Serializable
object ListsScreen

@Serializable
object ViewListScreen

@Serializable
object AddRecipeToListScreen

fun getTitle(screen: NavDestination?): String {
    return when {
        screen?.hasRoute(route = RecipesScreen::class) == true -> "My Recipes"
        screen?.hasRoute(route = CreateRecipe::class) == true -> "Create a Recipe"
        screen?.hasRoute(route = EditIngredients::class) == true -> "Edit Ingredients"
        screen?.hasRoute(route = EditDirections::class) == true -> "Edit Directions"
        screen?.hasRoute(route = ActionAuthorizeScreen::class) == true -> "Verify Identity"
        screen?.hasRoute(route = ListsScreen::class) == true -> "My Shopping Lists"
        else -> "Yumm"
    }
}

val showBottomBarList = listOf(
    HomeScreen::class.qualifiedName,
    RecipesScreen::class.qualifiedName,
    ViewRecipe::class.qualifiedName,
    ListsScreen::class.qualifiedName,
    ProfileScreen::class.qualifiedName
)

val showBackButtonList = listOf(
    ViewRecipe::class.qualifiedName,
    CreateRecipe::class.qualifiedName,
    EditIngredients::class.qualifiedName,
    EditDirections::class.qualifiedName,
    ActionAuthorizeScreen::class.qualifiedName + "/{infoText}/{actionText}/{event}",
    CreateListScreen::class.qualifiedName,
    ViewListScreen::class.qualifiedName,
    AddRecipeToListScreen::class.qualifiedName
)

@Composable
fun YummNavHost(
    recipeViewModel: RecipeViewModel,
    loginViewModel: LoginViewModel,
    listViewModel: ListViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val showBottomBar = remember { mutableStateOf(true) }
    val showBackButton = remember { mutableStateOf(false) }

    // Observe current navigation state
    val actionButtonState = remember { mutableStateOf(FloatingActionButtonState()) }
    val rightButtonState = remember { mutableStateOf(RightTopBarButtonState()) }
    navBackStackEntry?.destination?.let { currentDestination ->
        val route = currentDestination.route
        Timber.d("Route: ${route}")

        showBottomBar.value = route in showBottomBarList
        showBackButton.value = route in showBackButtonList

        rightButtonState.value = if (route == ViewRecipe::class.qualifiedName)
            RightTopBarButtonState(
                shouldShow = true,
                onClick = { navController.navigate(AddRecipeToListScreen) }) else RightTopBarButtonState(
            shouldShow = false
        )

        actionButtonState.value =
            when (route) {
                RecipesScreen::class.qualifiedName -> {
                    FloatingActionButtonState(
                        shouldShow = recipeViewModel.currentUserId.value != EMPTY_USER_ID,
                        onClick = {
                            navController.navigate(CreateRecipe)
                            recipeViewModel.onEvent(RecipeEvent.CreateRecipe())
                        }
                    )
                }

                ListsScreen::class.qualifiedName -> {
                    FloatingActionButtonState(
                        shouldShow = recipeViewModel.currentUserId.value != EMPTY_USER_ID,
                        onClick = {
                            navController.navigate(CreateListScreen)
                            listViewModel.onEvent(ListEvent.CreateNewList())
                        }
                    )
                }

                else -> {
                    FloatingActionButtonState(
                        shouldShow = false
                    )
                }
            }
    }

    val recipeUiState by recipeViewModel.recipeUiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.loginUiState.collectAsStateWithLifecycle()
    val listUiState by listViewModel.listUiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopBar(
                title = getTitle(navBackStackEntry?.destination),
                leftButton = { modifier ->
                    if (showBackButton.value) {
                        IconButton(
                            modifier = modifier,
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                rightButton = { modifier ->
                    if (rightButtonState.value.shouldShow) {
                        IconButton(
                            modifier = modifier,
                            onClick = { rightButtonState.value.onClick() }
                        ) {
                            Icon(
                                painter = painterResource(rightButtonState.value.resId),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar.value) {
                BottomNavBar(
                    toRecipes = { navController.navigate(RecipesScreen) },
                    toHome = { navController.navigate(HomeScreen) },
                    toLists = { navController.navigate(ListsScreen) },
                    toProfile = { navController.navigate(ProfileScreen) },
                )
            }
        },
        floatingActionButton = {
            if (actionButtonState.value.shouldShow) {
                Button(
                    shape = CircleShape,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add",
                            modifier = Modifier.size(50.dp)
                        )
                    },
                    onClick = { actionButtonState.value.onClick() })
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .systemBarsPadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeScreen,
                exitTransition = {
                    scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS)
                },
                popEnterTransition = {
                    scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS)
                },
                popExitTransition = {
                    scaleOutOfContainer()
                }
            ) {
                composable<RecipesScreen> {
                    val state by recipeViewModel.userRecipeCardUiList.collectAsStateWithLifecycle()
                    val currentUser by recipeViewModel.currentUserId.collectAsStateWithLifecycle()
                    RecipesScreen(
                        currentUser,
                        state,
                        navigateToEdit = { navController.navigate(CreateRecipe) },
                        navigateToView = {
                            recipeViewModel.onEvent(RecipeEvent.SetRecipeToUi(false, it))
                            navController.navigate(ViewRecipe)
                        },
                        navigateToLogin = { navController.navigate(ProfileScreen) },
                        onEvent = { recipeViewModel.onEvent(it) })
                }
                composable<HomeScreen> {
                    val state by recipeViewModel.publicRecipeCardUiList.collectAsStateWithLifecycle()
                    val phrase by recipeViewModel.searchPhrase.collectAsStateWithLifecycle()
                    val filteredState by recipeViewModel.filteredPublicRecipes.collectAsStateWithLifecycle()
                    LaunchedEffect(phrase) {
                        if (phrase.length > 2) {
                            recipeViewModel.processSearchQueryForPublicRecipeStream().collectLatest { recipes ->
                                recipeViewModel.updateFilteredStream(recipes)
                            }
                        }
                    }
                    HomeScreen(
                        loading = recipeUiState.isLoading,
                        searchPhrase = phrase,
                        onSearch = { recipeViewModel.updateSearchPhrase(it) },
                        recipes = if (phrase.length < 3) state else filteredState,
                        navigateToView = {
                            recipeViewModel.onEvent(RecipeEvent.SetRecipeToUi(true, it))
                            navController.navigate(ViewRecipe)
                        }
                        )
                }
                composable<CreateRecipe> {
                    CreateRecipeScreen(
                        { recipeUiState },
                        onEvent = { recipeViewModel.onEvent(it) },
                        navigateToRecipesScreen = { navController.navigate(RecipesScreen) },
                        navigateToEditIngredientsScreen = { navController.navigate(EditIngredients) },
                        navigateToEditDirectionsScreen = { navController.navigate(EditDirections) })
                }
                composable<ViewRecipe> {
                    ViewRecipeScreen(
                        { recipeUiState },
                        onEvent = { recipeViewModel.onEvent(it) }
                    )
                }
                composable<EditIngredients> {
                    EditIngredientsScreen(
                        uiState = recipeUiState,
                        onEvent = { recipeViewModel.onEvent(it) }
                    ) {
                        navController.popBackStack()
                    }
                }
                composable<EditDirections> {
                    EditDirectionsScreen(
                        uiState = recipeUiState,
                        onEvent = { recipeViewModel.onEvent(it) }
                    ) {
                        navController.popBackStack()
                    }
                }
                composable<ProfileScreen> {
                    ProfileScreen(
                        uiState = { loginUiState },
                        onEvent = { loginViewModel.onEvent(it) },
                        navigateToProfileScreen = { navController.navigate(ProfileScreen) },
                        navigateToAction = { info, action, event ->
                            navController.navigate(
                                ActionAuthorizeScreen(info, action, event)
                            )
                        }
                    )
                }
                composable<ActionAuthorizeScreen> { navBackStackEntry ->
                    val args: ActionAuthorizeScreen = navBackStackEntry.toRoute()
                    ActionAuthorizeScreen(
                        infoText = args.infoText,
                        actionText = args.actionText,
                        actionType = args.event,
                        uiState = { loginUiState },
                        onEvent = { loginViewModel.onEvent(it) },
                        navigateToProfileScreen = { navController.navigate(ProfileScreen) }
                    )
                }

                // Lists
                composable<CreateListScreen> {
                    CreateListScreen(
                        navigateBack = { navController.popBackStack() },
                        uiState = { listUiState }
                    ) { listViewModel.onEvent(it) }
                }
                composable<ListsScreen> {
                    val lists by listViewModel.userListsUiState.collectAsStateWithLifecycle()
                    val currentUser by listViewModel.currentUserId.collectAsStateWithLifecycle()
                    ListScreen(
                        currentUserId = currentUser,
                        navigateToLogin = { navController.navigate(ProfileScreen) },
                        navigateToEdit = { navController.navigate(CreateListScreen) },
                        navigateToView = { navController.navigate(ViewListScreen) },
                        lists = lists,
                        onEvent = { listViewModel.onEvent(it) }
                    )
                }

                composable<ViewListScreen> {
                    ViewListScreen(
                        uiState = listUiState
                    ) { listViewModel.onEvent(it) }
                }

                composable<AddRecipeToListScreen> {
                    val lists by listViewModel.userListsUiState.collectAsStateWithLifecycle()
                    AddRecipeToListScreen(
                        recipeIngredients = recipeUiState.ingredients,
                        userLists = lists
                    ) {
                        listViewModel.onEvent(it)
                        navController.popBackStack()
                    }
                }
            }
            if (recipeUiState.isLoading || loginUiState.isLoading || listUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// https://medium.com/@nitheeshag/navigation-in-jetpack-compose-with-animations-724037d7b119, retrieved on 30/12/2024
enum class ScaleTransitionDirection {
    INWARDS,
    OUTWARDS
}

fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 0.9f else 1.1f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(220, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
}

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.9f else 1.1f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 220,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}
