package dev.slne.surf.chat.server

import dev.slne.surf.chat.server.database.repository.IgnoreListRepository
import dev.slne.surf.cloud.api.common.event.CloudEventHandler
import dev.slne.surf.cloud.api.common.event.player.connection.CloudPlayerDisconnectFromNetworkEvent
import org.springframework.stereotype.Component

@Component
class PostLogoutChatTask(
    private val ignoreListRepository: IgnoreListRepository
) {
    @CloudEventHandler
    suspend fun onPlayerDisconnectFromNetwork(event: CloudPlayerDisconnectFromNetworkEvent) {
        ignoreListRepository.storeIgnorelist(event.player.uuid)
    }
}