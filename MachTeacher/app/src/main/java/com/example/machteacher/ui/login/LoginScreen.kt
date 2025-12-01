 package com.example.machteacher.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.ui.design.LabeledField
import com.example.machteacher.ui.design.PasswordField
import com.example.machteacher.ui.design.PrimaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    showSuccessMessage: Boolean,
    onSuccess: (token: String, userId: Long, role: String) -> Unit = { _, _, _ -> },
    onGoToRegister: () -> Unit = {},
    vm: LoginViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val view = LocalView.current

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            snackbarHostState.showSnackbar("¡Cuenta creada con éxito!")
        }
    }

    LaunchedEffect(state.data) {
        val data = state.data ?: return@LaunchedEffect
        view.post { onSuccess(data.token, data.id, data.role) }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            vm.clearError()
        }
    }

    var goToRegister by remember { mutableStateOf(false) }
    LaunchedEffect(goToRegister) {
        if (goToRegister) {
            delay(50)
            onGoToRegister()
            goToRegister = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0xFFF5F6F8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ======== Header Azul ========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF6E06BB)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("MachTeacher", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Tu plataforma de tutorías universitarias", color = Color.White.copy(0.8f), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ======== Card del Formulario ========
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("¡Bienvenido! 👋", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Text("Inicia sesión para continuar tu aprendizaje", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(20.dp))

                    LabeledField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "tu.correo@universidad.edu",
                        label = "Correo Institucional",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(12.dp))

                    PasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        show = passwordVisible,
                        onToggleShow = { passwordVisible = !passwordVisible }
                    )

                    Spacer(Modifier.height(6.dp))

                    TextButton(onClick = { /* TODO recuperar contraseña */ }) {
                        Text("¿Olvidaste tu contraseña?", color = Color(0xFF0D47FF), fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        onClick = {
                            if (email.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("El correo no puede estar vacío") }
                                return@PrimaryButton
                            }
                            if (password.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("La contraseña no puede estar vacía") }
                                return@PrimaryButton
                            }
                            vm.login(email.trim(), password.trim())
                        },
                        enabled = !state.loading,
                        text = if (state.loading) "Ingresando..." else "Iniciar Sesión"
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ======== Separador ========
            Row(
                modifier = Modifier.padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text("   o   ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(Modifier.height(10.dp))

            // ======== Crear Cuenta ========
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF0D47FF))
            ) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { goToRegister = true }
                ) {
                    Text("Crear Cuenta", color = Color(0xFF0D47FF))
                }
            }
        }
    }
}
