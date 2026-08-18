package dev.slne.surf.chat.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.chat.minestom.command.ChatCommandRegistrar
import dev.slne.surf.chat.minestom.listener.ConnectionListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-chat-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom",
        "surf-redis-minestom",
        "surf-settings-minestom"
    ]
)
class SurfChatMinestomPlugin : MinestomPlugin(SurfChatMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindCommandRegistrar<ChatCommandRegistrar>()
        bindEventRegistrar<ConnectionListener>()
    }
}
