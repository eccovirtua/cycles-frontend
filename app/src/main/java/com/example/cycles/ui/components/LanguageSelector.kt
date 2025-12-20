package com.example.cycles.ui.components

// Agrega estos imports
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun LanguageSelector(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    // Detectamos el idioma actual (por defecto o el que haya configurado el usuario)
    val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language
        ?: java.util.Locale.getDefault().language

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Opción Español
        LanguageOption(
            text = "Español",
            code = "es",
            isSelected = currentLocale == "es"
        )

        // Separador visual
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "|",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Opción Inglés
        LanguageOption(
            text = "English",
            code = "en",
            isSelected = currentLocale == "en"
        )
    }
}

@Composable
fun LanguageOption(text: String, code: String, isSelected: Boolean) {
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val weight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    val scale = if (isSelected) 1.1f else 1.0f

    Text(
        text = text,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = weight,
            color = color
        ),
        modifier = Modifier
            .scale(scale)
            .clickable {
                // Lógica para cambiar el idioma
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
    )
}