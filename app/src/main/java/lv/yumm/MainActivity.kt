package lv.yumm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import lv.yumm.login.LoginViewModel
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.ui.YummNavHost
import lv.yumm.ui.theme.YummTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val recipeViewModel by viewModels<RecipeViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // todo reload
        }

        enableEdgeToEdge()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            YummTheme {
                YummNavHost(recipeViewModel, loginViewModel)
            }
        }
    }
}