package com.example.machteacher.ui.sos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.dto.SosResponseDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosListScreen(
    mentorId: Long,
    onBack: () -> Unit
) {
    val vm: SosViewModel = hiltViewModel()
    val state by vm.state.collectAsState()


    LaunchedEffect(Unit) {
        vm.loadActiveSosForMentor(mentorId)
    }


    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.clearMessages() },
            confirmButton = {
                TextButton(onClick = { vm.clearMessages() }) {
                    Text("OK")
                }
            },
            title = { Text("SOS") },
            text = { Text(state.successMessage!!) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SOS Urgentes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorBox(
                        message = state.error!!,
                        onRetry = { vm.loadActiveSosForMentor(mentorId) }
                    )
                }

                state.sosList.isEmpty() -> {
                    Text(
                        text = "No hay SOS pendientes por ahora 🎉",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.sosList) { sos ->
                            SosItemCard(
                                sos = sos,
                                onAccept = {
                                    vm.acceptSos(
                                        sosId = sos.id,
                                        mentorId = mentorId
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorBox(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(8.dp))
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun SosItemCard(
    sos: SosResponseDto,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val studentName = sos.student?.fullName ?: "Alumno desconocido"

            Text(
                text = "Alumno: $studentName",
                fontWeight = FontWeight.SemiBold
            )

            if (!sos.subject.isNullOrBlank()) Text("Materia: ${sos.subject}")
            if (!sos.message.isNullOrBlank()) Text("Mensaje: ${sos.message}")
            if (!sos.createdAt.isNullOrBlank()) Text("Creado: ${sos.createdAt}")

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onAccept) { Text("Aceptar SOS") }
            }
        }
    }
}
