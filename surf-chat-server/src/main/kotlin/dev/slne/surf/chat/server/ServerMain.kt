package dev.slne.surf.chat.server

import dev.slne.surf.chat.server.database.repository.DenylistActionRepository
import dev.slne.surf.chat.server.database.repository.DenylistRepository
import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.surfapi.core.api.util.logger

class ServerMain(
    private val denylistRepository: DenylistRepository,
    private val denylistActionRepository: DenylistActionRepository
) : StandalonePlugin() {
    private val pluginLogger = logger()

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