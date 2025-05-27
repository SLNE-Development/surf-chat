package dev.slne.surf.chat.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: PostLoginEvent) {

    }

    @Subscribe
    fun onServerConnect(event: ServerConnectedEvent) {

    }
}