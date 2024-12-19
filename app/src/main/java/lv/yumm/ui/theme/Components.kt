package lv.yumm.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import lv.yumm.R
import lv.yumm.ui.state.ConfirmationDialogUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(uiState: ConfirmationDialogUiState) {
    BasicAlertDialog(
        onDismissRequest = uiState.onDismissDialog,
        modifier = Modifier
            .width(intrinsicSize = IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(all = 30.dp)
        ) {
            Text(
                text = uiState.title,
                style = Typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = uiState.description,
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = uiState.onConfirmButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = uiState.confirmButtonText
                    )
                }
                Button(
                    onClick = uiState.onCancelButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = uiState.cancelButtonText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(title: String, description: String, onDismiss: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(intrinsicSize = IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(all = 30.dp)
        ) {
            Text(
                text = title,
                style = Typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = description,
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(.8f)
            ) {
                Text(
                    text = "Close"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = Modifier,
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
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
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
                .size(30.dp)
        )
    }
}

//todo this is ugly
@Composable
fun LoadImageWithStates(url: String, modifier: Modifier) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .size(coil.size.Size.ORIGINAL)
        .scale(Scale.FIT)
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
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
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

@Preview
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(
        title = "Warning",
        description = "You messed up"
    ) { }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        ConfirmationDialogUiState(
            title = "You sure?",
            cancelButtonText = "go back",
            confirmButtonText = "i am sure"
        )
    )
}