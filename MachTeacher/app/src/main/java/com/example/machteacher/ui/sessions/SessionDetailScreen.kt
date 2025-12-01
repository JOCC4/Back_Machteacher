package com.example.machteacher.ui.sessions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

private val CardBorder = Color(0xFFE6E6E6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val DisabledBtn = Color(0xFF9CA3AF)
private val NoteBg = Color(0xFFFFF7D6)

@Suppress("UnusedParameter")
@Composable
fun SessionDetailScreen(
    sessionId: Long,
    onBack: () -> Unit = {},
    viewModel: SessionDetailViewModel = hiltViewModel()
) {
    val ui by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showConfirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(ui.deleted) {
        if (ui.deleted) onBack()
    }

    LaunchedEffect(ui.error) {
        ui.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        "Próxima Sesión",
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showConfirmDelete = true },
                        enabled = !ui.loading && ui.exists && !ui.deleting
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
                Divider(color = CardBorder)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            when {
                ui.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                !ui.exists -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        color = Color.White
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Sesión no encontrada", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onBack) { Text("Volver") }
                        }
                    }
                }

                else -> {
                    // ===== Card principal =====
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "Sesión Programada",
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            // Mentor + materia
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val initial = ui.mentorName.firstOrNull()?.uppercase() ?: "M"
                                    Text(initial, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        ui.mentorName.ifBlank { "Mentor" },
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    SubjectBadge(ui.subject.ifBlank { "Programación" })
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Hora / duración
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextSecondary)
                                Spacer(Modifier.width(8.dp))
                                Text("${ui.whenText} • ${ui.durationText}", color = TextSecondary)
                            }

                            Spacer(Modifier.height(6.dp))

                            // Modalidad
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Computer, contentDescription = null, tint = TextSecondary)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (ui.modeText.equals("ONLINE", true))
                                        "Sesión Online"
                                    else
                                        "Sesión Presencial",
                                    color = TextSecondary
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Nota amarilla con ícono (como el mockup)
                            Surface(
                                color = NoteBg,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Event,
                                        contentDescription = null,
                                        tint = TextPrimary.copy(alpha = 0.75f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Tu sesión comenzará en 15 minutos. Te enviaremos una notificación.",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Botón deshabilitado "Sesión no ha iniciado"
                            Button(
                                onClick = { },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = DisabledBtn,
                                    disabledContentColor = Color.White,
                                    containerColor = DisabledBtn,
                                    contentColor = Color.White
                                )
                            ) {
                                if (ui.deleting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Sesión no ha iniciado")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ===== Card de preparación =====
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Preparación", fontWeight = FontWeight.Medium, color = TextPrimary)
                            Spacer(Modifier.height(10.dp))
                            Bullet("Prepara tus preguntas y dudas específicas")
                            Bullet("Ten a mano los materiales de estudio necesarios")
                            Bullet("Verifica tu conexión a internet (para sesiones online)")
                            Bullet("Encuentra un lugar tranquilo para estudiar")
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { if (!ui.deleting) showConfirmDelete = false },
            title = { Text("Eliminar sesión") },
            text = { Text("Esta acción eliminará la sesión. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDelete = false
                        viewModel.deleteSession()
                    },
                    enabled = !ui.deleting
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDelete = false },
                    enabled = !ui.deleting
                ) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SubjectBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFEFF1F5)
    ) {
        Text(
            text,
            color = TextPrimary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun Bullet(text: String) {
    Row {
        Text("• ", color = TextPrimary)
        Text(text, color = TextPrimary)
    }
}
