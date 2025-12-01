// app/src/main/java/com/example/machteacher/ui/home/HomeScreen.kt
package com.example.machteacher.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.ui.chat.MessagesViewModel
import com.example.machteacher.ui.sos.SosViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/* ================== PALETA ================== */
private val BorderGray = Color(0xFFE6E6E6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val AppBlue = Color(0xFF1E49FF)
private val OnlineGreen = Color(0xFF22C55E)

/* ====== Modelo UI Mentor ======= */
data class HomeMentorUi(
    val id: Long,
    val name: String,
    val area: String,
    val tags: List<String>,
    val price: String,
    val nextWhen: String,
    val rating: String,
    val ratingCount: Int,
    val online: Boolean,
    val initials: String,
    val aboutMe: String? = null,
    val badges: List<String> = emptyList()
)

/* ========================================================
                    HOME SCREEN PRINCIPAL
======================================================== */
@Composable
fun HomeScreen(
    userId: Long,
    role: String,
    onTapSOS: () -> Unit = {},
    onViewAllSessions: () -> Unit = {},
    onOpenSession: (HomeSessionUi) -> Unit = {},
    onViewMentor: (HomeMentorUi) -> Unit = {},
    onBookMentor: (HomeMentorUi) -> Unit = {}
) {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    val messagesVm: MessagesViewModel = hiltViewModel()
    val sosVm: SosViewModel = hiltViewModel()
    val sosState by sosVm.state.collectAsState()

    val isStudent = role.equals("STUDENT", ignoreCase = true)
    val isMentor = role.equals("MENTOR", ignoreCase = true)

    LaunchedEffect(userId, role) {
        vm.load(currentUserId = userId, role = role)
        messagesVm.startUnreadListener(userId)

        if (isStudent) {
            sosVm.loadStudentSos(userId)
        }
        if (isMentor) {
            sosVm.loadActiveSosForMentor(userId)
        }
    }

    Box(Modifier.fillMaxSize()) {

        // ===== Contenido principal =====
        when {
            state.loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.load(currentUserId = userId, role = role) }) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                HomeScreenContent(
                    userName = state.userName.ifBlank { "Usuario" },
                    sessions = state.sessions,
                    featuredMentors = state.featured,
                    onTapSOS = {
                        if (isStudent) {
                            sosVm.sendSosAsStudent(
                                studentId = userId,
                                subject = "SOS Urgente",
                                message = "Necesito ayuda urgente ahora mismo"
                            )
                        } else if (isMentor) {
                            onTapSOS()
                        }
                    },
                    onViewAllSessions = onViewAllSessions,
                    onOpenSession = onOpenSession,
                    onViewMentor = onViewMentor,
                    onBookMentor = onBookMentor,
                    isMentor = isMentor,
                    mentorHasPendingSos = if (isMentor) sosState.hasPendingSos else false
                )
            }
        }

        // 🎉 Confeti cuando la ayuda es aceptada
        ConfettiOverlay(
            isActive = sosState.showAcceptedPopup
        )

        // 📌 Popups SOS alumno (una sola vez por evento, hasta que el VM limpie el estado)
        when {
            sosState.showAcceptedPopup && sosState.acceptedByName != null -> {
                AlertDialog(
                    onDismissRequest = {
                        sosVm.clearMessages()
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            sosVm.clearMessages()
                        }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("✅ Ayuda aceptada") },
                    text = { Text("👨‍🏫 ${sosState.acceptedByName} aceptó tu SOS.") }
                )
            }

            sosState.successMessage != null -> {
                AlertDialog(
                    onDismissRequest = { sosVm.clearMessages() },
                    confirmButton = {
                        TextButton(onClick = { sosVm.clearMessages() }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("🚨 SOS enviado") },
                    text = { Text(sosState.successMessage ?: "") }
                )
            }
        }
    }
}

/* ========================================================
                    CONTENIDO DE PANTALLA
======================================================== */
@Composable
private fun HomeScreenContent(
    userName: String,
    sessions: List<HomeSessionUi>,
    featuredMentors: List<HomeMentorUi>,
    onTapSOS: () -> Unit,
    onViewAllSessions: () -> Unit,
    onOpenSession: (HomeSessionUi) -> Unit,
    onViewMentor: (HomeMentorUi) -> Unit,
    onBookMentor: (HomeMentorUi) -> Unit,
    isMentor: Boolean,
    mentorHasPendingSos: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")

    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sosScale"
    )

    val glowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sosGlowRotation"
    )

    // Órbitas C3: dos luces en sentidos opuestos
    val orbitAngle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sosOrbit1"
    )

    val orbitAngle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sosOrbit2"
    )

    val shouldGlow = isMentor && mentorHasPendingSos
    val scale = if (shouldGlow) animatedScale else 1f

    // Color neón basado en tu UI
    val neonColor = Color(0xFFC498EC)

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        /* ===== Header ===== */
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    "¡Hola, $userName! 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text("¿Qué quieres aprender hoy?", color = TextSecondary)
            }
            IconButton(onClick = { /* TODO: Notificaciones */ }) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))

        /* ===== Card SOS ===== */
        Surface(shape = RoundedCornerShape(18.dp), color = Color.Transparent) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2F6BFF), Color(0xFF8A2EFF))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "¿Necesitas ayuda urgente?",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Encuentra un mentor disponible en menos de 1 hora",
                        color = Color.White.copy(alpha = .92f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))

                    // Botón con neón y órbitas, alineado a la izquierda
                    Box(
                        modifier = Modifier.graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                    ) {
                        if (shouldGlow) {
                            // Halo neón girando
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                Color.Transparent,
                                                neonColor.copy(alpha = 0.35f),
                                                neonColor.copy(alpha = 0.8f),
                                                neonColor.copy(alpha = 0.35f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .graphicsLayer(
                                        rotationZ = glowRotation,
                                        alpha = 0.95f
                                    )
                            )

                            // Órbitas con dos luces
                            Canvas(
                                modifier = Modifier
                                    .matchParentSize()
                            ) {
                                val w = size.width
                                val h = size.height
                                val radius = min(w, h) / 2.2f

                                // Luz 1
                                val x1 = center.x + radius * cos(Math.toRadians(orbitAngle1.toDouble())).toFloat()
                                val y1 = center.y + radius * sin(Math.toRadians(orbitAngle1.toDouble())).toFloat()
                                drawCircle(
                                    color = neonColor.copy(alpha = 0.95f),
                                    radius = 9f,
                                    center = Offset(x1, y1)
                                )

                                // Luz 2 (lado opuesto)
                                val x2 = center.x + radius * cos(Math.toRadians(orbitAngle2.toDouble())).toFloat()
                                val y2 = center.y + radius * sin(Math.toRadians(orbitAngle2.toDouble())).toFloat()
                                drawCircle(
                                    color = neonColor.copy(alpha = 0.95f),
                                    radius = 9f,
                                    center = Offset(x2, y2)
                                )

                                // Borde neón suave
                                drawRoundRect(
                                    color = neonColor.copy(alpha = 0.6f),
                                    topLeft = Offset(6f, 6f),
                                    size = Size(size.width - 12f, size.height - 12f),
                                    cornerRadius = CornerRadius(26f, 26f),
                                    style = Stroke(width = 3f)
                                )
                            }
                        }

                        Button(
                            onClick = onTapSOS,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B3B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                        ) {
                            Text("🚨 SOS Urgente", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ===== Métricas ===== */
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(Icons.Outlined.Book, "12", "Sesiones completadas", Modifier.weight(1f))
            MetricCard(Icons.Outlined.Bolt, "340", "Puntos ganados", Modifier.weight(1f))
            MetricCard(Icons.Filled.Star, "4.9", "Tu calificación", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        /* ===== Próximas Sesiones ===== */
        SectionCard(
            title = "Próximas Sesiones",
            trailing = {
                TextButton(onClick = onViewAllSessions) {
                    Text("Ver todas")
                    Icon(Icons.Outlined.ArrowForward, contentDescription = null)
                }
            }
        ) {
            if (sessions.isEmpty()) {
                Text("Sin sesiones programadas", color = TextSecondary)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    sessions.forEach { s -> SessionRow(s) { onOpenSession(s) } }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ===== Mentores Destacados ===== */
        SectionCard(title = "Mentores Destacados") {
            if (featuredMentors.isEmpty()) {
                Text("No hay mentores destacados", color = TextSecondary)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    featuredMentors.forEach { m ->
                        MentorCard(
                            m,
                            onView = { onViewMentor(m) },
                            onBook = { onBookMentor(m) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

/* ================== UI SUPPORT WIDGETS ================== */

@Composable
private fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = AppBlue)
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(label, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Medium, color = TextPrimary)
                trailing?.invoke()
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SessionRow(s: HomeSessionUi, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color(0xFFEFF1F5)) {
                Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Alarm, contentDescription = null, tint = AppBlue)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(s.subject, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text("con ${s.mentorName}", color = TextSecondary, fontSize = 13.sp)
                Text(s.whenText, color = TextSecondary, fontSize = 12.sp)
            }
            Pill(text = s.mode)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
private fun MentorCard(m: HomeMentorUi, onView: () -> Unit, onBook: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(m.initials, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (m.online) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(OnlineGreen)
                                .align(Alignment.TopEnd)
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            m.name,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107))
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "${m.rating} (${m.ratingCount})",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                    Text(m.area, color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            if (m.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    m.tags.forEach { Pill(it) }
                }
                Spacer(Modifier.height(6.dp))
            }

            if (m.badges.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    m.badges.forEach { badge ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            color = Color(0xFFEFF4FF)
                        ) {
                            Text(
                                badge,
                                color = AppBlue,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.price, color = TextPrimary)
                Spacer(Modifier.width(10.dp))
                Text("• ${m.nextWhen}", color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ver Perfil")
                }
                Button(
                    onClick = onBook,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reservar")
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String) {
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

/* 🎉 Confeti dibujado a mano, sin Lottie */
@Composable
private fun ConfettiOverlay(isActive: Boolean) {
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            show = true
            delay(1600)   // dura ~1.6s
            show = false
        } else {
            show = false
        }
    }

    if (!show) return

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val colors = listOf(
            Color(0xFFFF4B91),
            Color(0xFFFFC727),
            Color(0xFF22C55E),
            Color(0xFF38BDF8),
            Color(0xFF8B5CF6)
        )

        val random = kotlin.random.Random(1234)

        repeat(120) { i ->
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val w = 6f
            val h = 18f
            val color = colors[i % colors.size]

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(w, h),
                cornerRadius = CornerRadius(3f, 3f),
                alpha = 0.9f
            )
        }
    }
}
