package com.example.machteacher.ui.chat

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machteacher.model.ChatMessage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    mentorName: String,
    conversationId: Long,
    userId: Long,
    onBack: () -> Unit,
    onConversationOpened: (Long, Long) -> Unit,
    onQrClick: () -> Unit,
    qrResult: String?,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var highlightInput by remember { mutableStateOf(false) }


    LaunchedEffect(qrResult) {
        if (!qrResult.isNullOrBlank()) {

            viewModel.onInputChange(qrResult)

            // Activar highlight
            highlightInput = true

            // Vibración corta
            vibrator?.let { vib ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(
                        VibrationEffect.createOneShot(
                            80,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(80)
                }
            }
        }
    }

    // Apagar el highlight después de un pequeño tiempo
    LaunchedEffect(highlightInput) {
        if (highlightInput) {
            delay(600)
            highlightInput = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.registerConversationOpenedCallback(onConversationOpened)
    }

    LaunchedEffect(conversationId, userId) {
        if (userId != 0L && conversationId != 0L) {
            viewModel.notifyConversationOpened(conversationId, userId)
        }
        viewModel.refreshMessages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mentorName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (state.isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.messages,
                        key = { it.id }
                    ) { msg ->
                        MessageBubble(message = msg)
                    }
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            ChatInputRow(
                text = state.input,
                onTextChange = viewModel::onInputChange,
                onSend = { viewModel.sendCurrentMessage() },
                enabled = !state.isSending,
                onQrClick = onQrClick,
                highlight = highlightInput
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val bg = if (message.mine) Color(0xFF2563EB) else Color(0xFFE5E7EB)
    val txtColor = if (message.mine) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.mine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(message.text, color = txtColor)
        }
    }
}

@Composable
private fun ChatInputRow(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    onQrClick: () -> Unit,
    highlight: Boolean
) {
    val canSend = enabled && text.isNotBlank()

    val baseColor = MaterialTheme.colorScheme.surface
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)

    val containerColor by animateColorAsState(
        targetValue = if (highlight) highlightColor else baseColor,
        label = "inputHighlight"
    )

    Surface(
        tonalElevation = 4.dp,
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje…") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.width(8.dp))


            if (text.isBlank()) {
                IconButton(
                    onClick = onQrClick,
                    enabled = enabled,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF111827))
                ) {
                    Icon(
                        Icons.Filled.QrCode,
                        contentDescription = "Escanear código QR",
                        tint = Color.White
                    )
                }
            } else {
                IconButton(
                    onClick = onSend,
                    enabled = canSend,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF111827))
                ) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "Enviar mensaje",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
