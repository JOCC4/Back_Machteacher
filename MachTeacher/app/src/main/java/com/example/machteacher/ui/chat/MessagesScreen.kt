package com.example.machteacher.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.machteacher.model.ConversationPreview

private val BadgeRed = Color(0xFFEF4444)

@Composable
fun MessagesScreen(
    userId: Long,
    onOpenChat: (Long, String) -> Unit,
    viewModel: MessagesViewModel,


    onUnreadMessagesChanged: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(state.totalUnread) {
        onUnreadMessagesChanged(state.totalUnread)
    }


    LaunchedEffect(userId) {
        if (userId != 0L) {
            viewModel.loadFromSessions()
            viewModel.startUnreadListener(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Text(
            text = "Mensajes",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        )

        Spacer(Modifier.height(12.dp))

        // Barra de búsqueda (decorativa por ahora)
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Buscar conversaciones…",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Loader
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.conversations) { conv ->
                    ConversationRow(
                        item = conv,
                        onClick = {

                            onOpenChat(conv.id, conv.mentorName)
                        }

                    )
                }
            }
        }

        // Error
        if (state.error != null) {
            Text(
                text = state.error ?: "",
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ConversationRow(
    item: ConversationPreview,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.mentorName.take(1).uppercase(),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
            }


            if (item.unread > 0) {
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BadgeRed)
                )
            }

            Spacer(Modifier.width(10.dp))

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.mentorName,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF111827)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.lastMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1
                )
            }


            Column(horizontalAlignment = Alignment.End) {

                Text(
                    text = item.timeLabel,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(Modifier.height(8.dp))

                if (item.unread > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(BadgeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.unread.toString(),
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
