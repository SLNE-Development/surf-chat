package dev.slne.surf.chat.paper.util

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.CommandMinecart
import org.bukkit.event.player.PlayerEvent

fun ChannelMember.player() = Bukkit.getPlayer(this.uuid)
fun Audience.isConsole() = this is ConsoleCommandSender

fun Audience.name() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    else -> "Error"
}

fun CommandSender.realName() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    is BlockCommandSender -> "Block"
    is CommandMinecart -> "CommandBlockMinecart"
    else -> "Error"
}

val CloudPlayer.bukkitPlayer get() = Bukkit.getPlayer(this.uuid)

val Player.cloudPlayer
    get() = CloudPlayer[this.uniqueId] ?: error("CloudPlayer not found for ${this.uniqueId}")

fun OfflineCloudPlayer.channelMember(channel: Channel) =
    channel.members.firstOrNull { it.uuid == this.uuid }

val PlayerEvent.cloudPlayer
    get() = this.player.toCloudPlayer()
        ?: error("CloudPlayer not found for ${this.player.uniqueId}")

val PlayerEvent.offlineCloudPlayer get() = OfflineCloudPlayer[this.player.uniqueId]


fun ChannelMember.sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }

fun CloudPlayer.hasPlatformPermission(permission: String) =
    Bukkit.getPlayer(this.uuid)?.hasPermission(permission) == true