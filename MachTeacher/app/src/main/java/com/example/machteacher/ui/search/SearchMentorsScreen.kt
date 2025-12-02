package com.example.machteacher.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.search.SearchViewModel
import java.text.Normalizer

// ===== Helpers =====
private fun String.norm(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()
        .trim()

private fun canonicalSubject(raw: String): String = when (raw.norm()) {
    "mate", "matematica", "matematicas" -> "Matemáticas"
    "fisica" -> "Física"
    "quimica" -> "Química"
    "estadistica" -> "Estadística"
    "ingles" -> "Inglés"
    "programacion" -> "Programación"
    "python" -> "Python"
    else -> raw.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

// ===== Palette =====
private val BorderGray = Color(0xFFE6E6E6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val AppBlue = Color(0xFF1E49FF)
private val OnlineGreen = Color(0xFF22C55E)
private val CardShadow = 8.dp

// ================== Pantalla de búsqueda ==================
@Composable
fun SearchMentorsScreen(
    navTo: (String) -> Unit = {},
    onOpenProfile: (Long) -> Unit,
    onOpenBooking: (Long) -> Unit = { id -> navTo("booking/$id") }, // <-- nuevo (opcional)
    viewModel: SearchViewModel = hiltViewModel()
) {
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pendingRoute) {
        val route = pendingRoute ?: return@LaunchedEffect
        kotlinx.coroutines.android.awaitFrame()
        navTo(route)
        pendingRoute = null
    }

    // ===== Estado =====
    val state by viewModel.state.collectAsState()

    val mentors = state.results


    val filtered: List<MentorUi> = remember(state.query, mentors) {
        val q = state.query.orEmpty()
        if (q.isBlank()) mentors
        else {
            val qNorm = q.norm()
            val qCanon = canonicalSubject(q).norm()
            mentors.filter { m ->
                m.name.norm().contains(qNorm) ||
                        m.subjects.any { s -> canonicalSubject(s).norm() == qCanon }
            }
        }
    }

    var filtersExpanded by remember { mutableStateOf(false) }
    val availableSubjects: List<String> = remember(mentors) {
        mentors
            .flatMap { it.subjects }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .groupBy { it.norm() }
            .values
            .map { v -> canonicalSubject(v.first()) }
            .distinct()
            .sorted()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Buscar Mentores",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))


        OutlinedTextField(
            value = state.query.orEmpty(),
            onValueChange = { viewModel.onQueryChanged(it) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TextSecondary) },
            placeholder = { Text("Buscar por nombre o materia", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF5F6F8),
                unfocusedContainerColor = Color(0xFFF5F6F8)
            )
        )

        Spacer(Modifier.height(8.dp))


        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                OutlinedButton(
                    onClick = { filtersExpanded = true },
                    border = BorderStroke(1.dp, BorderGray),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Outlined.Tune, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Filtros")
                }

                DropdownMenu(
                    expanded = filtersExpanded,
                    onDismissRequest = { filtersExpanded = false }
                ) {
                    if (availableSubjects.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay materias disponibles") },
                            onClick = { filtersExpanded = false }
                        )
                    } else {
                        availableSubjects.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    viewModel.onQueryChanged(s)
                                    viewModel.searchMentors()
                                    filtersExpanded = false
                                }
                            )
                        }
                    }
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Limpiar filtro") },
                        onClick = {
                            viewModel.clearQuery()
                            viewModel.searchMentors()
                            filtersExpanded = false
                        }
                    )
                }
            }

            Button(
                onClick = { /* TODO: acción SOS */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2D2D)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("SOS Urgente", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filtered.forEach { m ->
                    MentorCardModern(
                        m,
                        onViewProfile = { onOpenProfile(m.id) },
                        onBook = { pendingRoute = "booking/${m.id}" } // defer nav
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ===== Card =====
@Composable
private fun MentorCardModern(
    m: MentorUi,
    onViewProfile: () -> Unit,
    onBook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        shadowElevation = CardShadow,
        color = Color.White
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Avatar placeholder
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Image, null, tint = TextSecondary)
                    if (m.online) {
                        Box(
                            Modifier
                                .align(Alignment.TopStart)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(m.name, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(m.degree, color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))

                    val canon = remember(m.subjects) { m.subjects.map { canonicalSubject(it) } }
                    val firstTwo = canon.take(2)
                    val hidden = canon.drop(2)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        firstTwo.forEach { TagFilled(it) }
                        if (hidden.isNotEmpty()) TagLightSmall("+${hidden.size}")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(m.rating, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(2.dp))
                    Text("(${m.reviews})", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                m.badges.forEach { BadgeBlue(it) }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.pricePerHour, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(12.dp))
                Text(m.nextSlot, color = TextSecondary)
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onViewProfile,
                    border = BorderStroke(1.dp, BorderGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) { Text("Ver Perfil") }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = AppBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) { Text("Reservar", color = Color.White, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

// ===== Chips & Badges =====
@Composable
private fun TagFilled(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFF1F5F9)
    ) {
        Text(
            text,
            color = TextPrimary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TagLightSmall(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Text(
            text,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun BadgeBlue(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFEAF2FF)
    ) {
        Text(
            text,
            color = AppBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
