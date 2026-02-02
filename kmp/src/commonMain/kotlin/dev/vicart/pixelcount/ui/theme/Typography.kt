package dev.vicart.pixelcount.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.vicart.pixelcount.resources.GoogleSans
import dev.vicart.pixelcount.resources.Res
import org.jetbrains.compose.resources.Font

private val baseTypography = Typography()

private val googleFontFamily: FontFamily
    @Composable get() = FontFamily(
        fonts = listOf(
            Font(
                resource = Res.font.GoogleSans,
                variationSettings = FontVariation.Settings(
                    weight = FontWeight.Normal,
                    style = FontStyle.Normal
                )
            ),
            Font(
                resource = Res.font.GoogleSans,
                variationSettings = FontVariation.Settings(
                    weight = FontWeight.Bold,
                    style = FontStyle.Normal
                )
            ),
            Font(
                resource = Res.font.GoogleSans,
                variationSettings = FontVariation.Settings(
                    weight = FontWeight.SemiBold,
                    style = FontStyle.Normal
                )
            )
        )
    )

val typography: Typography
    @Composable get() = Typography(
        displayLarge = baseTypography.displayLarge.copy(fontFamily = googleFontFamily),
        displayMedium = baseTypography.displayMedium.copy(fontFamily = googleFontFamily),
        displaySmall = baseTypography.displaySmall.copy(fontFamily = googleFontFamily),
        headlineLarge = baseTypography.headlineLarge.copy(fontFamily = googleFontFamily),
        headlineMedium = baseTypography.headlineMedium.copy(fontFamily = googleFontFamily),
        headlineSmall = baseTypography.headlineSmall.copy(fontFamily = googleFontFamily),
        titleLarge = baseTypography.titleLarge.copy(fontFamily = googleFontFamily),
        titleMedium = baseTypography.titleMedium.copy(fontFamily = googleFontFamily),
        titleSmall = baseTypography.titleSmall.copy(fontFamily = googleFontFamily),
        bodyLarge = baseTypography.bodyLarge.copy(fontFamily = googleFontFamily),
        bodyMedium = baseTypography.bodyMedium.copy(fontFamily = googleFontFamily),
        bodySmall = baseTypography.bodySmall.copy(fontFamily = googleFontFamily),
        labelLarge = baseTypography.labelLarge.copy(fontFamily = googleFontFamily),
        labelMedium = baseTypography.labelMedium.copy(fontFamily = googleFontFamily),
        labelSmall = baseTypography.labelSmall.copy(fontFamily = googleFontFamily)
    )