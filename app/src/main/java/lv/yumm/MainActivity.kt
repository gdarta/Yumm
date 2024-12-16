package lv.yumm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.recipes.ui.RecipesScreen
import lv.yumm.ui.theme.YummTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val recipeViewModel by viewModels<RecipeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            YummTheme {
                YummNavHost(recipeViewModel)
            }
        }
    }
}