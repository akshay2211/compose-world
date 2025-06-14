package compose.world.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import compose.world.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val PoppinsFontFamily = FontFamily(
    Font(resId = R.font.poppins_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(resId = R.font.poppins_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(resId = R.font.poppins_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resId = R.font.poppins_mediumitalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(resId = R.font.poppins_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
    Font(resId = R.font.poppins_thinitalic, weight = FontWeight.Thin, style = FontStyle.Italic),
    Font(resId = R.font.poppins_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(resId = R.font.poppins_lightitalic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resId = R.font.poppins_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(resId = R.font.poppins_semibolditalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(resId = R.font.poppins_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resId = R.font.poppins_bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resId = R.font.poppins_extrabold, weight = FontWeight.ExtraBold, style = FontStyle.Normal),
    Font(resId = R.font.poppins_extrabolditalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
    Font(resId = R.font.poppins_extralight, weight = FontWeight.ExtraLight, style = FontStyle.Normal),
    Font(resId = R.font.poppins_extralightitalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(resId = R.font.poppins_black, weight = FontWeight.Black, style = FontStyle.Normal),
    Font(resId = R.font.poppins_blackitalic, weight = FontWeight.Black, style = FontStyle.Italic),
)

val SoraFontFamily = FontFamily(
    Font(resId = R.font.sora_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(resId = R.font.sora_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(resId = R.font.sora_thin, weight = FontWeight.Thin, style = FontStyle.Normal)
)