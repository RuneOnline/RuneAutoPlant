package org.rookieand.autoplant.storable

import org.rookieand.autoplant.database.PlayerCountRepository
import org.rookieand.autoplant.registry.PlayerCountRegistry
import java.util.UUID

// registry(캐시)와 repository(DB) 사이의 영속화 오케스트레이션. 모든 호출은 비동기 스레드에서.
class PlayerCountStorable(
    private val registry: PlayerCountRegistry,
    private val repository: PlayerCountRepository
) {
    fun load(uuid: UUID) {
        registry.put(uuid, repository.load(uuid) ?: 0)
    }

    fun unload(uuid: UUID) {
        val state = registry.remove(uuid) ?: return
        if (state.dirty) repository.save(uuid, state.count)
    }

    fun save() {
        registry.forEach { uuid, state ->
            if (!state.dirty) return@forEach
            repository.save(uuid, state.count)
            state.dirty = false
        }
    }
}
