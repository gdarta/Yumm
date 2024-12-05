package lv.yumm.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import lv.yumm.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.height(70.dp),
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
        modifier = Modifier.height(100.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            NavIcon(
                id = R.drawable.ic_home,
                description = "Home",
                { toHome() }
            )
            NavIcon(
                id = R.drawable.ic_recipes,
                description = "Home",
                { toRecipes() }
            )
            NavIcon(
                id = R.drawable.ic_list,
                description = "Home",
                { toLists() }
            )
            NavIcon(
                id = R.drawable.ic_calendar,
                description = "Home",
                { toCalendar() }
            )
            NavIcon(
                id = R.drawable.ic_profile,
                description = "Home",
                { toProfile() }
            )
        }
    }
}

@Composable
fun NavIcon(id: Int, description: String, onClick: () -> Unit) {
    Icon(
        painter = painterResource(id),
        contentDescription = description,
        modifier = Modifier
            .padding(all = 10.dp)
            .size(30.dp)
            .clickable { onClick() }
    )
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