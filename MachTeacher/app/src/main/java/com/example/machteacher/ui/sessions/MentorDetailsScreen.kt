package com.example.machteacher.ui.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MentorDetailsScreen(
    userId: Long,
    vm: MentorDetailsViewModel = hiltViewModel(),
    onDone: () -> Unit,
    onBack: () -> Unit = {}
) {
    val state by vm.state.collectAsState()

    // Carga del perfil al entrar
    LaunchedEffect(userId) { vm.loadMentor(userId) }

    // Estados del formulario (saveable para rotaciones / process death)
    var subjectsText by rememberSaveable { mutableStateOf("") }
    var hourlyRate   by rememberSaveable { mutableStateOf("") }
    var teachingExp  by rememberSaveable { mutableStateOf("") }
    var aboutMe      by rememberSaveable { mutableStateOf("") }
    var references   by rememberSaveable { mutableStateOf("") }
    var university   by rememberSaveable { mutableStateOf("") }
    var career       by rememberSaveable { mutableStateOf("") }
    var semester     by rememberSaveable { mutableStateOf("") }

    // Prellenar una sola vez por campo (no pisar lo que ya se está editando)
    LaunchedEffect(state.profile) {
        state.profile?.let { p ->
            if (subjectsText.isBlank()) subjectsText = (p.subjects ?: emptyList()).joinToString(", ")
            if (hourlyRate.isBlank())   hourlyRate   = p.hourlyRate.orEmpty()
            if (teachingExp.isBlank())  teachingExp  = p.teachingExperience.orEmpty()
            if (aboutMe.isBlank())      aboutMe      = p.aboutMe.orEmpty()
            if (references.isBlank())   references   = p.references.orEmpty()
            if (university.isBlank())   university   = p.university.orEmpty()
            if (career.isBlank())       career       = p.career.orEmpty()
            if (semester.isBlank())     semester     = p.semester.orEmpty()
        }
    }

    // Navegación al completar guardado
    LaunchedEffect(state.done) {
        if (state.done) onDone()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        Text("Información del Mentor", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        LabeledField(
            label = "Materias (coma separada)",
            value = subjectsText,
            onValueChange = { subjectsText = it }
        )
        LabeledField(
            label = "Tarifa por hora",
            value = hourlyRate,
            onValueChange = { hourlyRate = it },
            keyboardType = KeyboardType.Decimal
        )
        LabeledField(
            label = "Experiencia docente",
            value = teachingExp,
            onValueChange = { teachingExp = it }
        )
        LabeledField(
            label = "Presentación personal",
            value = aboutMe,
            onValueChange = { aboutMe = it }
        )
        LabeledField(
            label = "Referencias",
            value = references,
            onValueChange = { references = it }
        )

        // Opcional: sincronizar también con User
        LabeledField(
            label = "Universidad (opcional)",
            value = university,
            onValueChange = { university = it }
        )
        LabeledField(
            label = "Carrera (opcional)",
            value = career,
            onValueChange = { career = it }
        )
        LabeledField(
            label = "Semestre (opcional)",
            value = semester,
            onValueChange = { semester = it },
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.loading != true,
            onClick = {
                val subjects = subjectsText
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                vm.saveMentor(
                    userId = userId,
                    subjects = subjects,
                    hourlyRate = hourlyRate.ifBlank { null }, // backend espera String
                    teachingExp = teachingExp.ifBlank { null },
                    aboutMe = aboutMe.ifBlank { null },
                    references = references.ifBlank { null },
                    university = university.ifBlank { null },
                    career = career.ifBlank { null },
                    semester = semester.ifBlank { null }
                )
            }
        ) {
            Text(if (state.loading == true) "Guardando..." else "Guardar Perfil de Mentor")
        }

        TextButton(onClick = onBack) { Text("Atrás") }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  Helper de entrada rotulado                                                */
/* -------------------------------------------------------------------------- */
@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        Spacer(Modifier.height(10.dp))
    }
}
