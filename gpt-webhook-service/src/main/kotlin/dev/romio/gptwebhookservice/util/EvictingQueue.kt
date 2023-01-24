package dev.romio.gptwebhookservice.util

import java.util.*

class EvictingQueue<T>(private val maxSize: Int): Collection<T> {
    private val backingQueue: Queue<T> = LinkedList()

    override val size: Int
        get() = backingQueue.size

    override fun isEmpty(): Boolean {
        return backingQueue.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return backingQueue.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return backingQueue.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return backingQueue.contains(element)
    }

    fun add(value: T) {
        if(backingQueue.size >= maxSize) {
            backingQueue.poll()
        }
        backingQueue.add(value)
    }
}