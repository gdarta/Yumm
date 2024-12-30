package lv.yumm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import lv.yumm.lists.ListViewModel
import lv.yumm.login.LoginViewModel
import lv.yumm.recipes.RecipeViewModel
import lv.yumm.ui.YummNavHost
import lv.yumm.ui.theme.YummTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val recipeViewModel by viewModels<RecipeViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val listViewModel by viewModels<ListViewModel>()
    private val baseViewModel by viewModels<BaseViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show toast message
        loginViewModel.message.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })

        // Show toast message
        recipeViewModel.message.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })

        // Show toast message
        listViewModel.message.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // todo reload
        }

        enableEdgeToEdge()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            YummTheme {
                YummNavHost(recipeViewModel, loginViewModel, listViewModel)
            }
        }
    }
}