package dev.slne.surf.chat.server

import dev.slne.surf.chat.ChatApplication
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.cloud.api.server.plugin.bootstrap.BootstrapContext
import dev.slne.surf.cloud.api.server.plugin.bootstrap.StandalonePluginBootstrap

class ServerBootstrap : StandalonePluginBootstrap {
    override suspend fun bootstrap(context: BootstrapContext) {
        ChatContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(ChatApplication::class)
    }
}