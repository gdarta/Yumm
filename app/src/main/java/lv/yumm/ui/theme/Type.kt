package lv.yumm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lv.yumm.R

val mulishFontFamily = FontFamily(
    Font(R.font.mulish_black, FontWeight.Black),
    Font(R.font.mulish_bold, FontWeight.Bold),
    Font(R.font.mulish_extra_bold, FontWeight.ExtraBold),
    Font(R.font.mulish_extra_light, FontWeight.ExtraLight),
    Font(R.font.mulish_light, FontWeight.Light),
    Font(R.font.mulish_medium, FontWeight.Medium),
    Font(R.font.mulish_regular, FontWeight.Normal),
    Font(R.font.mulish_semi_bold, FontWeight.SemiBold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    displayLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp
    )
)
