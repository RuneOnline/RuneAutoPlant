package org.rookieand.replantCropCount.api

import org.rookieand.replantCropCount.service.PlayerCountService
import java.util.UUID

class ReplantCropCountApiProvider(
    private val playerCountService: PlayerCountService
) : ReplantCropCountApi {
    override fun getCount(uuid: UUID): Int = playerCountService.getCount(uuid)
    override fun setCount(uuid: UUID, count: Int) = playerCountService.setCount(uuid, count)
    override fun addCount(uuid: UUID, count: Int) = playerCountService.addCount(uuid, count)
    override fun deleteCount(uuid: UUID) = playerCountService.deleteCount(uuid)
}
