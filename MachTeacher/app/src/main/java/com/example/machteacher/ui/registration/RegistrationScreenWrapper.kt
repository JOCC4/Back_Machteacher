package com.example.machteacher.ui.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.machteacher.navigation.Routes

@Composable
fun RegistrationScreenWrapper(
    navController: NavController,
    vm: RegistrationViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) { state.error?.let { snackbarHostState.showSnackbar(it) } }

    // Al terminar el registro (STUDENT o MENTOR) → Login
    LaunchedEffect(state.done) {
        if (state.done) {
            navController.navigate(Routes.Login.build(showSuccessMessage = true)) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            RegistrationScreen(
                onSubmit = { _, email, name, phone, city, country, password, university, career, semester ->

                    vm.registerStudent(
                        email = email,
                        fullName = name,
                        phone = phone,
                        city = city,
                        country = country,
                        password = password,
                        university = university,
                        career = career,
                        semester = semester
                    )
                },
                onSubmitMentor = { _, email, name, phone, city, country, password, university, career, semester,
                                   // Campos extra de mentor
                                   subjects, hourlyRate, teachingExperience, aboutMe, references ->

                    vm.registerMentor(
                        email = email,
                        fullName = name,
                        phone = phone,
                        city = city,
                        country = country,
                        password = password,
                        university = university,
                        career = career,
                        semester = semester,
                        subjects = subjects,
                        hourlyRate = hourlyRate,
                        teachingExperience = teachingExperience,
                        aboutMe = aboutMe,
                        references = references
                    )
                }
            )
        }
    }
}
