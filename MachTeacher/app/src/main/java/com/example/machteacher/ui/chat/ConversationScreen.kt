package com.example.machteacher.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)
private val GrayBorder = Color(0xFFE6E6E6)
private val BubbleMe = Color(0xFF1E49FF)
private val BubbleOther = Color(0xFFF2F4F7)

data class MessageUi(
    val id: String,
    val fromMe: Boolean,
    val text: String,
    val time: String
)

@Composable
fun ConversationScreen(
    // Encabezado
    name: String = "María González",
    subject: String = "Programación",
    initials: String = "M",

    // Lista real de mensajes (desde VM/back)
    messages: List<MessageUi> = emptyList(),

    // Callback de envío
    onSend: (String) -> Unit = {},

    // Navegación atrás
    onBack: () -> Unit = {},

    // ✅ Nuevo: callback que se ejecuta al abrir la conversación
    // Aquí desde arriba puedes llamar:
    // { messagesViewModel.onConversationOpened(conversationId, userId) }
    onOpened: () -> Unit = {}
) {
    var input by remember { mutableStateOf("") }

    // ✅ Se ejecuta una sola vez cuando se entra a esta pantalla
    LaunchedEffect(Unit) {
        onOpened()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        // HEADER
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
            }
            Spacer(Modifier.width(4.dp))

            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(
                    text = "En línea • $subject",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { /* TODO: llamada */ }) {
                Icon(Icons.Outlined.Call, contentDescription = "Llamar")
            }
            IconButton(onClick = { /* TODO: video */ }) {
                Icon(Icons.Outlined.Videocam, contentDescription = "Videollamada")
            }
            IconButton(onClick = { /* TODO: más opciones */ }) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones")
            }
        }

        Divider(color = GrayBorder)

        // LISTA DE MENSAJES
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(msg = msg)
            }
        }

        // INPUT
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Escribe un mensaje...") },
                singleLine = false,
                minLines = 1,
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )

            Spacer(Modifier.width(12.dp))

            FilledIconButton(
                onClick = {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        onSend(text)
                        input = ""
                    }
                },
                enabled = input.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Send, contentDescription = "Enviar")
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: MessageUi) {
    val bg = if (msg.fromMe) BubbleMe else BubbleOther
    val content = if (msg.fromMe) Color.White else TextPrimary
    val shape =
        if (msg.fromMe)
            RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = 12.dp,
                bottomEnd = 6.dp
            )
        else
            RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = 6.dp,
                bottomEnd = 12.dp
            )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.fromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (msg.fromMe) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = bg,
                shape = shape,
                border = if (msg.fromMe) null else BorderStroke(1.dp, Color(0xFFE6E6E6))
            ) {
                Text(
                    text = msg.text,
                    color = content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(msg.time, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
