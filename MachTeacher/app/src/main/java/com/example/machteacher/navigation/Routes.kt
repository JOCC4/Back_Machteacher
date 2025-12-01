// app/src/main/java/com/example/machteacher/navigation/Routes.kt
package com.example.machteacher.navigation

import android.net.Uri

sealed class Routes(val route: String) {

    // ===== RUTAS PRINCIPALES =====
    object Home : Routes("home")
    object Search : Routes("search")
    object Sessions : Routes("sessions")
    object Messages : Routes("messages")
    object Profile : Routes("profile")
    object Registration : Routes("registration")
    object Filters : Routes("filters")

    // 🔴 NUEVO: lista de SOS para mentores
    object SosList : Routes("sos_list")

    // ===== PERFIL DEL MENTOR =====
    object MentorProfile : Routes("mentor_profile/{userId}") {
        fun build(userId: Long) = "mentor_profile/$userId"
    }

    // ===== BOOKING (RESERVAR MENTOR) =====
    object Booking : Routes("booking/{mentorId}") {
        fun build(mentorId: Long) = "booking/$mentorId"
    }

    // ===== LOGIN =====
    object Login : Routes("login") {
        const val ARG_SHOW_SUCCESS_MESSAGE = "showSuccessMessage"

        // login?showSuccessMessage={showSuccessMessage}
        val routeWithArgs =
            "$route?$ARG_SHOW_SUCCESS_MESSAGE={$ARG_SHOW_SUCCESS_MESSAGE}"

        fun build(showSuccessMessage: Boolean) =
            "$route?$ARG_SHOW_SUCCESS_MESSAGE=$showSuccessMessage"
    }

    // ===== DETALLE DEL MENTOR =====
    object MentorDetails : Routes("mentorDetails/{userId}") {
        fun build(userId: Long) = "mentorDetails/$userId"
    }

    // ===== WIZARD DEL MENTOR =====
    object MentorWizard : Routes("mentor_wizard")

    // ===== DETALLE DE SESIÓN =====
    object SessionDetail : Routes("session_detail/{id}") {
        fun build(id: Long) = "session_detail/$id"
    }

    // ===== CHAT =====
    object Conversation : Routes("chat/{conversationId}/{mentorName}") {

        const val ARG_CONVERSATION_ID = "conversationId"
        const val ARG_MENTOR_NAME = "mentorName"

        fun build(conversationId: Long, mentorName: String): String {
            val encoded = Uri.encode(mentorName)
            return "chat/$conversationId/$encoded"
        }
    }
}
