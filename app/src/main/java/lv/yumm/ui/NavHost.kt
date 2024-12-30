package lv.yumm.ui

import androidx.annotation.Keep
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.recipes.ui.CreateRecipeScreen
import lv.yumm.recipes.ui.RecipesScreen
import lv.yumm.ui.theme.BottomNavBar
import lv.yumm.ui.theme.TopBar
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import lv.yumm.login.LoginViewModel
import lv.yumm.login.ui.ActionAuthorizeScreen
import lv.yumm.login.ui.ProfileScreen
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.ui.EditDirectionsScreen
import lv.yumm.recipes.ui.EditIngredientsScreen
import lv.yumm.recipes.ui.HomeScreen
import lv.yumm.recipes.ui.ViewRecipeScreen
import lv.yumm.R
import lv.yumm.login.service.AccountServiceImpl.Companion.EMPTY_USER_ID

@Keep
enum class EditProfileAction{
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


fun getTitle(screen: NavDestination?) : String{
    return when {
        screen?.hasRoute(route = RecipesScreen::class) == true -> "My Recipes"
        screen?.hasRoute(route = CreateRecipe::class) == true -> "Create a Recipe"
        screen?.hasRoute(route = EditIngredients::class) == true -> "Edit Ingredients"
        screen?.hasRoute(route = EditDirections::class) == true -> "Edit Directions"
        screen?.hasRoute(route = ActionAuthorizeScreen::class) == true -> "Verify Identity"
        else -> "Yumm"
    }
}

@Composable
fun YummNavHost(recipeViewModel: RecipeViewModel, loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var shouldShowActionButton by remember { mutableStateOf(false) }

    navBackStackEntry?.destination?.let { currentDestination ->
        shouldShowActionButton = currentDestination.hasRoute(route = RecipesScreen::class) && recipeViewModel.currentUserId.value != EMPTY_USER_ID
    }

    val recipeUiState by recipeViewModel.recipeUiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.loginUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = { TopBar(title = getTitle(navBackStackEntry?.destination)) },
        bottomBar = { BottomNavBar(
            toRecipes = {
                navController.navigate(RecipesScreen)
            },
            toHome = {
                navController.navigate(HomeScreen)
            },
            toLists = {},
            toProfile = {
                navController.navigate(ProfileScreen)
            },
            toCalendar = {}) },
        floatingActionButton = {
            if (shouldShowActionButton) {
                Button(
                    shape = CircleShape,
                    content = { Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Add recipe",
                        modifier = Modifier.size(50.dp)
                    ) },
                    onClick = {
                        navController.navigate(CreateRecipe)
                        recipeViewModel.onEvent(RecipeEvent.CreateRecipe())
                    })
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
                startDestination = RecipesScreen,
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
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
                    HomeScreen(state,
                        navigateToView = {
                            recipeViewModel.onEvent(RecipeEvent.SetRecipeToUi(true, it))
                            navController.navigate(ViewRecipe)
                        },
                        { recipeViewModel.onEvent(it) })
                }
                composable<CreateRecipe> {
                    CreateRecipeScreen(
                        { recipeUiState },
                        onEvent = { recipeViewModel.onEvent(it) },
                        navigateToRecipesScreen = { navController.navigate(RecipesScreen) },
                        navigateToEditIngredientsScreen = {navController.navigate(EditIngredients)},
                        navigateToEditDirectionsScreen = { navController.navigate(EditDirections)})
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
                composable<SplashScreen> {
                    SplashScreen()
                }
                composable<ProfileScreen> {
                    ProfileScreen(
                        uiState = { loginUiState },
                        onEvent = { loginViewModel.onEvent(it) },
                        navigateToProfileScreen = { navController.navigate(ProfileScreen) },
                        navigateToAction = { info, action, event -> navController.navigate(ActionAuthorizeScreen(info, action, event)) }
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
            }
            if (recipeUiState.isLoading || loginUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
