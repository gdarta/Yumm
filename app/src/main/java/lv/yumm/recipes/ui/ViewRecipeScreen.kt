package lv.yumm.recipes.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import lv.yumm.R
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.RecipeUiState
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.RatingBar
import lv.yumm.ui.theme.Typography

@Composable
fun ViewRecipeScreen(
    uiState: RecipeUiState,
    onEvent: (RecipeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(all = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            model = uiState.imageUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_pasta_filled),
            fallback = painterResource(R.drawable.ic_pasta_filled),
            error = painterResource(R.drawable.ic_pasta_filled), // todo icon for placeholder brr
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
        )
        Text(
            text = uiState.title,
            style = Typography.titleLarge,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(iterations = Int.MAX_VALUE)
        )
        Text(
            text = uiState.description,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Category: ${uiState.category}",
            style = Typography.titleMedium
        )
        EditRow {
            Text(
                text = "Difficulty:  ${if (uiState.difficulty > 0) uiState.difficulty.toInt() else ""}",
                style = Typography.titleMedium,
                color = if (!uiState.difficultyError) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
            )
            RatingBar(
                modifier = Modifier,
                rating = uiState.difficulty,
                ratingStep = 1f,
                starSize = 32.dp,
                unratedContent = {
                    Icon(
                        modifier = Modifier.alpha(.1f),
                        painter = painterResource(R.drawable.ic_cookie_outlined),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = null
                    )
                },
                ratedContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_cookie_outlined),
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = null
                    )
                },
                onRatingChanged = {}
            )
        }
        Text(
            text = "Duration: ${uiState.duration.toTimestamp()}",
            style = Typography.titleMedium,
            color = if (!uiState.durationError) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
        )
        Text(
            text = "Ingredients:",
            style = Typography.titleMedium,
            color = if (!uiState.ingredientsEmptyError) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
        )
        repeat(uiState.ingredients.size) { index ->
            IngredientText(uiState.ingredients[index])
        }
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