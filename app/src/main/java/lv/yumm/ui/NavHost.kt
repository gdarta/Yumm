package lv.yumm.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import lv.yumm.login.LoginViewModel
import lv.yumm.login.ui.LoginScreen
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.ui.EditDirectionsScreen
import lv.yumm.recipes.ui.EditIngredientsScreen
import lv.yumm.recipes.ui.ViewRecipeScreen
import timber.log.Timber

@Serializable
object RecipesScreen

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
object LoginScreen


fun getTitle(screen: NavDestination?) : String{
    return when {
        screen?.hasRoute(route = RecipesScreen::class) == true -> "My Recipes"
        screen?.hasRoute(route = CreateRecipe::class) == true -> "Create a Recipe"
        screen?.hasRoute(route = EditIngredients::class) == true -> "Edit Ingredients"
        screen?.hasRoute(route = EditDirections::class) == true -> "Edit Directions"
        screen?.hasRoute(route = LoginScreen::class) == true -> "Log in"
        else -> "Yumm"
    }
}

@Composable
fun YummNavHost(recipeViewModel: RecipeViewModel, loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var shouldShowActionButton by remember { mutableStateOf(false) }

    navBackStackEntry?.destination?.let { currentDestination ->
        shouldShowActionButton = currentDestination.hasRoute(route = RecipesScreen::class)
    }

    val recipeUiState by recipeViewModel.recipeUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = { TopBar(title = getTitle(navBackStackEntry?.destination)) },
        bottomBar = { BottomNavBar(
            toRecipes = {
                navController.navigate(RecipesScreen)
            },
            toHome = {},
            toLists = {},
            toProfile = {
                navController.navigate(LoginScreen)
            },
            toCalendar = {}) },
        floatingActionButton = {
            if (shouldShowActionButton) {
                Button(
                    content = { Text("Add recipe") },
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
                startDestination = RecipesScreen
            ) {
                composable<RecipesScreen> {
                    val state by recipeViewModel.recipeCardUiList.collectAsStateWithLifecycle()
                    RecipesScreen(state,
                        { navController.navigate(CreateRecipe) },
                        navigateToView = {
                            recipeViewModel.onEvent(RecipeEvent.SetRecipeToUi(it))
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
                composable<LoginScreen> {
                    val loginUiState by loginViewModel.loginUiState.collectAsStateWithLifecycle()
                    LoginScreen(
                        uiState = { loginUiState },
                        onEvent = { loginViewModel.onEvent(it) },
                        navigateBack = { navController.popBackStack() }
                    )
                }
            }
            if (recipeUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
