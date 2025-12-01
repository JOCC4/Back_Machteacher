package com.example.machteacher.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState = AuthUiState()
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = loginUseCase(email, password)
            uiState = if (result.isSuccess)
                uiState.copy(isLoading = false, success = true)
            else
                uiState.copy(isLoading = false, error = "Error al iniciar sesión")
        }
    }
}