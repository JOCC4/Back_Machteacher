package com.example.machteacher.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Grade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.model.ProfileUi
import com.example.machteacher.ui.design.AppColors
import com.example.machteacher.ui.design.SectionCard

/* ============ Tabs ============ */
private enum class ProfileTab { Perfil, Notificaciones, Seguridad }

/* ============ Pantalla (conectada al ViewModel) ============ */
@Composable
fun ProfileScreen(
    userId: Long,
    role: String,
    onLogout: () -> Unit = {},
    vm: ProfileViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val view = LocalView.current


    val type = remember(role) {
        if (role.equals("MENTOR", ignoreCase = true)) "mentor" else "student"
    }


    LaunchedEffect(userId, type) {
        vm.load(role = role, userId = userId)
    }


    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    state.error?.let { err ->
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("No se pudo cargar el perfil", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Text(err)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { vm.load(role = role, userId = userId) }) { Text("Reintentar") }
        }
        return
    }


    val profile: ProfileUi = state.data ?: ProfileUi()

    var tab by rememberSaveable { mutableStateOf(ProfileTab.Perfil) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { view.post(onLogout) }) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = tab.ordinal,
            indicator = {},
            divider = {}
        ) {
            Tab(
                selected = tab == ProfileTab.Perfil,
                onClick = { tab = ProfileTab.Perfil },
                text = { Text("Perfil") }
            )
            Tab(
                selected = tab == ProfileTab.Notificaciones,
                onClick = { tab = ProfileTab.Notificaciones },
                text = { Text("Notificaciones") }
            )
            Tab(
                selected = tab == ProfileTab.Seguridad,
                onClick = { tab = ProfileTab.Seguridad },
                text = { Text("Seguridad") }
            )
        }

        Spacer(Modifier.height(16.dp))

        when (tab) {
            ProfileTab.Perfil -> PerfilTab(
                u = profile,
                onBioSubmit = { draft ->
                    vm.updateBio(role = role, userId = userId, newBio = draft ?: "")
                }
            )
            ProfileTab.Notificaciones -> NotificacionesTab()
            ProfileTab.Seguridad -> SeguridadTab()
        }

        Spacer(Modifier.height(24.dp))
    }
}

/* ============ Contenido: Perfil ============ */
@Composable
private fun PerfilTab(
    u: ProfileUi,
    onBioSubmit: (String?) -> Unit = {}
) {

    val aboutOrBio = remember(u.aboutMe, u.bio) { u.aboutMe ?: u.bio }


    var showBioDialog by remember { mutableStateOf(false) }
    var bioDraft by rememberSaveable(aboutOrBio) { mutableStateOf(aboutOrBio.orEmpty()) }

    SectionCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            // Avatar
            Surface(shape = CircleShape, color = AppColors.AvatarBg) {
                Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Person, null, tint = AppColors.TextSecondary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(u.name ?: "—", fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Text(
                    aboutOrBio ?: "—",
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Grade, null, tint = AppColors.Gold)
                    Spacer(Modifier.width(4.dp))
                    Text("—", color = AppColors.TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    BadgePill(u.role ?: "—")
                }
            }

            IconButton(onClick = {
                bioDraft = aboutOrBio.orEmpty()
                showBioDialog = true
            }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = AppColors.TextSecondary)
            }
        }
    }


    if (showBioDialog) {
        AlertDialog(
            onDismissRequest = { showBioDialog = false },
            title = { Text("Editar descripción") },
            text = {
                OutlinedTextField(
                    value = bioDraft,
                    onValueChange = { bioDraft = it },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onBioSubmit(bioDraft.ifBlank { null })
                    showBioDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showBioDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Spacer(Modifier.height(12.dp))

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            icon = Icons.Outlined.Book,
            label = "Sesiones completadas",
            value = "45",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Rounded.Grade,
            label = "Puntos ganados",
            value = "1250",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(12.dp))

    SectionCard(title = "Información Personal") {
        InfoRow("Email", u.email)
        InfoRow("Teléfono", u.phone)
        InfoRow("Universidad", u.university)
        InfoRow("Carrera", u.career)
        InfoRow("Semestre", u.semester)
        InfoRow("Ubicación", u.location)
    }
}

/* ============ Contenido: Notificaciones ============ */
@Composable
private fun NotificacionesTab() {
    SectionCard(titleIcon = Icons.Outlined.Notifications, title = "Preferencias de Notificaciones") {
        ToggleRow("Sesiones programadas", "Recordatorios de sesiones próximas", true)
        ToggleRow("Nuevos mensajes", "Notificaciones de chat", true)
        ToggleRow("Ofertas y promociones", "Descuentos y ofertas especiales", false)
        ToggleRow("Resumen semanal", "Estadísticas y progreso semanal", true)
    }
}

/* ============ Contenido: Seguridad ============ */
@Composable
private fun SeguridadTab() {
    SectionCard(title = "Seguridad") {
        SettingRow(
            title = "Cambiar contraseña",
            subtitle = "Actualiza tu contraseña regularmente para mantener tu cuenta segura",
            btnText = "Cambiar contraseña"
        )
        Divider(Modifier.padding(vertical = 10.dp), color = AppColors.Border)
        SettingRow(
            title = "Autenticación de dos factores",
            subtitle = "Agrega una capa extra de seguridad a tu cuenta",
            btnText = "Configurar 2FA"
        )
        Divider(Modifier.padding(vertical = 10.dp), color = AppColors.Border)
        SettingRow(
            title = "Sesiones activas",
            subtitle = "Administra tus dispositivos conectados",
            btnText = "Ver sesiones"
        )
    }

    Spacer(Modifier.height(14.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Zona de peligro", color = AppColors.Danger, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("Eliminar cuenta", color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
            Text(
                "Esta acción no se puede deshacer. Se eliminarán permanentemente todos tus datos.",
                color = AppColors.TextSecondary, fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { /* eliminar */ },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Danger),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Eliminar cuenta", color = Color.White) }
        }
    }
}

/* ============ Helpers UI ============ */

@Composable
private fun BadgePill(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        color = Color.White
    ) {
        Text(
            text,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun StatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Icon(icon, null, tint = AppColors.TextPrimary)
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Text(label, color = AppColors.TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value.takeUnless { it.isNullOrBlank() } ?: "—",
            style = MaterialTheme.typography.bodyMedium
        )
        Divider(Modifier.padding(top = 8.dp), color = AppColors.Border)
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, defaultChecked: Boolean) {
    var checked by rememberSaveable { mutableStateOf(defaultChecked) }
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
            Text(subtitle, color = AppColors.TextSecondary, fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun SettingRow(title: String, subtitle: String, btnText: String) {
    Column {
        Text(title, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
        Text(subtitle, color = AppColors.TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { /* acción */ },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, AppColors.Border)
        ) { Text(btnText) }
    }
}
