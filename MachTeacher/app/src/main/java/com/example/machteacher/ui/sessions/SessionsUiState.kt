package com.example.machteacher.ui.sessions

import com.example.machteacher.model.Session

data class SessionsUiState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null // Para gestionar errores
)
