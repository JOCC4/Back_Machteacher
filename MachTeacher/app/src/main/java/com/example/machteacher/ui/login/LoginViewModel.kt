package com.example.machteacher.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.dto.AuthResponse
import com.example.machteacher.dto.LoginRequest
import com.example.machteacher.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class LoginState(
    val loading: Boolean = false,
    val data: AuthResponse? = null,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        if (_state.value.loading) return
        _state.value = LoginState(loading = true)

        viewModelScope.launch {
            try {
                val res = repo.login(LoginRequest(email = email, password = password))
                _state.value = LoginState(loading = false, data = res)
            } catch (e: Exception) {
                val msg = when (e) {
                    is HttpException -> when (e.code()) {

                        401, 403 -> "Correo o contraseña incorrectos"
                        else -> "Error: HTTP ${e.code()}"
                    }
                    is IOException -> "Sin conexión. Revisa tu internet."
                    else -> e.message ?: "Error desconocido"
                }
                _state.value = LoginState(loading = false, error = msg)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
