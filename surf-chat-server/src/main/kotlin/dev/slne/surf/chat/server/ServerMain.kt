package dev.slne.surf.chat.server

import dev.slne.surf.chat.server.database.repository.DenylistActionRepository
import dev.slne.surf.chat.server.database.repository.DenylistRepository
import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin

class ServerMain(
    private val denylistRepository: DenylistRepository,
    private val denylistActionRepository: DenylistActionRepository
) : StandalonePlugin() {
    override suspend fun load() {}
    override suspend fun enable() {
        denylistActionRepository.cacheActions()
        denylistRepository.cacheDenylist()
    }

    override suspend fun disable() {
        denylistActionRepository.storeActions()
        denylistRepository.storyDenylist()
    }
}

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)