package dev.slne.surf.chat.server.database

import dev.slne.surf.chat.server.database.repository.IgnoreListRepository
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.task.PrePlayerJoinTask
import org.springframework.stereotype.Component

@Component
class PreLoginChatTask(
    private val ignoreListRepository: IgnoreListRepository
) : PrePlayerJoinTask {
    override suspend fun preJoin(player: OfflineCloudPlayer): PrePlayerJoinTask.Result {
        ignoreListRepository.cacheIgnorelist(player.uuid)
        return PrePlayerJoinTask.Result.ALLOWED
    }
}