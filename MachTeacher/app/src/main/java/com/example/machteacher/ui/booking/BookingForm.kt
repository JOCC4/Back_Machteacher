@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.machteacher.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────
// FORMULARIO DE RESERVA
// ─────────────────────────────────────────────────────────────

@Composable
fun BookingForm(
    viewModel: BookingViewModel,
    uiState: BookingUiState
) {
    val Stroke = Color(0xFFE6E6EB)
    val CardBg = Color(0xFFF8F8FA)
    val Dark = Color(0xFF0C0C14)

    Column {

        // ─────── Materia
        Text("Materia", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        var expandedSubject by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedSubject,
            onExpandedChange = { expandedSubject = it }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.dp, Stroke, RoundedCornerShape(12.dp)),
                value = uiState.selectedSubject ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona la materia") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedSubject) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    disabledContainerColor = CardBg
                )
            )
            ExposedDropdownMenu(
                expanded = expandedSubject,
                onDismissRequest = { expandedSubject = false }
            ) {
                uiState.subjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject) },
                        onClick = {
                            viewModel.updateSubject(subject)
                            expandedSubject = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ─────── Paquetes
        Text("Tipo de Paquete", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        val halfWidth = Modifier
            .fillMaxWidth()
            .weight(1f)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PackageChip(
                title = "Sesión Individual",
                selected = uiState.selectedPackage == "Sesión Individual",
                modifier = halfWidth
            ) { viewModel.updatePackage("Sesión Individual") }

            PackageChip(
                title = "Paquete 3 Sesiones",
                badge = "-10%",
                selected = uiState.selectedPackage.contains("Paquete 3"),
                modifier = halfWidth
            ) { viewModel.updatePackage("Paquete 3") }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PackageChip(
                title = "Paquete 5 Sesiones",
                badge = "-15%",
                selected = uiState.selectedPackage.contains("Paquete 5"),
                modifier = halfWidth
            ) { viewModel.updatePackage("Paquete 5") }

            Spacer(modifier = halfWidth)
        }

        Spacer(Modifier.height(24.dp))

        // ─────── Fecha, Hora, Duración
        BookingDateTimeSection(viewModel = viewModel, uiState = uiState)

        Spacer(Modifier.height(24.dp))

        // ─────── Notas
        Text("Notas (opcional)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { viewModel.updateNotes(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Escribe información adicional...") },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Stroke,
                focusedBorderColor = Dark
            )
        )

        Spacer(Modifier.height(20.dp))
        Text("Precio estimado: $${uiState.price}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.confirmBooking() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Dark)
        ) {
            Text("Confirmar Reserva", color = Color.White)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE DE CHIP PARA LOS PAQUETES
// ─────────────────────────────────────────────────────────────

@Composable
fun PackageChip(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val bg = if (selected) Color(0xFF0C0C14) else Color.White
    val fg = if (selected) Color.White else Color.Black
    val border = if (selected) Color(0xFF0C0C14) else Color(0xFFE6E6EB)

    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, border, shape)
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 18.dp, horizontal = 14.dp)
    ) {
        Text(title, color = fg, style = MaterialTheme.typography.bodyMedium)
        if (badge != null) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF1F2F6))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(badge, color = Color(0xFF333333), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE FECHA / HORA / DURACIÓN
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDateTimeSection(viewModel: BookingViewModel, uiState: BookingUiState) {
    val Stroke = Color(0xFFE6E6EB)
    val CardBg = Color(0xFFF8F8FA)

    Text("Horario y Duración", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(12.dp))

    // Hora de inicio
    Text("Hora de inicio", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    Spacer(Modifier.height(8.dp))
    val hours = listOf("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00")
    var expandedTime by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandedTime,
        onExpandedChange = { expandedTime = it }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, Stroke, RoundedCornerShape(12.dp)),
            value = if (uiState.selectedTime.isBlank()) "Selecciona la hora" else uiState.selectedTime,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTime) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                disabledContainerColor = CardBg
            )
        )
        ExposedDropdownMenu(
            expanded = expandedTime,
            onDismissRequest = { expandedTime = false }
        ) {
            hours.forEach { h ->
                DropdownMenuItem(
                    text = { Text(h) },
                    trailingIcon = {
                        if (uiState.selectedTime == h) Icon(Icons.Filled.Check, contentDescription = null)
                    },
                    onClick = {
                        viewModel.updateTime(h)
                        expandedTime = false
                    }
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Duración
    Text("Duración (horas)", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    Spacer(Modifier.height(8.dp))
    val durations = listOf("1 hora", "1.5 horas", "2 horas", "3 horas")
    var expandedDuration by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedDuration,
        onExpandedChange = { expandedDuration = it }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, Stroke, RoundedCornerShape(12.dp)),
            value = uiState.duration,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDuration) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                disabledContainerColor = CardBg
            )
        )
        ExposedDropdownMenu(
            expanded = expandedDuration,
            onDismissRequest = { expandedDuration = false }
        ) {
            durations.forEach { d ->
                DropdownMenuItem(
                    text = { Text(d) },
                    trailingIcon = {
                        if (uiState.duration == d) Icon(Icons.Filled.Check, contentDescription = null)
                    },
                    onClick = {
                        viewModel.updateDuration(d)
                        expandedDuration = false
                    }
                )
            }
        }
    }
}
