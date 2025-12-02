package com.example.machteacher.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.Normalizer

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    fun String.normalize(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
            .trim()


    val filteredResults = remember(state.query, state.results) {
        val q = state.query.orEmpty().normalize()
        if (q.isBlank()) {
            state.results
        } else {
            state.results.filter { mentor ->
                val nameMatches = mentor.name.normalize().contains(q)
                val subjectMatches = mentor.subjects.any { subject ->
                    subject.normalize().contains(q)
                }
                nameMatches || subjectMatches
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onQueryChanged(it) },
            label = { Text("Buscar por nombre o materia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { viewModel.searchMentors() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Buscar") }

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (filteredResults.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin resultados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredResults) { mentor ->

                        MentorRow(
                            name = mentor.name,
                            career = mentor.degree,
                            subjects = mentor.subjects.joinToString(", "),
                            hourly = mentor.pricePerHour,
                            enabled = true,
                            onClick = {
                                navController.navigate("booking/${mentor.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MentorRow(
    name: String,
    career: String,
    subjects: String,
    hourly: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (career.isNotBlank()) {
                Text(career, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (subjects.isNotBlank()) {
                Text("Materias: $subjects", style = MaterialTheme.typography.bodyMedium)
            }
            if (hourly.isNotBlank()) {
                Text("Tarifa: $hourly", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            if (!enabled) {
                Text(
                    "ID de mentor no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
