package lv.yumm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryAndCameraLauncher(
    modifier: Modifier = Modifier,
    saveUri: (String) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

    val file = context.createImageFile()

    val uri = FileProvider.getUriForFile(
        context,
        "com.app.id.fileProvider", file
    )

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) imageUri = uri
            if (imageUri.toString().isNotEmpty()) {
                saveUri(imageUri.toString())
                Timber.tag("myImageUri").d("$imageUri ")
            }
        }
    )

    val mediaPermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(
            Manifest.permission.READ_MEDIA_IMAGES
        ) else listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                if (data != null) {
                    imageUri = Uri.parse(data.data.toString())

                    if (imageUri.toString().isNotEmpty()) {
                        saveUri(imageUri.toString())
                        Timber.tag("myImageUri").d("$imageUri ")
                    }
                }
            }
        }

    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    val hasCameraPermission = cameraPermissionState.status.isGranted
    val hasMediaPermission = mediaPermissionState.allPermissionsGranted

    Column (
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxHeight(),
    ){
        Button(
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.wrapContentWidth(),
            onClick = {
                if (hasCameraPermission) {
                    cameraLauncher.launch(uri)
                } else {
                    cameraPermissionState.launchPermissionRequest()
                    cameraLauncher.launch(uri)
                }
            }
        ) {
            Text(
                text = "Take a photo",
                maxLines = 1,
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
            )
        }
        Button(
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.wrapContentWidth(),
            onClick =
            {
                if (hasMediaPermission) {
                    galleryLauncher.launch(galleryIntent)
                } else {
                    mediaPermissionState.launchMultiplePermissionRequest()
                    galleryLauncher.launch(galleryIntent)
                }
            }
        ) {
            Text(
                text = "Choose a photo",
                maxLines = 1,
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
            )
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}