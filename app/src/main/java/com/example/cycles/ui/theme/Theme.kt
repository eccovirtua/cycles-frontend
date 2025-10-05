package com.example.cycles.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
// --- ESQUEMA 8: MENTA FRESCA (Light Scheme) ---
private val MintScheme = lightColorScheme(
    primary = mintPrimary,
    onPrimary = mintOnPrimary,
    secondary = mintSecondary,
    onSecondary = Color.White,
    background = mintBackground,
    onBackground = Color.Black,
    surface = mintSurface,
    onSurface = Color.Black,
    // Puedes usar los contenedores de tus esquemas existentes si quieres más detalle
    primaryContainer = primaryContainerLight
)

// --- ESQUEMA 9: PUESTA DE SOL (Dark Scheme con tonos cálidos) ---
private val SunsetDarkScheme = darkColorScheme(
    primary = sunsetPrimary,
    onPrimary = sunsetOnPrimary,
    secondary = sunsetSecondary,
    onSecondary = Color.White,
    background = Color(0xFF1E1E1E), // Fondo gris oscuro para contraste
    onBackground = sunsetSurface,
    surface = Color(0xFF2C2C2C),
    onSurface = sunsetPrimary,
    // Usamos el color de fondo para el error para un contraste vibrante
    error = sunsetBackground
)

// --- ESQUEMA 10: MORADO CÓSMICO (Dark Scheme) ---
private val CosmicPurpleScheme = darkColorScheme(
    primary = deepPurplePrimary,
    onPrimary = deepPurpleOnPrimary,
    secondary = deepPurpleSecondary,
    onSecondary = Color.White,
    background = deepPurpleBackground,
    onBackground = Color.White,
    surface = deepPurpleSurface,
    onSurface = Color.White,
)


private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val SapphireLightScheme = lightColorScheme(
    primary = sapphirePrimary,
    onPrimary = sapphireOnPrimary,
    secondary = sapphireSecondary,
    onSecondary = Color.Black,
    background = sapphireBackground,
    onBackground = Color.Black,
    surface = sapphireSurface,
    onSurface = Color.Black,
    // Utilizamos el contenedor primario para el surfaceVariant
    surfaceVariant = sapphireSurface,
    onSurfaceVariant = Color.Black
)
// --- ESQUEMA 12: CORAL DEL DESIERTO ---
private val DesertCoralDarkScheme = darkColorScheme(
    primary = coralPrimary,
    onPrimary = coralOnPrimary,
    secondary = coralSecondary,
    onSecondary = Color.Black,
    // Usa un fondo muy oscuro para contrastar con los acentos
    background = Color(0xFF1C0A04),
    onBackground = Color.White,
    surface = Color(0xFF3D271E),
    onSurface = coralSurface,
    // Asegura que los errores sean muy visibles
    error = Color.Red
)
// --- ESQUEMA 13: CABAÑA DE TIERRA ---
private val EarthCabinDarkScheme = darkColorScheme(
    primary = earthPrimary,
    onPrimary = earthOnPrimary,
    secondary = earthSecondary,
    onSecondary = Color.White,
    background = earthBackground,
    onBackground = Color.White,
    surface = earthSurface,
    onSurface = Color.White,
    // Usamos el primario para el contenedor, manteniendo la coherencia monocromática
    primaryContainer = earthPrimary
)
private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)
private val PurpleScheme = lightColorScheme(
    primary = purplePrimary,
    onPrimary = purpleOnPrimary,
    primaryContainer = purpleContainer,
    onPrimaryContainer = Color.Black, // Usas un color existente para el texto

    // Asignas los nuevos colores a Background y Surface
    background = purpleBackground,
    onBackground = Color.Black,
    surface = purpleSurface,
    onSurface = Color.Black,

    // Puedes usar los secundarios/terciarios de tus otros temas si quieres
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    // ...
)



private val TertiaryHighContrastScheme = darkColorScheme(
    // 🎯 Un color principal brillante y contrastante
    primary = tertiaryLightHighContrast,
    onPrimary = Color.Black,
    // 🎯 Usamos el color secundario como fondo principal oscuro
    background = secondaryDark,
    onBackground = Color.White,
    // 🎯 Mantiene los botones y superficies brillantes
    surface = surfaceBrightDark,
    onSurface = onSurfaceDarkHighContrast,
    // ... puedes rellenar el resto de los colores con los que quieras
    secondary = primaryDark,
)
private val DarkTonalLightScheme = lightColorScheme(
    primary = primaryDark, // Usa el primario oscuro como color principal
    onPrimary = Color.White,
    primaryContainer = primaryDarkMediumContrast,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    // 🎯 Haz los botones delineados y secundarios más intensos
    secondary = secondaryDarkHighContrast,
    onSecondary = Color.White
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

//lista de temas para ciclar
val ThemeColors = listOf(
    darkScheme,
    highContrastLightColorScheme,
    lightScheme,
    mediumContrastDarkColorScheme,
    TertiaryHighContrastScheme,
    DarkTonalLightScheme,
    highContrastDarkColorScheme,
    mediumContrastLightColorScheme,
    PurpleScheme,
    MintScheme,
    SunsetDarkScheme,
    CosmicPurpleScheme,
    SapphireLightScheme,
    DesertCoralDarkScheme,
    EarthCabinDarkScheme
)

//private val GlobalColorScheme = darkScheme global este tema
@Composable
fun CyclesTheme(
colorScheme: ColorScheme,
content: @Composable() () -> Unit
) {
    // 🎯 Asignamos directamente el tema fijo que queremos usar
//    val colorScheme = GlobalColorScheme

    // El resto de la función permanece igual
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

