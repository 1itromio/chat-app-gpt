package dev.romio.gptwebhookservice.storage

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.Conversation
import dev.romio.gptwebhookservice.util.EvictingQueue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryStorage(private val config: Config): Storage {

    private val conversationStorage = hashMapOf<String, EvictingQueue<Conversation>>()

    private val lock = Mutex()

    override suspend fun getConversation(userId: String): List<Conversation> = lock.withLock {
        return conversationStorage[userId]?.toList() ?: emptyList()
    }

    override suspend fun saveConversation(userId: String, conversation: Conversation) = lock.withLock {
        val evictingQueue = conversationStorage.getOrPut(userId) {
            EvictingQueue(config.maxConversationSize)
        }
        evictingQueue.add(conversation)
    }
}