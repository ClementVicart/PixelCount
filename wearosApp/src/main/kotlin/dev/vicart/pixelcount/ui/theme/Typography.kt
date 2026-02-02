package dev.vicart.pixelcount.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.material3.Typography
import dev.vicart.pixelcount.R

private val baseTypography = Typography()

@OptIn(ExperimentalTextApi::class)
private val googleFontFamily: FontFamily = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.googlesans,
            variationSettings = FontVariation.Settings(
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            )
        ),
        Font(
            resId = R.font.googlesans,
            variationSettings = FontVariation.Settings(
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        Font(
            resId = R.font.googlesans,
            variationSettings = FontVariation.Settings(
                weight = FontWeight.SemiBold,
                style = FontStyle.Normal
            )
        ),
    )
)

val typography: Typography = Typography(
    defaultFontFamily = googleFontFamily,
)