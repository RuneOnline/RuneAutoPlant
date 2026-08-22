package org.rookieand.autoplant.api

import org.bukkit.Bukkit
import java.util.UUID

// 외부 플러그인은 AutoplantApi.get() 으로 획득. 오프라인 유저 대상 호출은 비동기 스레드 권장.
interface AutoplantApi {
    fun getCount(uuid: UUID): Int
    fun setCount(uuid: UUID, count: Int)
    fun addCount(uuid: UUID, count: Int)
    fun deleteCount(uuid: UUID)

    companion object {
        fun get(): AutoplantApi? =
            Bukkit.getServicesManager().getRegistration(AutoplantApi::class.java)?.provider
    }
}
