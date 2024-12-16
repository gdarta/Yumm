package lv.yumm

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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
import kotlinx.serialization.InternalSerializationApi
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.data.Ingredient
import lv.yumm.recipes.toRecipeCardUiState
import lv.yumm.recipes.ui.EditDirectionsScreen
import lv.yumm.recipes.ui.EditIngredientsScreen
import timber.log.Timber

@Serializable
object RecipesScreen

@Serializable
object CreateRecipe

@Serializable
object EditIngredients

@Serializable
object EditDirections

fun getTitle(screen: NavDestination?) : String{
    return when {
        screen?.hasRoute(route = RecipesScreen::class) == true -> "My Recipes"
        screen?.hasRoute(route = CreateRecipe::class) == true -> "Create a Recipe"
        screen?.hasRoute(route = EditIngredients::class) == true -> "Edit Ingredients"
        screen?.hasRoute(route = EditDirections::class) == true -> "Edit Directions"
        else -> "Yumm"
    }
}

@Composable
fun YummNavHost(viewModel: RecipeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var shouldShowActionButton by remember { mutableStateOf(false) }

    navBackStackEntry?.destination?.let { currentDestination ->
        shouldShowActionButton = currentDestination.hasRoute(route = RecipesScreen::class)
    }

    Scaffold(
        topBar = { TopBar(title = getTitle(navBackStackEntry?.destination)) },
        bottomBar = { BottomNavBar(
            toRecipes = {
                navController.navigate(RecipesScreen)
            },
            toHome = {},
            toLists = {},
            toProfile = {},
            toCalendar = {}) },
        floatingActionButton = {
            if (shouldShowActionButton) {
                Button(
                    content = { Text("Add recipe") },
                    onClick = {
                        navController.navigate(CreateRecipe)
                        viewModel.onEvent(RecipeEvent.CreateRecipe())
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
                    val state = viewModel.recipeCardUiList.collectAsStateWithLifecycle()
                    RecipesScreen(state.value,
                        { navController.navigate(CreateRecipe) },
                        { viewModel.onEvent(it) })
                }
                composable<CreateRecipe> {
                    val recipeUiState = viewModel.recipeUiState.collectAsStateWithLifecycle()
                    CreateRecipeScreen(
                        recipeUiState.value,
                        onEvent = { viewModel.onEvent(it) },
                        navigateToRecipesScreen = { navController.navigate(RecipesScreen) },
                        navigateToEditIngredientsScreen = {navController.navigate(EditIngredients)},
                        navigateToEditDirectionsScreen = { navController.navigate(EditDirections)})
                }
                composable<EditIngredients> {
                    val recipeUiState = viewModel.recipeUiState.collectAsStateWithLifecycle()
                    EditIngredientsScreen(
                        uiState = recipeUiState.value,
                        onEvent = { viewModel.onEvent(it) }
                    ) {
                        navController.popBackStack()
                    }
                }
                composable<EditDirections> {
                    val recipeUiState = viewModel.recipeUiState.collectAsStateWithLifecycle()
                    EditDirectionsScreen(
                        uiState = recipeUiState.value,
                        onEvent = { viewModel.onEvent(it) }
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
