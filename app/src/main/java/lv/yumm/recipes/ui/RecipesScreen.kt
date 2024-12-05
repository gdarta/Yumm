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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var cardWidth by remember { mutableStateOf(0) }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
    ) {
        itemsIndexed(items = recipes, key = {_, recipe -> recipe.id}) { index, recipe ->
            SwipeableItemWithActions(
                isLeftRevealed = recipe.isDeleteRevealed,
                isRightRevealed = recipe.isEditRevealed,
                leftAction = {
                    Button(
                        onClick = {},
                        content = { Icon(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null) },
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((cardWidth/6).dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )
                },
                rightAction = {
                    Button(
                        onClick = {},
                        content = { Icon(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null) },
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((cardWidth/6).dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )
                },
            ){
                RecipeCard(
                    recipe, Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .onGloballyPositioned {
                            cardWidth = it.size.width
                        }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeCardUiState, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(80.dp),
        shape = RoundedCornerShape(3.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {}
    ) {
        Row() {
            LoadImageWithStates(
                url = recipe.imageUrl,
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
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
                .onSizeChanged {
                    actionWidth = (it.width / 6).toFloat()
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            leftAction()
            rightAction()
        }
        Surface(
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
                                offset.value <= -actionWidth / 2f -> {
                                    scope.launch {
                                        offset.animateTo(-actionWidth)
                                        onRightExpanded()
                                        onLeftCollapsed
                                    }
                                }

                                offset.value >= -actionWidth / 2f && offset.value <= actionWidth / 2f -> {
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