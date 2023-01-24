package dev.romio.gptwebhookservice.storage

import dev.romio.gptwebhookservice.model.Conversation

interface Storage {
    suspend fun getConversation(userId: String): List<Conversation>
    suspend fun saveConversation(userId: String, conversation: Conversation)
}