package com.example.machteacher.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.repository.MessagingRepository
import com.example.machteacher.storage.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val dataStore: AppDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: Long =
        checkNotNull(savedStateHandle["conversationId"]) {
            "conversationId no encontrado en SavedStateHandle"
        }

    private var onConversationOpenedCallback: ((Long, Long) -> Unit)? = null

    fun registerConversationOpenedCallback(cb: (Long, Long) -> Unit) {
        onConversationOpenedCallback = cb
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // 🔵 Llamado explícito desde ChatScreen
    fun notifyConversationOpened(convId: Long, userId: Long) {
        onConversationOpenedCallback?.invoke(convId, userId)
    }

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = dataStore.getUserId()
                    ?: throw IllegalStateException("Usuario no logeado")

                val msgs = messagingRepository.getMessages(
                    conversationId = conversationId,
                    userId = userId
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        conversationId = conversationId,
                        messages = msgs
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun refreshMessages() {
        loadMessages()
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(input = text) }
    }

    fun sendCurrentMessage() {
        val text = uiState.value.input.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            try {
                val userId = dataStore.getUserId()
                    ?: throw IllegalStateException("Usuario no logeado")

                _uiState.update { it.copy(isSending = true) }

                val msg = messagingRepository.sendMessage(
                    conversationId = conversationId,
                    senderId = userId,
                    body = text
                )

                _uiState.update {
                    it.copy(
                        isSending = false,
                        input = "",
                        messages = it.messages + msg
                    )
                }

                loadMessages()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSending = false, error = e.message)
                }
            }
        }
    }
}
