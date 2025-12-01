package com.example.machteacher.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class PushService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("PushService", "Mensaje recibido: ${remoteMessage.data}")
    }

    override fun onNewToken(token: String) {
        Log.d("PushService", "Nuevo token FCM: $token")
    }
}