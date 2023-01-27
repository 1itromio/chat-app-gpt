package dev.romio.gptwebhookservice.storage

import dev.romio.gptwebhookservice.model.Conversation

interface Storage {
    suspend fun getConversation(conversationKey: String): List<Conversation>
    suspend fun saveConversation(conversationKey: String, conversation: Conversation)
    suspend fun isValidUser(userId: String): Boolean
    suspend fun clearConversation(conversationKey: String)
}
