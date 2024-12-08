package lv.yumm.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import lv.yumm.R
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = Modifier.padding(vertical = 10.dp),
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
    )
}

@Composable
fun BottomNavBar(
    toHome: () -> Unit,
    toLists: () -> Unit,
    toCalendar: () -> Unit,
    toRecipes: () -> Unit,
    toProfile:() -> Unit,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ){
            NavIcon(
                id = R.drawable.ic_home,
                description = "Home"
            ) { toHome() }
            NavIcon(
                id = R.drawable.ic_recipes,
                description = "Home"
            ) { toRecipes() }
            NavIcon(
                id = R.drawable.ic_list,
                description = "Home"
            ) { toLists() }
            NavIcon(
                id = R.drawable.ic_calendar,
                description = "Home"
            ) { toCalendar() }
            NavIcon(
                id = R.drawable.ic_profile,
                description = "Home"
            ) { toProfile() }
        }
    }
}

@Composable
fun NavIcon(id: Int, description: String, onClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        shape = CircleShape,
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = description,
            tint = Color.White,
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
        )
    }
}

@Composable
fun LoadImageWithStates(url: String, modifier: Modifier) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .size(coil.size.Size.ORIGINAL)
        .build())

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                // Placeholder while loading
                Image(
                    painter = ColorPainter(Color.Gray),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is AsyncImagePainter.State.Error -> {
                // Placeholder for error
                Image(
                    painter = ColorPainter(Color.Red),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // Successfully loaded image
                Image(
                    painter = painter,
                    contentDescription = "Loaded Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview
@Composable
fun TopBarPreview() {
    YummTheme { TopBar("Yumm") }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    YummTheme { BottomNavBar({},{},{},{},{}) }
}