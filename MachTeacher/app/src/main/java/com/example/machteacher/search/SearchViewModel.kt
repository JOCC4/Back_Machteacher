package com.example.machteacher.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.repository.ProfileRepository
import com.example.machteacher.ui.search.toMentorUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    init {

        loadMentors(page = 0, size = 20)
    }

    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun searchMentors() {
        loadMentors(page = 0, size = 20, query = _state.value.query)
    }

    fun loadMentors(page: Int, size: Int, query: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val q = query?.takeIf { it.isNotBlank() }
                val mentors = profileRepository.getMentors(page = page, size = size, query = q)
                val mentorUis = mentors.mapNotNull { it.toMentorUi() }
                _state.update { it.copy(isLoading = false, results = mentorUis) }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error al cargar mentores", e)
                _state.update { it.copy(isLoading = false, error = "No se pudieron cargar los mentores") }
            }
        }
    }

    fun clearQuery() {
        _state.update { it.copy(query = "") }
    }
}
