package dev.slne.surf.chat.velocity.listener

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.toChatUser

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: PostLoginEvent) {

    }

    @Subscribe
    fun onServerConnect(event: ServerConnectedEvent) {

    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        container.launch {
            channelService.handleDisconnect(event.player.toChatUser())
        }
    }
}