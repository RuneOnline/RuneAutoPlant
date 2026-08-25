package org.rookieand.autoplant.service

import org.rookieand.autoplant.database.PlayerCountRepository
import org.rookieand.autoplant.registry.PlayerCountRegistry
import java.util.UUID

// 온라인 유저는 registry(캐시)로, 오프라인 유저는 repository(DB)로 처리한다.
// 오프라인 경로는 블로킹이므로 호출 측(command/api)에서 비동기 처리해야 한다.
class PlayerCountService(
    private val registry: PlayerCountRegistry,
    private val repository: PlayerCountRepository
) {
    fun getCount(uuid: UUID): Int =
        registry.get(uuid)?.count ?: repository.load(uuid) ?: 0

    // 메인 스레드(리스너)용 non-blocking 조회. 캐시에 없으면 0으로 간주해 DB를 건드리지 않는다.
    fun getOnlineCount(uuid: UUID): Int = registry.get(uuid)?.count ?: 0

    fun addCount(uuid: UUID, count: Int) {
        if (!registry.add(uuid, count)) {
            repository.save(uuid, (repository.load(uuid) ?: 0) + count)
        }
    }

    fun takeCount(uuid: UUID, count: Int) = addCount(uuid, -count)

    fun setCount(uuid: UUID, count: Int) {
        if (!registry.set(uuid, count)) {
            repository.save(uuid, count)
        }
    }

    fun deleteCount(uuid: UUID) {
        registry.remove(uuid)
        repository.delete(uuid)
    }
}
