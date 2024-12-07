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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lv.yumm.R
import lv.yumm.recipes.RecipeCardUiState
import lv.yumm.recipes.data.RecipeType
import lv.yumm.recipes.data.toTimestamp
import lv.yumm.ui.theme.LoadImageWithStates
import lv.yumm.ui.theme.YummTheme
import kotlin.math.roundToInt

@Composable
fun RecipesScreen(recipes: List<RecipeCardUiState>, navigateToCreateScreen: () -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        itemsIndexed(items = recipes, key = {_, recipe -> recipe.id}) { index, recipe ->
            SwipeableItemWithActions(
                isLeftRevealed = recipe.isDeleteRevealed,
                isRightRevealed = recipe.isEditRevealed,
                leftAction = {
                    Surface(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp),
                        color = Color.Red,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null
                        )
                    }
                },
                rightAction = {
                    Surface(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp),
                        color = Color.Green,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null
                        )
                    }
                },
            ) {
                RecipeCard(
                    recipe,
                    Modifier
                )
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeCardUiState, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Max),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {}
    ) {
        Row() {
            LoadImageWithStates(
                url = recipe.imageUrl,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
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

@Composable
fun SwipeableItemWithActions(
    isLeftRevealed: Boolean = false,
    isRightRevealed: Boolean = false,
    leftAction: @Composable () -> Unit = {},
    rightAction: @Composable () -> Unit = {},
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

    LaunchedEffect(isRightRevealed, isLeftRevealed, actionWidth) {
        if (isRightRevealed) {
            offset.animateTo(-actionWidth)
        } else if(isLeftRevealed) {
            offset.animateTo(actionWidth)
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
                leftAction()
            }
            rightAction()
        }
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(actionWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offset.value + dragAmount)
                                    .coerceIn(-actionWidth, actionWidth)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value <= -actionWidth * 3f / 4f -> {
                                    scope.launch {
                                        offset.animateTo(-actionWidth)
                                        onRightExpanded()
                                        onLeftCollapsed
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