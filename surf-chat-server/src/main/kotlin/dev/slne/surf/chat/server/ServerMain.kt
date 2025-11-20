package dev.slne.surf.chat.server

import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.surfapi.core.api.util.logger

class ServerMain : StandalonePlugin() {
    private val pluginLogger = logger()

    override suspend fun load() {}
    override suspend fun enable() {}
    override suspend fun disable() {}
}

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)