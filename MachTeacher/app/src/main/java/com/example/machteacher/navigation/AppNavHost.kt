// app/src/main/java/com/example/machteacher/navigation/AppNavHost.kt
package com.example.machteacher.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.machteacher.ui.booking.BookingScreen
import com.example.machteacher.ui.chat.ChatScreen
import com.example.machteacher.ui.chat.MessagesScreen
import com.example.machteacher.ui.chat.MessagesViewModel
import com.example.machteacher.ui.home.HomeScreen
import com.example.machteacher.ui.login.LoginScreen
import com.example.machteacher.ui.mentorprofile.MentorProfileScreen
import com.example.machteacher.ui.profile.ProfileScreen
import com.example.machteacher.ui.registration.RegistrationScreenWrapper
import com.example.machteacher.ui.search.FiltersScreen
import com.example.machteacher.ui.search.SearchMentorsScreen
import com.example.machteacher.ui.sessions.MentorDetailsScreen
import com.example.machteacher.ui.sessions.SessionDetailScreen
import com.example.machteacher.ui.sessions.SessionsScreen
import com.example.machteacher.ui.sos.SosListScreen
import com.example.machteacher.ui.chat.QrScannerScreen   // 👈 IMPORT QR

// 🔥 IMPORTS PARA NOTIFICACIÓN DE MENSAJES
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun AppNavHost(
    navController: NavHostController,
    authId: Long?,
    authRole: String?,
    setAuth: (Long, String) -> Unit,
    clearAuth: () -> Unit,
    onUnreadMessagesChanged: (Int) -> Unit
) {
    val view = LocalView.current

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(
            route = Routes.Login.routeWithArgs,
            arguments = listOf(
                navArgument(Routes.Login.ARG_SHOW_SUCCESS_MESSAGE) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val showSuccessMessage =
                entry.arguments?.getBoolean(Routes.Login.ARG_SHOW_SUCCESS_MESSAGE) ?: false

            LoginScreen(
                showSuccessMessage = showSuccessMessage,
                onGoToRegister = { navController.navigate(Routes.Registration.route) },
                onSuccess = { _: String, userId: Long, role: String ->
                    setAuth(userId, role)
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                showSuccessMessage = false,
                onGoToRegister = { navController.navigate(Routes.Registration.route) },
                onSuccess = { _: String, userId: Long, role: String ->
                    setAuth(userId, role)
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            val uid = authId
            val rl = authRole
            if (uid == null || rl == null) {
                view.post {
                    navController.navigate(Routes.Login.routeWithArgs) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            } else {
                val messagesVm: MessagesViewModel = hiltViewModel()
                val messagesState by messagesVm.state.collectAsState()

                LaunchedEffect(uid) {
                    if (uid != 0L) messagesVm.startUnreadListener(uid)
                }

                LaunchedEffect(messagesState.totalUnread) {
                    onUnreadMessagesChanged(messagesState.totalUnread)
                }

                HomeScreen(
                    userId = uid,
                    role = rl,
                    onTapSOS = { navController.navigate(Routes.SosList.route) },
                    onViewAllSessions = { navController.navigate(Routes.Sessions.route) },
                    onOpenSession = { s ->
                        navController.navigate(Routes.SessionDetail.build(s.id.toLong()))
                    },
                    onViewMentor = { m ->
                        navController.navigate(Routes.MentorProfile.build(m.id))
                    },
                    onBookMentor = { m ->
                        navController.navigate(Routes.Booking.build(m.id))
                    }
                )
            }
        }

        composable(Routes.Registration.route) {
            RegistrationScreenWrapper(navController)
        }

        composable(Routes.Messages.route) {
            val vm: MessagesViewModel = hiltViewModel()
            val uid = authId ?: 0L

            MessagesScreen(
                userId = uid,
                onOpenChat = { cid, name ->
                    navController.navigate(Routes.Conversation.build(cid, name))
                },
                viewModel = vm,
                onUnreadMessagesChanged = onUnreadMessagesChanged
            )
        }

        // 👇 CONVERSACIÓN + QR RESULT
        composable(
            route = Routes.Conversation.route,
            arguments = listOf(
                navArgument(Routes.Conversation.ARG_CONVERSATION_ID) { type = NavType.LongType },
                navArgument(Routes.Conversation.ARG_MENTOR_NAME) { type = NavType.StringType }
            )
        ) { entry ->
            val conversationId =
                entry.arguments?.getLong(Routes.Conversation.ARG_CONVERSATION_ID) ?: 0L
            val mentorName =
                entry.arguments?.getString(Routes.Conversation.ARG_MENTOR_NAME) ?: "Chat"

            val vm: MessagesViewModel = hiltViewModel()

            // 👇 LEE RESULTADO DEL QR
            val qrResult by entry.savedStateHandle
                .getStateFlow<String?>("qr_result", null)
                .collectAsState(initial = null)

            // 👇 Limpia después de usar
            LaunchedEffect(qrResult) {
                if (qrResult != null) entry.savedStateHandle["qr_result"] = null
            }

            ChatScreen(
                mentorName = mentorName,
                conversationId = conversationId,
                userId = authId ?: 0L,
                onBack = { navController.popBackStack() },
                onConversationOpened = { convId, userId ->
                    vm.onConversationOpened(convId, userId)
                },
                onQrClick = {
                    navController.navigate("qrScanner")
                },
                qrResult = qrResult  // 👈 PASAMOS EL TEXTO AL CHAT
            )
        }

        composable(Routes.Profile.route) {
            val uid = authId
            val rl = authRole
            if (uid == null || rl == null) {
                view.post {
                    navController.navigate(Routes.Login.routeWithArgs) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            } else {
                ProfileScreen(
                    userId = uid,
                    role = rl,
                    onLogout = {
                        view.post {
                            clearAuth()
                            navController.navigate(Routes.Login.routeWithArgs) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        composable(Routes.Sessions.route) {
            SessionsScreen(
                onOpenDetail = { s ->
                    view.post {
                        navController.navigate(Routes.SessionDetail.build(s.id.toLong()))
                    }
                }
            )
        }

        composable(
            route = Routes.SessionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val sessionId = entry.arguments?.getLong("id") ?: 0L
            SessionDetailScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Search.route) {
            SearchMentorsScreen(
                navTo = { r -> view.post { navController.navigate(r) } },
                onOpenProfile = { id ->
                    view.post { navController.navigate(Routes.MentorProfile.build(id)) }
                }
            )
        }

        composable(Routes.Filters.route) {
            FiltersScreen(onApply = { navController.popBackStack() })
        }

        composable(
            route = Routes.MentorProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { entry ->
            val mentorId = entry.arguments?.getLong("userId") ?: 0L
            MentorProfileScreen(
                userId = mentorId,
                onBack = { navController.popBackStack() },
                onBook = { navController.navigate(Routes.Booking.build(mentorId)) }
            )
        }

        composable(
            route = Routes.Booking.route,
            arguments = listOf(navArgument("mentorId") { type = NavType.LongType })
        ) { entry ->
            val mentorId = entry.arguments?.getLong("mentorId") ?: 0L
            BookingScreen(
                mentorId = mentorId,
                onNavigateUp = { navController.popBackStack() },
                onBookingConfirmed = {
                    view.post {
                        navController.navigate(Routes.Sessions.route) {
                            popUpTo(Routes.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = Routes.MentorDetails.route,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { entry ->
            val uid = entry.arguments?.getLong("userId") ?: 0L
            MentorDetailsScreen(
                userId = uid,
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.SosList.route) {
            val uid = authId ?: 0L
            SosListScreen(
                mentorId = uid,
                onBack = { navController.popBackStack() }
            )
        }

        // 🆕 PANTALLA QR
        composable("qrScanner") {
            QrScannerScreen(
                onBack = { navController.popBackStack() },
                onResult = { qrText ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("qr_result", qrText)
                    navController.popBackStack()
                }
            )
        }
    }
}
