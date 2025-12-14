package dev.slne.surf.chat.paper

import dev.slne.surf.chat.ChatApplication
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.paper.channel.ChannelService
import dev.slne.surf.cloud.api.common.CloudInstance
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

@Suppress("UnstableApiUsage")
class PaperBootstrap : PluginBootstrap {
    override fun bootstrap(p0: BootstrapContext) {
        ChatContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(ChatApplication::class.java, ChannelService::class.java.classLoader) {
                repeat(20) {
                    println("init paper bootstrap")
                    println("classloader: ${this.javaClass.classLoader}")
                }
            }
    }
}