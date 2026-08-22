package org.rookieand.autoplant.api

import org.rookieand.autoplant.service.PlayerCountService
import java.util.UUID

class AutoplantApiProvider(
    private val playerCountService: PlayerCountService
) : AutoplantApi {
    override fun getCount(uuid: UUID): Int = playerCountService.getCount(uuid)
    override fun setCount(uuid: UUID, count: Int) = playerCountService.setCount(uuid, count)
    override fun addCount(uuid: UUID, count: Int) = playerCountService.addCount(uuid, count)
    override fun deleteCount(uuid: UUID) = playerCountService.deleteCount(uuid)
}
