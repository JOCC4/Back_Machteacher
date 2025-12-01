package com.example.machteacher.notifications

import android.content.Context
import android.widget.Toast

fun handleNotification(context: Context, message: String) {
    Toast.makeText(context, "Notificación: $message", Toast.LENGTH_LONG).show()
}