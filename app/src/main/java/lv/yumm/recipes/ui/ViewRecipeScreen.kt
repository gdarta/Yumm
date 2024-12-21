package lv.yumm.recipes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import lv.yumm.R
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.CategoryBadge
import lv.yumm.ui.theme.DifficultyBadge
import lv.yumm.ui.theme.RatingBar
import lv.yumm.ui.theme.TextBadge
import lv.yumm.ui.theme.Typography

@Composable
fun ViewRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box() {
            AsyncImage(
                model = uiState.imageUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_pasta_filled),
                fallback = painterResource(R.drawable.ic_pasta_filled),
                error = painterResource(R.drawable.ic_pasta_filled), // todo icon for placeholder brr
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = 150.dp)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.1f to MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                                0.4f to MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                0.5f to MaterialTheme.colorScheme.background.copy(alpha = 1f),
                            )
                        )
                    )
                    .padding(top = 40.dp)
            ) {
                uiState.category?.let {
                    CategoryBadge(
                        text = it.name,
                    )
                }
                DifficultyBadge(
                    modifier = Modifier,
                    difficulty = uiState.difficulty.toInt(),
                )
                TextBadge(
                    modifier = Modifier,
                    text = uiState.duration.toTimestamp(),
                )
            }
        }
        Text(
            text = uiState.title,
            style = Typography.headlineLarge,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(iterations = Int.MAX_VALUE)
                .padding(top = 30.dp)
        )
        Text(
            text = uiState.description,
            modifier = Modifier.fillMaxWidth()
        )
        Surface(
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 5.dp,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(all = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ingredients:",
                    style = Typography.titleMedium,
                    color = if (!uiState.ingredientsEmptyError) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
                )
                repeat(uiState.ingredients.size) { index ->
                    IngredientText(uiState.ingredients[index])
                }
            }
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 5.dp,
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(all = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Directions:",
                    style = Typography.titleMedium,
                    color = if (!uiState.directionsEmptyError) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
                )
                repeat(uiState.directions.size) { index ->
                    Text(
                        text = "${index + 1}. ${uiState.directions[index]}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}