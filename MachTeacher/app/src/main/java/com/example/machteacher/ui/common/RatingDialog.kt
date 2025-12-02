package com.example.machteacher.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingDialog(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String?) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (!subtitle.isNullOrBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                    Divider(Modifier.padding(vertical = 8.dp))
                }
                StarRow(
                    current = rating,
                    onChange = { rating = it }
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario (opcional)") },
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(rating, comment.ifBlank { null }) }) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun StarRow(current: Int, onChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { i ->
            IconButton(onClick = { onChange(i) }) {
                if (i <= current) {
                    Icon(Icons.Filled.Star, contentDescription = null)
                } else {
                    Icon(Icons.Outlined.Star, contentDescription = null)
                }
            }
        }
    }
}
