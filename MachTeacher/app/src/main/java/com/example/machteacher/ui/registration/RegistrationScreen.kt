package com.example.machteacher.ui.registration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.machteacher.ui.design.ExposedSelect
import com.example.machteacher.ui.design.LabeledArea
import com.example.machteacher.ui.design.LabeledField
import com.example.machteacher.ui.design.PasswordField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onSubmit: (
        roleApi: String,
        email: String,
        name: String,
        phone: String,
        city: String,
        country: String,
        password: String,
        university: String?,
        career: String?,
        semester: String?
    ) -> Unit,

    onSubmitMentor: (
        roleApi: String, email: String, name: String, phone: String, city: String, country: String, 
        password: String, university: String?, career: String?, semester: String?,
        subjects: String?, hourlyRate: String?, teachingExperience: String?, 
        aboutMe: String?, references: String?
    ) -> Unit
) {
    /* ---- Estado ---- */
    var role by rememberSaveable { mutableStateOf("Estudiante") }
    var email by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPwd by rememberSaveable { mutableStateOf(false) }

    var university by rememberSaveable { mutableStateOf("") }
    var career by rememberSaveable { mutableStateOf("") }
    var semester by rememberSaveable { mutableStateOf("") }

    // Campos de Mentor
    val defaultSubjects = listOf(
        "Matemáticas","Física","Química","Programación","Inglés",
        "Estadística","Contabilidad","Economía","Derecho","Medicina"
    )
    var selectedSubjects by rememberSaveable { mutableStateOf(setOf<String>()) }
    var hourlyRate by rememberSaveable { mutableStateOf("") }
    var teachingExp by rememberSaveable { mutableStateOf("") }
    var aboutMe by rememberSaveable { mutableStateOf("") }
    var references by rememberSaveable { mutableStateOf("") }

    val scroll = rememberScrollState()

    /* ---- Layout ---- */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
            ) {
                HeaderBlock()

                Spacer(Modifier.height(14.dp))

                RoleCard(
                    role = role,
                    onStudent = { role = "Estudiante" },
                    onMentor = { role = "Mentor" }
                )

                Spacer(Modifier.height(16.dp))

                PersonalInfoCard(
                    role = role,
                    email = email, setEmail = { email = it },
                    name = name, setName = { name = it },
                    university = university, setUniversity = { university = it },
                    career = career, setCareer = { career = it },
                    semester = semester, setSemester = { semester = it },
                    phone = phone, setPhone = { phone = it },
                    city = city, setCity = { city = it },
                    country = country, setCountry = { country = it },
                    password = password, setPassword = { password = it },
                    showPwd = showPwd, onTogglePwd = { showPwd = !showPwd },
                    onSubmit = {
                        onSubmit(
                            "STUDENT", email.trim(), name.trim(), phone.trim(), city.trim(), 
                            country.trim(), password, university.trim().ifBlank { null },
                            career.trim().ifBlank { null }, semester.trim().ifBlank { null }
                        )
                    }
                )

                if (role == "Mentor") {
                    Spacer(Modifier.height(16.dp))
                    MentorExtraCard(
                        defaultSubjects = defaultSubjects,
                        selectedSubjects = selectedSubjects,
                        onToggleSubject = { subj ->
                            selectedSubjects =
                                if (selectedSubjects.contains(subj)) selectedSubjects - subj
                                else selectedSubjects + subj
                        },
                        hourlyRate = hourlyRate, setHourlyRate = { hourlyRate = it },
                        teachingExp = teachingExp, setTeachingExp = { teachingExp = it },
                        aboutMe = aboutMe, setAboutMe = { aboutMe = it },
                        references = references, setReferences = { references = it },
                        onSubmitMentor = {

                            onSubmitMentor(
                                "MENTOR", email.trim(), name.trim(), phone.trim(), city.trim(), 
                                country.trim(), password, university.trim().ifBlank { null },
                                career.trim().ifBlank { null }, semester.trim().ifBlank { null },
                                selectedSubjects.joinToString(",").ifBlank { null },
                                hourlyRate.trim().ifBlank { null },
                                teachingExp.trim().ifBlank { null },
                                aboutMe.trim().ifBlank { null },
                                references.trim().ifBlank { null }
                            )
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}


/* ---------------------- Secciones ---------------------- */

@Composable
private fun HeaderBlock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE9EEFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.School, contentDescription = null, tint = Color(0xFF1E49FF))
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text("¿Cómo te quieres registrar?", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RoleCard(
    role: String,
    onStudent: () -> Unit,
    onMentor: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6E8EF))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SegmentedChoice(
                title = "Estudiante",
                subtitle = "Buscar tutorías",
                selected = role == "Estudiante",
                onClick = onStudent,
                modifier = Modifier.weight(1f)
            )
            SegmentedChoice(
                title = "Mentor",
                subtitle = "Ofrecer tutorías",
                selected = role == "Mentor",
                onClick = onMentor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PersonalInfoCard(
    role: String,
    email: String, setEmail: (String) -> Unit,
    name: String, setName: (String) -> Unit,
    university: String, setUniversity: (String) -> Unit,
    career: String, setCareer: (String) -> Unit,
    semester: String, setSemester: (String) -> Unit,
    phone: String, setPhone: (String) -> Unit,
    city: String, setCity: (String) -> Unit,
    country: String, setCountry: (String) -> Unit,
    password: String, setPassword: (String) -> Unit,
    showPwd: Boolean, onTogglePwd: () -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6E8EF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Información Personal", fontWeight = FontWeight.SemiBold)

            LabeledField(
                label = "Correo Institucional",
                value = email,
                onValueChange = setEmail,
                placeholder = "ejemplo@universidad.edu.co",
                keyboardType = KeyboardType.Email
            )
            Assistive("Debe ser tu correo universitario oficial")

            LabeledField(
                label = "Nombre Completo",
                value = name,
                onValueChange = setName,
                placeholder = "Tu nombre completo"
            )

            ExposedSelect(
                label = "Universidad",
                options = listOf("Duoc UC","Universidad de Chile","Universidad Católica","USACH","Universidad de los Andes","Otra"),
                selected = university,
                onSelected = setUniversity,
                placeholder = "Selecciona tu universidad"
            )

            ExposedSelect(
                label = "Carrera",
                options = listOf("Informática","Medicina","Derecho","Economía","Psicología","Otra"),
                selected = career,
                onSelected = setCareer,
                placeholder = "Selecciona tu carrera"
            )

            ExposedSelect(
                label = "Semestre Actual",
                options = (1..12).map { "$it" },
                selected = semester,
                onSelected = setSemester,
                placeholder = "Selecciona tu semestre"
            )

            LabeledField(
                label = "Teléfono",
                value = phone,
                onValueChange = setPhone,
                placeholder = "+56 9 1234 5678",
                keyboardType = KeyboardType.Phone
            )

            ResponsiveTwoColumn(
                gap = 8.dp,
                left = {
                    LabeledField(
                        label = "Ciudad",
                        value = city,
                        onValueChange = setCity,
                        placeholder = "Santiago",
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                right = {
                    LabeledField(
                        label = "País",
                        value = country,
                        onValueChange = setCountry,
                        placeholder = "Chile",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            PasswordField(
                label = "Contraseña",
                value = password,
                onValueChange = setPassword,
                show = showPwd,
                onToggleShow = onTogglePwd
            )

            if (role == "Estudiante") {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E49FF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Crear Cuenta de Estudiante", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MentorExtraCard(
    defaultSubjects: List<String>,
    selectedSubjects: Set<String>,
    onToggleSubject: (String) -> Unit,
    hourlyRate: String, setHourlyRate: (String) -> Unit,
    teachingExp: String, setTeachingExp: (String) -> Unit,
    aboutMe: String, setAboutMe: (String) -> Unit,
    references: String, setReferences: (String) -> Unit,
    onSubmitMentor: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6E8EF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Información del Mentor", fontWeight = FontWeight.SemiBold)

            Text("Materias que puedes enseñar")
            FlowChips(
                all = defaultSubjects,
                selected = selectedSubjects,
                onToggle = onToggleSubject
            )

            LabeledField(
                label = "Tarifa por Hora (CLP)",
                value = hourlyRate,
                onValueChange = setHourlyRate,
                placeholder = "$ 9.999",
                keyboardType = KeyboardType.Number
            )

            LabeledArea(
                label = "Experiencia Docente",
                value = teachingExp,
                onValueChange = setTeachingExp,
                placeholder = "Describe tu experiencia previa enseñando o ayudando a otros estudiantes..."
            )

            LabeledArea(
                label = "Presentación Personal",
                value = aboutMe,
                onValueChange = setAboutMe,
                placeholder = "Cuéntanos sobre ti, tu metodología de enseñanza y por qué quieres ser mentor..."
            )

            LabeledArea(
                label = "Referencias de Profesores",
                value = references,
                onValueChange = setReferences,
                placeholder = "Menciona profesores (nombre, materia, contacto)..."
            )

            Button(
                onClick = onSubmitMentor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E49FF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Crear Cuenta de Mentor", color = Color.White)
            }
        }
    }
}


/* ---------------------- Helpers UI ---------------------- */

@Composable
private fun SegmentedChoice(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) Color(0xFF0B0D21) else Color.White
    val fg = if (selected) Color.White else Color(0xFF0B0D21)
    val sub = if (selected) Color(0xFFE3E6FF) else Color(0xFF6B7280)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E8EF)),
        color = bg,
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .heightIn(min = 72.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = fg, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = sub, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun Assistive(text: String) {
    Text(text, color = Color(0xFF6B7280), style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun FlowChips(
    all: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        all.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    val isSel = selected.contains(label)
                    Surface(
                        onClick = { onToggle(label) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSel) Color(0xFF1E49FF) else Color(0xFFE6E8EF)),
                        color = if (isSel) Color(0xFFEFF3FF) else Color.White,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                label,
                                color = if (isSel) Color(0xFF1E49FF) else Color(0xFF0B0D21)
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ResponsiveTwoColumn(
    gap: Dp,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < 360.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                left()
                right()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.weight(1f)) { left() }
                Box(Modifier.weight(1f)) { right() }
            }
        }
    }
}
