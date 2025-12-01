package com.example.machteacher.ui.mentorprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.machteacher.ui.components.SubjectChips

// REVERTIDO: Colores definidos localmente como antes
private val BorderGray = Color(0xFFE6E6E6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val AppBlue = Color(0xFF1E49FF)
private val OnlineGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorProfileScreen(
    userId: Long,
    onBack: () -> Unit = {},
    onBook: () -> Unit = {},
    vm: MentorProfileViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(userId) { vm.loadMentorProfile(userId) }

    val profile = state.profile

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null) }
            Text("Perfil del Mentor", fontWeight = FontWeight.Medium, color = TextPrimary)
        }
        Divider(Modifier.padding(top = 8.dp), color = BorderGray)
        Spacer(Modifier.height(12.dp))

        // Header card
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray),
            color = Color.White
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = (profile?.name?.firstOrNull()?.uppercase() ?: "M")
                        Text(initial, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(OnlineGreen)
                            .offset(x = (-8).dp, y = 12.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(Modifier.weight(1f)) {
                        Text(profile?.name ?: "Mentor", fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text(profile?.career ?: "—", color = TextSecondary, fontSize = 13.sp)
                        val uniSem = listOfNotNull(
                            profile?.university?.takeIf { it.isNotBlank() },
                            profile?.semester?.takeIf { it.isNotBlank() }?.let { "$it Sem." }
                        ).joinToString(" • ").ifBlank { "—" }
                        Text(uniSem, color = TextSecondary, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107))
                        Spacer(Modifier.width(4.dp))
                        // REVERTIDO: Se vuelve a usar el valor fijo
                        Text("4.9 (127)", color = TextPrimary)
                    }
                }
                Spacer(Modifier.height(8.dp))
                // REVERTIDO: Se vuelve a usar el valor fijo
                Text("Responde en < 1 hora", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF3FB)) {
                        Text(
                            "Top Mentor",
                            color = AppBlue,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF3FB)) {
                        Text(
                            "Especialista",
                            color = AppBlue,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Price + book
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray),
            color = Color.White
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                     // REVERTIDO: hourlyRate ya es String
                    Text("$ ${profile?.hourlyRate ?: "—"} /hora", color = TextPrimary)
                    Text("Precio por sesión", color = TextSecondary, fontSize = 12.sp)
                }
                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = AppBlue),
                    shape = RoundedCornerShape(10.dp),
                    enabled = profile != null
                ) { Text("Reservar Sesión", color = Color.White) }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tabs
        var tab by remember { mutableStateOf(0) }
        Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFFEFF1F5)) {
            Row(
                Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabPill("Acerca", tab == 0, { tab = 0 }, Modifier.weight(1f))
                TabPill("Reseñas", tab == 1, { tab = 1 }, Modifier.weight(1f))
                TabPill("Horario", tab == 2, { tab = 2 }, Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(12.dp))

        // Contenido por pestaña
        when (tab) {
            0 -> { // Acerca
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    color = Color.White
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Descripción", fontWeight = FontWeight.Medium, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Text(profile?.aboutMe ?: profile?.bio ?: "Sin descripción", color = TextPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("Materias que enseña", fontWeight = FontWeight.Medium, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))

                        // REVERTIDO: Se vuelve a usar split para procesar el String
                        SubjectChips(
                            subjects = profile?.subjects
                                ?.split(",")
                                ?.map { it.trim() }
                                ?.filter { it.isNotEmpty() }
                                ?: emptyList()
                        )

                    }
                }
            }
            1 -> { // REVERTIDO: Se vuelve a usar la lista de ejemplo
                val reviews = listOf(
                    ReviewUi("Carlos Ruiz", "Programación", "Excelente tutora, muy paciente y clara en sus explicaciones.", "2 días atrás"),
                    ReviewUi("Ana López", "Algoritmos", "Me ayudó mucho con los algoritmos. Súper recomendada.", "1 semana atrás")
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    reviews.forEach { ReviewItem(it) }
                }
            }
            2 -> { // REVERTIDO: Se vuelve a usar la lista de ejemplo
                val schedule = listOf(
                    "Lunes" to listOf("14:00", "15:00", "16:00", "19:00"),
                    "Martes" to listOf("15:00", "16:00", "17:00"),
                    "Miércoles" to listOf("14:00", "15:00", "18:00", "19:00"),
                    "Jueves" to listOf("16:00", "17:00", "20:00"),
                    "Viernes" to listOf("15:00", "16:00", "17:00")
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    schedule.forEach { (day, times) ->
                        DayBlock(day, times)
                    }
                }
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) Color.White else Color.Transparent
    val fg = if (selected) TextPrimary else TextSecondary

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = bg,
        border = if (selected) BorderStroke(1.dp, Color.Transparent) else null,
        modifier = modifier
    ) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(text, color = fg)
        }
    }
}

@Composable
private fun TagLight(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class ReviewUi(
    val author: String,
    val subject: String,
    val text: String,
    val whenText: String
)

@Composable
private fun ReviewItem(r: ReviewUi) {
    Surface(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderGray), color = Color.White) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color(0xFFF1F5F9)) {
                    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Text(r.author.first().uppercase(), color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.author, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Row { TagLight(r.subject) }
                }
                Row { repeat(5) { Text("★", color = Color(0xFFFFC107)) } }
            }
            Spacer(Modifier.height(8.dp))
            Text(r.text, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(r.whenText, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DayBlock(day: String, times: List<String>) {
    Surface(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderGray), color = Color.White) {
        Column(Modifier.padding(16.dp)) {
            Text(day, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                times.forEach { TimeSlotChip(it) }
            }
        }
    }
}

@Composable
private fun TimeSlotChip(text: String) {
    Surface(shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, BorderGray), color = Color.White) {
        Text(text, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
    }
}
