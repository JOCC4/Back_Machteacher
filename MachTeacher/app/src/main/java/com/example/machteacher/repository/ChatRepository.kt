package com.example.machteacher.repository

import com.example.machteacher.model.Message
import javax.inject.Inject

class ChatRepository @Inject constructor() {

    suspend fun getConversationById(id: Long): List<Message> {

        return listOf(
            Message(id = 1, sender = "Tutor", content = "Hola, ¿cómo te va?"),
            Message(id = 2, sender = "Alumno", content = "Todo bien, gracias."),
            Message(id = 3, sender = "Tutor", content = "Perfecto, sigamos con la clase.")
        )
    }
}