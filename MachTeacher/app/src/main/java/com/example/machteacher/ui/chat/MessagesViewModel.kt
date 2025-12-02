package com.example.machteacher.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.model.ConversationPreview
import com.example.machteacher.repository.MessagingRepository
import com.example.machteacher.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val conversations: List<ConversationPreview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalUnread: Int = 0
)

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {


    private var notifyUnread: ((Int) -> Unit)? = null

    private val _state = MutableStateFlow(MessagesUiState())
    val state: StateFlow<MessagesUiState> = _state.asStateFlow()


    fun registerUnreadCallback(cb: (Int) -> Unit) {
        notifyUnread = cb

        val current = _state.value.totalUnread
        if (current > 0) notifyUnread?.invoke(current)
    }


    fun loadFromSessions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                sessionRepository.refreshUpcomingSessions()
                val sessions = sessionRepository.getUpcomingSessions().first()

                if (sessions.isEmpty()) {
                    _state.update {
                        it.copy(isLoading = false, conversations = emptyList(), totalUnread = 0)
                    }
                    notifyUnread?.invoke(0)
                    return@launch
                }

                _state.update { current ->
                    val mutable = current.conversations.toMutableList()
                    val validIds = mutableSetOf<Long>()

                    sessions.forEach { session ->
                        val convId = messagingRepository.ensureConversationForSession(session.id)
                        validIds.add(convId)

                        val index = mutable.indexOfFirst { it.id == convId }
                        if (index >= 0) {
                            val old = mutable[index]
                            mutable[index] = old.copy(
                                mentorName = session.mentorName,
                                lastMessage = session.subject,
                                timeLabel = old.timeLabel,
                                unread = old.unread
                            )
                        } else {
                            mutable.add(
                                ConversationPreview(
                                    id = convId,
                                    mentorName = session.mentorName,
                                    lastMessage = session.subject,
                                    timeLabel = "",
                                    unread = 0
                                )
                            )
                        }
                    }

                    val filtered = mutable.filter { it.id in validIds }
                    val total = filtered.sumOf { it.unread }

                    notifyUnread?.invoke(total)

                    current.copy(
                        conversations = filtered,
                        isLoading = false,
                        totalUnread = total
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error cargando conversaciones"
                    )
                }
            }
        }
    }


    fun startUnreadListener(userId: Long) {
        viewModelScope.launch {
            try {
                val backendList = messagingRepository.loadUserConversations(userId)

                _state.update { current ->
                    val total = backendList.sumOf { it.unread }
                    notifyUnread?.invoke(total)

                    current.copy(
                        conversations = backendList,
                        totalUnread = total,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {

            }
        }
    }


    fun onConversationOpened(conversationId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                messagingRepository.markConversationAsRead(conversationId, userId)

                _state.update { current ->
                    val updated = current.conversations.map { conv ->
                        if (conv.id == conversationId) conv.copy(unread = 0) else conv
                    }

                    val total = updated.sumOf { it.unread }
                    notifyUnread?.invoke(total)

                    current.copy(
                        conversations = updated,
                        totalUnread = total
                    )
                }

            } catch (_: Exception) { }
        }
    }
}
