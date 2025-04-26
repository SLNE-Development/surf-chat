package dev.slne.surf.chat.velocity

import com.google.gson.Gson
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.slne.surf.chat.velocity.service.VelocityMessagingReceiverService
import org.slf4j.Logger
import java.nio.file.Path

@Plugin (
    id = "surf-chat-velocity",
    version = "1.21.4-1.0.0-SNAPSHOT",
    name = "surf-chat-velocity",
    description = "Velocity instance of the surf chat plugin",
    authors = ["SLNE Development"],
    url = "https://server.castcrafter.de/",
    dependencies = []
)
class SurfChatVelocity {
    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var proxy: ProxyServer

    @Inject
    lateinit var pluginContainer: PluginContainer

    @Inject
    @DataDirectory
    lateinit var dataFolder: Path

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        proxy.channelRegistrar.register(messageChannel)
        proxy.eventManager.register(this, VelocityMessagingReceiverService())
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent) {

    }

    companion object {
        val INSTANCE = SurfChatVelocity()
    }
}

val messageChannel get() = MinecraftChannelIdentifier.from("surf-chat:messaging")
val plugin get() = SurfChatVelocity.INSTANCE
val gson get() = Gson()