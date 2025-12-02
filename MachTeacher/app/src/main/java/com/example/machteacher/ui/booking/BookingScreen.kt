package com.example.machteacher.ui.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

// Colores
private val CardBg = Color(0xFFF8F8FA)
private val Stroke = Color(0xFFE6E6EB)
private val SegBg = Color(0xFFEDEDF2)
private val Dark = Color(0xFF0C0C14)
private val Pill = Color(0xFFE9ECF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    mentorId: Long,
    onNavigateUp: () -> Unit,
    onBookingConfirmed: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar a Sesiones cuando success = true
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onBookingConfirmed()
            viewModel.consumeSuccess()
        }
    }


    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Reservar Sesión", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "con ${uiState.mentorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // ───────── Tabs Detalles / Carrito
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(SegBg)
                        .padding(4.dp)
                ) {
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        val half = (maxWidth / 2) - 2.dp
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SegItem("Detalles", tab == 0, Modifier.width(half)) { tab = 0 }
                            SegItem("Carrito", tab == 1, Modifier.width(half)) { tab = 1 }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (tab == 0) {
                    /* ────────────────────
                     *        DETALLES
                     * ──────────────────── */
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Stroke)
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            // Materia
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expandedSubject)
                                    },
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

                            // Tipo de Paquete
                            Text("Tipo de Paquete", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                BoxWithConstraints(Modifier.fillMaxWidth()) {
                                    val half = (maxWidth / 2) - 6.dp
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        PackageOptionChip(
                                            title = "Sesión Individual",
                                            selected = uiState.selectedPackage == "Sesión Individual",
                                            modifier = Modifier.width(half)
                                        ) { viewModel.updatePackage("Sesión Individual") }

                                        PackageOptionChip(
                                            title = "Paquete 3 Sesiones",
                                            badge = "-10%",
                                            selected = uiState.selectedPackage == "Paquete 3" ||
                                                    uiState.selectedPackage == "Paquete 3 Sesiones",
                                            modifier = Modifier.width(half)
                                        ) { viewModel.updatePackage("Paquete 3") }
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        PackageOptionChip(
                                            title = "Paquete 5 Sesiones",
                                            badge = "-15%",
                                            selected = uiState.selectedPackage == "Paquete 5" ||
                                                    uiState.selectedPackage == "Paquete 5 Sesiones",
                                            modifier = Modifier.width(half)
                                        ) { viewModel.updatePackage("Paquete 5") }

                                        Spacer(Modifier.width(half)) // hueco
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Modalidad
                            Text("Modalidad", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                val half = (maxWidth / 2) - 6.dp
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ModeChip(
                                        title = "Online",
                                        selected = uiState.selectedMode == "Online",
                                        modifier = Modifier.width(half)
                                    ) { viewModel.updateMode("Online") }

                                    ModeChip(
                                        title = "Presencial",
                                        selected = uiState.selectedMode == "Presencial",
                                        modifier = Modifier.width(half)
                                    ) { viewModel.updateMode("Presencial") }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Horario y Duración
                            Text("Horario y Duración", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))

                            // Hora de inicio
                            Text(
                                "Hora de inicio",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Spacer(Modifier.height(8.dp))
                            val hours = remember {
                                listOf(
                                    "08:00", "09:00", "10:00", "11:00",
                                    "12:00", "13:00", "14:00", "15:00",
                                    "16:00", "17:00", "18:00", "19:00"
                                )
                            }
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
                                    value = uiState.selectedTime.ifBlank { "Selecciona la hora" },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expandedTime)
                                    },
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
                                                if (uiState.selectedTime == h)
                                                    Icon(Icons.Filled.Check, null)
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
                            Text(
                                "Duración (horas)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expandedDuration)
                                    },
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
                                                if (uiState.duration == d)
                                                    Icon(Icons.Filled.Check, null)
                                            },
                                            onClick = {
                                                viewModel.updateDuration(d)
                                                expandedDuration = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Fecha
                            Text("Fecha", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))
                            CalendarCard(
                                selectedDate = uiState.selectedDate,
                                onDateSelected = { viewModel.updateDate(it) }
                            )

                            Spacer(Modifier.height(24.dp))

                            // Notas
                            Text("Notas (opcional)", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))
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
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                "Precio estimado: $${uiState.price}",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(Modifier.height(16.dp))


                            Button(
                                onClick = {
                                    viewModel.addToCart()
                                    tab = 1
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Dark)
                            ) {
                                Text("Agregar a carrito", color = Color.White)
                            }
                        }
                    }
                } else {
                    /* ────────────────────
                     *        CARRITO
                     * ──────────────────── */
                    val item = uiState.cart.firstOrNull()

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Stroke)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            if (item == null) {
                                Text(
                                    "Tu carrito está vacío",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Agrega sesiones desde la pestaña de detalles",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    "Resumen de tu compra",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(12.dp))

                                Text("Materia: ${item.subject}")
                                Text("Paquete: ${item.packageName}")
                                Text("Modalidad: ${item.mode}")
                                Text("Fecha: ${item.date}")
                                Text("Hora: ${item.time}")
                                Text("Duración: ${item.duration}")
                                if (item.notes.isNotBlank()) {
                                    Text("Notas: ${item.notes}")
                                }

                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Total: $${item.price}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }

                    if (item != null) {
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.confirmBooking() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Dark)
                        ) {
                            Text("Confirmar compra", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

/* ───────────────── Calendario visual ───────────────── */

@Composable
private fun CalendarCard(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val sdfMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    var monthCal by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                runCatching {
                    if (selectedDate.isNotBlank()) {
                        val sdfIn = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        time = sdfIn.parse(selectedDate)!!
                    }
                }
                set(Calendar.DAY_OF_MONTH, 1)
            }
        )
    }

    val selectedCal = remember(selectedDate) {
        runCatching {
            val sdfIn = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            Calendar.getInstance().apply { time = sdfIn.parse(selectedDate)!! }
        }.getOrNull()
    }

    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Stroke)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallRoundButton("‹") {
                    monthCal = (monthCal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                }
                Text(
                    text = sdfMonth.format(monthCal.time).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                SmallRoundButton("›") {
                    monthCal = (monthCal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                }
            }

            Spacer(Modifier.height(8.dp))

            val weekLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekLabels.forEach {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(it, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            val firstDayOfWeek = ((monthCal.get(Calendar.DAY_OF_WEEK) + 6) % 7)
            val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = ceil(totalCells / 7f).toInt()

            Column {
                var dayNum = 1
                repeat(rows) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(7) { col ->
                            val show =
                                (col >= firstDayOfWeek || dayNum > 1) && dayNum <= daysInMonth
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (show) {
                                    val cellCal = (monthCal.clone() as Calendar).apply {
                                        set(Calendar.DAY_OF_MONTH, dayNum)
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val isSelected =
                                        selectedCal?.let {
                                            it.get(Calendar.YEAR) == cellCal.get(Calendar.YEAR) &&
                                                    it.get(Calendar.MONTH) == cellCal.get(
                                                Calendar.MONTH
                                            ) &&
                                                    it.get(Calendar.DAY_OF_MONTH) == cellCal.get(
                                                Calendar.DAY_OF_MONTH
                                            )
                                        } ?: false

                                    val isToday =
                                        today.get(Calendar.YEAR) == cellCal.get(Calendar.YEAR) &&
                                                today.get(Calendar.MONTH) == cellCal.get(
                                            Calendar.MONTH
                                        ) &&
                                                today.get(Calendar.DAY_OF_MONTH) == cellCal.get(
                                            Calendar.DAY_OF_MONTH
                                        )

                                    DayPill(
                                        text = dayNum.toString(),
                                        selected = isSelected,
                                        isToday = isToday
                                    ) {
                                        val sdfOutLocal =
                                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        onDateSelected(sdfOutLocal.format(cellCal.time))
                                    }
                                    dayNum++
                                } else {
                                    Spacer(Modifier.size(1.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SmallRoundButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .border(1.dp, Stroke, RoundedCornerShape(10.dp))
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center
    ) { Text(label, style = MaterialTheme.typography.titleSmall) }
}

@Composable
private fun DayPill(
    text: String,
    selected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        selected -> Dark
        isToday -> Pill
        else -> Color.Transparent
    }
    val txtColor = if (selected) Color.White else Dark
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center
    ) { Text(text, color = txtColor, style = MaterialTheme.typography.bodyMedium) }
}

/* ───────────────── helpers UI ───────────────── */

@Composable
private fun SegItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val bg = if (selected) Color.White else Color.Transparent
    val txt = if (selected) Dark else Color.Black
    Box(
        modifier = modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, if (selected) Stroke else Color.Transparent, shape)
            .height(40.dp)
            .padding(horizontal = 16.dp)
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center
    ) { Text(title, color = txt, style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun PackageOptionChip(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val bg = if (selected) Dark else Color.White
    val fg = if (selected) Color.White else Color.Black
    val border = if (selected) Dark else Stroke
    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, border, shape)
            .background(bg)
            .noRippleClickable(onClick)
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

@Composable
private fun ModeChip(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val bg = if (selected) Dark else Color.White
    val fg = if (selected) Color.White else Color.Black
    val border = if (selected) Dark else Stroke
    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, border, shape)
            .background(bg)
            .noRippleClickable(onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) { Text(title, color = fg, style = MaterialTheme.typography.bodyMedium) }
}


private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(interactionSource = interaction, indication = null, onClick = onClick)
}
