package com.example.cycles.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale
import com.example.cycles.R
import com.example.cycles.ui.theme.HelveticaFamily

@Composable
fun DateOfBirthPicker(
    selectedDate: String, // Formato "DD/MM/AAAA"
    onDateSelected: (String) -> Unit, // Callback para la fecha completa
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    // Si selectedDate tiene valor, úsalo para inicializar el calendario
    if (selectedDate.isNotEmpty() && selectedDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
        val parts = selectedDate.split("/")
        // Los meses de Calendar son 0-indexados (Enero=0)
        calendar.set(
            parts[2].toInt(),
            parts[1].toInt() - 1,
            parts[0].toInt()
        )
    }

    // Definimos el DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        // Listener cuando el usuario selecciona una fecha
        { _: DatePicker, year: Int, month: Int, day: Int ->
            // Formatear al formato "DD/MM/AAAA"
            val formattedDate = String.format(
                Locale.getDefault(),
                "%02d/%02d/%d",
                day,
                month + 1, // Sumamos 1 porque los meses son 0-indexados
                year
            )
            onDateSelected(formattedDate)
        },
        // Valores iniciales para el selector
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Opcional: Establecer una fecha máxima (la fecha actual)
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    Box(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .clickable{
                datePickerDialog.show()
            }
   ) {


        // --- CAMPO DE TEXTO UNIFICADO ---
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { Text(stringResource(R.string.register_dobfield), style = MaterialTheme.typography.bodySmall, fontFamily = HelveticaFamily,) },
            leadingIcon = {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Icono de calendario")
            },
            shape = RoundedCornerShape(35),
            colors = OutlinedTextFieldDefaults.colors(
                // --- Colores de estado Normal (Unfocused) ---
                focusedBorderColor = MaterialTheme.colorScheme.primary, // El que se ve al tocar
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant, // Borde cuando está quieto
                unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Color de fondo

                // --- Colores de estado Deshabilitado (NUEVAS PROPIEDADES) ---
                // Forzamos los colores deshabilitados para que se vean normales:
                disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant, // Mismo color de borde que 'unfocused'
                disabledTextColor = MaterialTheme.colorScheme.onSurface,       // Texto (fecha seleccionada) en color normal
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant, // Placeholder en color normal
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Icono en color normal
                disabledContainerColor = MaterialTheme.colorScheme.surface           // Color de fondo normal
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}