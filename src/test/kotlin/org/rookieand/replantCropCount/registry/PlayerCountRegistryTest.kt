package org.rookieand.replantCropCount.registry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class PlayerCountRegistryTest {

    private val registry = PlayerCountRegistry()
    private val uuid = UUID.randomUUID()

    @Test
    fun `put 은 dirty 가 아닌 엔트리를 생성한다`() {
        registry.put(uuid, 10)

        val state = registry.get(uuid)
        assertEquals(10, state?.count)
        assertFalse(state!!.dirty)
        assertTrue(registry.contains(uuid))
    }

    @Test
    fun `add 는 수량을 증가시키고 dirty 로 표시한다`() {
        registry.put(uuid, 10)

        registry.add(uuid, 5)

        val state = registry.get(uuid)
        assertEquals(15, state?.count)
        assertTrue(state!!.dirty)
    }

    @Test
    fun `add 에 음수를 넣으면 수량이 감소한다`() {
        registry.put(uuid, 10)

        registry.add(uuid, -3)

        assertEquals(7, registry.get(uuid)?.count)
    }

    @Test
    fun `set 은 값을 교체하고 dirty 로 표시한다`() {
        registry.put(uuid, 10)

        registry.set(uuid, 2)

        val state = registry.get(uuid)
        assertEquals(2, state?.count)
        assertTrue(state!!.dirty)
    }

    @Test
    fun `등록되지 않은 UUID 를 변경해도 엔트리가 생성되지 않는다`() {
        registry.add(uuid, 5)
        registry.set(uuid, 3)

        assertNull(registry.get(uuid))
        assertFalse(registry.contains(uuid))
    }

    @Test
    fun `remove 는 엔트리를 반환하고 제거한다`() {
        registry.put(uuid, 10)

        val removed = registry.remove(uuid)

        assertEquals(10, removed?.count)
        assertNull(registry.get(uuid))
    }

    @Test
    fun `forEach 는 모든 엔트리를 순회한다`() {
        val other = UUID.randomUUID()
        registry.put(uuid, 1)
        registry.put(other, 2)

        val visited = mutableMapOf<UUID, Int>()
        registry.forEach { key, state -> visited[key] = state.count }

        assertEquals(mapOf(uuid to 1, other to 2), visited)
    }
}
