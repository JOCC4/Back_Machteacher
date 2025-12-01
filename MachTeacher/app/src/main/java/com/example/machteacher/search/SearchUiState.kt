package com.example.machteacher.search

import com.example.machteacher.ui.search.MentorUi

data class SearchUiState(
    val query: String = "",
    val results: List<MentorUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
