package com.example.machteacher.ui.search


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BorderGray = Color(0xFFE6E6E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    onApply: () -> Unit = {},
    onClear: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var subject by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Top
        Text("Filtros de Búsqueda", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        LabeledDropdown("Materia", subject, { subject = it }, listOf("Programación","Matemáticas","Anatomía","Medicina"))
        LabeledDropdown("Semestre", semester, { semester = it }, (1..12).map { "$it" })
        LabeledDropdown("Modalidad", mode, { mode = it }, listOf("Online","Presencial"))
        LabeledDropdown("Rango de Precio", price, { price = it }, listOf("$0-$10","$10-$20","$20-$30","$30+"))
        LabeledDropdown("Disponibilidad", availability, { availability = it }, listOf("Hoy","Mañana","Esta semana"))

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { subject=""; semester=""; mode=""; price=""; availability=""; onClear() }) {
                Text("Limpiar Filtros")
            }
            Spacer(Modifier.width(12.dp))
            Button(onClick = onApply) { Text("Aplicar") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    Text(label)
    Spacer(Modifier.height(6.dp))
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Seleccionar ${label.lowercase()}") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderGray,
                focusedBorderColor = BorderGray
            ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { o ->
                DropdownMenuItem(text = { Text(o) }, onClick = { onValueChange(o); expanded = false })
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}
