package lv.yumm.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.StateFlow
import lv.yumm.recipes.data.Recipe
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.LoadImageWithStates
import lv.yumm.ui.theme.TopBar
import lv.yumm.ui.theme.YummTheme

@Composable
fun RecipesScreen(recipes: StateFlow<List<Recipe>>, navigateToCreateScreen: () -> Unit) {
    val state = recipes.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopBar(title = "Recipes") },
        floatingActionButton = { Button(content = { Text("Add recipe") }, onClick = { navigateToCreateScreen() }) }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(innerPadding)
        ) {
            itemsIndexed(state.value) { index, recipe ->
                RecipeCard(recipe, Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(3.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {}
    ) {
        Row(){
            LoadImageWithStates(
                url = recipe.imageUrl,
                modifier = Modifier.weight(1f).height(100.dp),
            )
            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = recipe.title
                )
                Text(
                    text = recipe.description
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = recipe.complexity.toString() + "/5"
            )
            Text(
                text = recipe.type.toString()
            )
            Text(
                text = recipe.duration.toTimestamp()
            )
        }
    }
}

@Preview
@Composable
fun RecipeCardPreview() {
    YummTheme {
        RecipeCard(
            modifier = Modifier,
            recipe = Recipe(
                id = "id",
                title = "Recipe title",
                description = "Some very very very very very very long description",
                complexity = 2,
                duration = 30000L,
                type = RecipeType.LUNCH,
                imageUrl = "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630"
            )
        )
    }
}