package lv.yumm

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.recipes.ui.CreateRecipeScreen
import lv.yumm.recipes.ui.RecipesScreen

@Serializable
object RecipesScreen

@Serializable
object CreateRecipe

@Composable
fun YummNavHost(viewModel: RecipeViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RecipesScreen
    ) {
        composable<RecipesScreen> {
            RecipesScreen(viewModel.recipeStream, {navController.navigate(CreateRecipe)})
        }
        composable<CreateRecipe> {
            val recipeUiState = viewModel.recipeUiState.collectAsStateWithLifecycle()
            CreateRecipeScreen(
                recipeUiState.value,
                onEvent = { viewModel.onEvent(it) },
                navigateToRecipesScreen = { navController.navigate(RecipesScreen) })
        }
    }
}