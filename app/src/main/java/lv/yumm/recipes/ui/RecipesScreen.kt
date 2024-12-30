package lv.yumm.recipes.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.login.ui.LoginButton
import lv.yumm.recipes.RecipeCardUiState
import lv.yumm.recipes.RecipeEvent
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.DifficultyBadge
import lv.yumm.ui.theme.LoadImageWithStates
import lv.yumm.ui.theme.TextBadge
import lv.yumm.ui.theme.Typography
import lv.yumm.ui.theme.YummTheme
import kotlin.math.roundToInt

@Composable
fun RecipesScreen(
    currentUserId: String,
    recipes: List<RecipeCardUiState>,
    navigateToLogin: () -> Unit,
    navigateToEdit: () -> Unit,
    navigateToView: (String) -> Unit,
    onEvent: (RecipeEvent) -> Unit
) {
    if (currentUserId != "col" && recipes.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            itemsIndexed(items = recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
                SwipeableItemWithActions(
                    modifier = Modifier.padding(vertical = 10.dp),
                    isLeftRevealed = recipe.isDeleteRevealed,
                    onLeftExpanded = { onEvent(RecipeEvent.OnDeleteRevealed(recipe.id)) },
                    onLeftCollapsed = { onEvent(RecipeEvent.OnDeleteCollapsed(recipe.id)) },
                    isRightRevealed = recipe.isEditRevealed,
                    onRightExpanded = { onEvent(RecipeEvent.OnEditRevealed(recipe.id)) },
                    onRightCollapsed = { onEvent(RecipeEvent.OnEditCollapsed(recipe.id)) },
                    leftAction = {
                        Surface(
                            onClick = {
                                onEvent(RecipeEvent.DeleteRecipe(recipe.id))
                                onEvent(RecipeEvent.OnDeleteCollapsed(recipe.id))
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(80.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                tint = Color.White,
                                contentDescription = "delete item"
                            )
                        }
                    },
                    rightAction = {
                        Surface(
                            onClick = {
                                onEvent(RecipeEvent.SetRecipeToUi(false, recipe.id))
                                onEvent(RecipeEvent.OnEditCollapsed(recipe.id))
                                navigateToEdit()
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(80.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                tint = Color.White,
                                contentDescription = "edit item"
                            )
                        }
                    },
                ) {
                    RecipeCard(recipe, Modifier) {
                        onEvent(RecipeEvent.OnCardClicked(recipe.id))
                        navigateToView(recipe.id)
                    }
                }
            }
        }
    } else if (currentUserId == "col") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "To save and create recipes, log in or create an account",
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
            LoginButton(
                text = "Log in"
            ) { navigateToLogin() }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "Empty here, click + to create a recipe...",
                style = Typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeCardUiState, modifier: Modifier, onClick: (String) -> Unit) {
    Card(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {onClick(recipe.id)}
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(all = 10.dp)
                .weight(1f)
                .height(100.dp)
        ) {
            LoadImageWithStates(
                url = recipe.imageUrl,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = recipe.description,
                    modifier = Modifier,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            recipe.type?.let {
                TextBadge(
                    modifier = Modifier.weight(1f),
                    text = it.name,
                )
            }
            DifficultyBadge(
                modifier = Modifier,
                difficulty = recipe.difficulty.toInt(),
            )
            TextBadge(
                modifier = Modifier.weight(1f),
                text = recipe.duration.toTimestamp(),
            )
        }
    }
}

@Composable
fun SwipeableItemWithActions(
    shape: Shape = RoundedCornerShape(16.dp),
    isLeftRevealed: Boolean = false,
    isRightRevealed: Boolean = false,
    leftAction: (@Composable () -> Unit)? = null,
    rightAction: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    onRightExpanded: () -> Unit = {},
    onLeftExpanded: () -> Unit = {},
    onRightCollapsed:() -> Unit = {},
    onLeftCollapsed:() -> Unit = {},
    content: @Composable () -> Unit
) {
    var actionWidth by remember { mutableFloatStateOf(0f) }

    val offset = remember { Animatable(initialValue = 0f) }

    val scope = rememberCoroutineScope()

    val min = if (rightAction == null) 0f else -actionWidth
    val max = if (leftAction == null) 0f else actionWidth

    LaunchedEffect(isRightRevealed, isLeftRevealed, actionWidth) {
        if (isRightRevealed) {
            offset.animateTo(-actionWidth)
        } else if(isLeftRevealed) {
            offset.animateTo(actionWidth)
        } else {
            offset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.onSizeChanged { actionWidth = (it.width + 20).toFloat() }
            ) {
                leftAction?.invoke()
            }
            rightAction?.invoke()
        }
        Surface(
            color = Color.Transparent,
            shape = shape,
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(actionWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offset.value + dragAmount)
                                    .coerceIn(min, max)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value <= -actionWidth * 3f / 4f -> {
                                    scope.launch {
                                        offset.animateTo(-actionWidth)
                                        onRightExpanded()
                                        onLeftCollapsed()
                                    }
                                }

                                offset.value >= -actionWidth * 3f / 4f && offset.value <= actionWidth * 3f / 4f -> {
                                    scope.launch {
                                        offset.animateTo(0f)
                                        onRightCollapsed()
                                        onLeftCollapsed()
                                    }
                                }

                                else -> {
                                    scope.launch {
                                        offset.animateTo(actionWidth)
                                        onLeftExpanded()
                                        onRightCollapsed()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun RecipeCardPreview() {
    YummTheme {
        RecipeCard(
            modifier = Modifier,
            recipe = RecipeCardUiState(
                id = "0",
                title = "Recipe title",
                description = "Some very very very very very very long description",
                difficulty = 2,
                duration = 30000L,
                type = RecipeType.LUNCH,
                imageUrl = "https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630"
            )
        ) {}
    }
}