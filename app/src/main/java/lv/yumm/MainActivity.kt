package lv.yumm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.recipes.ui.CreateRecipeScreen
import lv.yumm.ui.theme.YummTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val recipeViewModel by viewModels<RecipeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YummTheme {
                val recipeState = recipeViewModel.recipeUiState.collectAsState()
                CreateRecipeScreen(recipeState.value)
            }
        }
    }
}