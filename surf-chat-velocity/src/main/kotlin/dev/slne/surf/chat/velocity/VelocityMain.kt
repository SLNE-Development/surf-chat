package dev.slne.surf.chat.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer

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