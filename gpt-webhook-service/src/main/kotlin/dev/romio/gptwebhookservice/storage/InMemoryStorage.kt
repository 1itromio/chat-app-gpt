package dev.romio.gptwebhookservice.storage

import dev.romio.gptwebhookservice.config.Config
import dev.romio.gptwebhookservice.model.Conversation
import dev.romio.gptwebhookservice.util.EvictingQueue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryStorage(private val config: Config) : Storage {

    private val conversationStorage = hashMapOf<String, EvictingQueue<Conversation>>()
    private val userIds = mutableSetOf(config.defaultUserId)

    private val lock = Mutex()

    override suspend fun getConversation(conversationKey: String): List<Conversation> = lock.withLock {
        return conversationStorage[conversationKey]?.toList() ?: emptyList()
    }

    override suspend fun saveConversation(conversationKey: String, conversation: Conversation) = lock.withLock {
        val evictingQueue = conversationStorage.getOrPut(conversationKey) {
            EvictingQueue(config.maxConversationSize)
        }
        evictingQueue.add(conversation)
    }

    override suspend fun isValidUser(userId: String): Boolean {
        return userIds.contains(userId)
    }

    override suspend fun clearConversation(conversationKey: String): Unit = lock.withLock {
        conversationStorage[conversationKey]?.clear()
    }

    override suspend fun addUsers(userIds: List<String>) {
        this.userIds.addAll(userIds)
    }
}
