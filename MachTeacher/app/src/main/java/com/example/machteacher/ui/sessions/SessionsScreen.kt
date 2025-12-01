package com.example.machteacher.ui.sessions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val CardBorder = Color(0xFFE6E6E6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val DarkBtn = Color(0xFF111827)

data class SessionUi(
    val id: String,
    val title: String,
    val mentor: String,
    val whenText: String,
    val duration: String,
    val mode: String
)

@Composable
fun SessionsScreen(
    onOpenDetail: (SessionUi) -> Unit = {},
    viewModel: SessionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSessions() }

    val sessions = remember(state.sessions) {
        state.sessions.map {
            SessionUi(
                id = it.id.toString(),
                title = it.subject,
                mentor = it.mentorName,
                whenText = it.dateTime,
                duration = "1 hora",
                mode = it.modality
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Mis Sesiones",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (sessions.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes sesiones programadas.", color = TextSecondary)
            }
        } else {
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder),
                color = Color.White
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Próximas", fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(sessions) { s ->
                            SessionCard(s) { onOpenDetail(s) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(s: SessionUi, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder),
        color = Color.White
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    s.title,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                ModeBadge(s.mode)
            }
            Spacer(Modifier.height(4.dp))
            Text("con ${s.mentor}", color = TextSecondary)
            Text("${s.whenText} • ${s.duration}", color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBtn)
            ) { Text("Ver Detalles", color = Color.White) }
        }
    }
}

@Composable
private fun ModeBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFF111827),
        contentColor = Color.White
    ) {
        Text(
            text,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
