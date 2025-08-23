package dev.slne.surf.chat.velocity

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.velocity.command.directMessageCommand
import dev.slne.surf.chat.velocity.command.replyCommand
import dev.slne.surf.chat.velocity.handler.CrossChatListener
import dev.slne.surf.chat.velocity.handler.ServerRequestHandler
import dev.slne.surf.chat.velocity.handler.TeamchatHandler

import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        INSTANCE = this

        plugin.proxy.eventManager.register(plugin, TeamchatHandler())
        plugin.proxy.eventManager.register(plugin, ServerRequestHandler())
        plugin.proxy.channelRegistrar.register(MinecraftChannelIdentifier.from(Constants.CHANNEL_TEAM))
        plugin.proxy.channelRegistrar.register(MinecraftChannelIdentifier.from(Constants.CHANNEL_DM))
        plugin.proxy.channelRegistrar.register(MinecraftChannelIdentifier.from(Constants.CHANNEL_CHAT))
        plugin.proxy.channelRegistrar.register(MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_RESPONSE))
        plugin.proxy.channelRegistrar.register(MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_REQUEST))

        PacketEvents.getAPI().eventManager.registerListener(CrossChatListener())

        directMessageCommand()
        replyCommand()
    }

    companion object {
        lateinit var INSTANCE: VelocityMain
            private set
    }
}

val plugin get() = VelocityMain.INSTANCE
val container
    get() = plugin.proxy.pluginManager.getPlugin("surf-chat-velocity").getOrNull()
        ?: error("The providing plugin container is not available. Got the plugin ID changed?")