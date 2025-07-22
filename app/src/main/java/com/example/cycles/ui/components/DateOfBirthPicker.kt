package com.example.cycles.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api


@Composable
fun DateOfBirthPicker(
    selectedDate: String, // Formato "DD/MM/AAAA"
    onDateSelected: (String) -> Unit, // Callback para la fecha completa
    modifier: Modifier = Modifier
) {
    // Estados internos para día, mes y año
    var selectedDay by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }

    // Actualizar estados internos cuando selectedDate externo cambia (al inicializar o registrar)
    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotEmpty() && selectedDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            val parts = selectedDate.split("/")
            selectedDay = parts[0]
            selectedMonth = parts[1]
            selectedYear = parts[2]
        } else {
            selectedDay = ""
            selectedMonth = ""
            selectedYear = ""
        }
    }

    // Listas de opciones para los selectores
    val days = (1..31).map { it.toString().padStart(2, '0') }
    val months = (1..12).map { it.toString().padStart(2, '0') }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo currentYear - 100).map { it.toString() } // Últimos 100 años

    Row(modifier = modifier.fillMaxWidth()) {
        // Selector de Día
        DatePickerDropdown(
            label = "Día",
            options = days,
            selectedValue = selectedDay,
            onValueSelected = { newDay ->
                selectedDay = newDay
                updateFullDate(selectedDay, selectedMonth, selectedYear, onDateSelected)
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        // Selector de Mes
        DatePickerDropdown(
            label = "Mes",
            options = months,
            selectedValue = selectedMonth,
            onValueSelected = { newMonth ->
                selectedMonth = newMonth
                updateFullDate(selectedDay, selectedMonth, selectedYear, onDateSelected)
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        // Selector de Año
        DatePickerDropdown(
            label = "Año",
            options = years,
            selectedValue = selectedYear,
            onValueSelected = { newYear ->
                selectedYear = newYear
                updateFullDate(selectedDay, selectedMonth, selectedYear, onDateSelected)
            },
            modifier = Modifier.weight(1.5f) // Año puede ser un poco más ancho
        )
    }
}

// Función auxiliar para actualizar la fecha completa y llamar al callback
private fun updateFullDate(
    day: String,
    month: String,
    year: String,
    onDateSelected: (String) -> Unit
) {
    if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
        // Aquí podrías añadir una validación más robusta para fechas inválidas (ej. 31 de Febrero)
        val formattedDate = "$day/$month/$year"
        onDateSelected(formattedDate)
    } else {
        onDateSelected("") // Vaciar si no todos los campos están seleccionados
    }
}

// Componente Composable para un selector desplegable individual (Día, Mes, Año)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDropdown(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = { /* No se permite edición directa */ },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, // Indica que es un anchor para un campo no editable
                    true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}