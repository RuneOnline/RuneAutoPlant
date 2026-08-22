package org.rookieand.autoplant.registry

import org.rookieand.autoplant.data.PlayerCount
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerCountRegistry {

    private val counts = ConcurrentHashMap<UUID, PlayerCount>()

    fun get(uuid: UUID): PlayerCount? = counts[uuid]

    fun contains(uuid: UUID): Boolean = counts.containsKey(uuid)

    fun put(uuid: UUID, count: Int) {
        counts[uuid] = PlayerCount(count)
    }

    fun remove(uuid: UUID): PlayerCount? = counts.remove(uuid)

    fun add(uuid: UUID, delta: Int) {
        counts[uuid]?.let {
            it.count += delta
            it.dirty = true
        }
    }

    fun set(uuid: UUID, value: Int) {
        counts[uuid]?.let {
            it.count = value
            it.dirty = true
        }
    }

    fun forEach(action: (UUID, PlayerCount) -> Unit) {
        counts.forEach { (uuid, state) -> action(uuid, state) }
    }
}
