package io.rsbox.engine.model.world

import io.rsbox.engine.model.entity.Entity

class EntityList<T : Entity>(val capacity: Int) {

    private val entries = mutableMapOf<Int, T?>().apply {
        repeat(capacity) {
            this[it] = null
        }
    }

    var size: Int = 0
        private set

    fun contains(element: T): Boolean = entries.containsValue(element)

    fun forEach(action: (T) -> Unit) = entries.values.filterNotNull().forEach(action)

    operator fun get(index: Int): T? {
        return entries[index]
    }

    fun any(predicate: (T) -> Boolean): Boolean {
        entries.values.forEach {
            if(it != null && predicate(it)) {
                return true
            }
        }

        return false
    }

    fun add(element: T): Boolean {
        for(i in 1 until entries.size) {
            if(entries[i] == null) {
                entries[i] = element
                element.index = i
                size++
                return true
            }
        }

        return false
    }

    fun remove(element: T): Boolean {
        if(entries[element.index] == element) {
            entries[element.index] = null
            element.index = Int.MIN_VALUE
            size--
            return true
        }

        return false
    }

    fun remove(index: Int): T? {
        if(entries[index] != null) {
            val element = entries[index]
            entries[index] = null
            size--
            return element
        }

        return null
    }
}