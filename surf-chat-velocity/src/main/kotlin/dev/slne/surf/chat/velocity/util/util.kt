package dev.slne.surf.chat.velocity.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.velocitypowered.api.proxy.Player

fun PacketWrapper<*>.send(player: Player) =
    PacketEvents.getAPI().playerManager.sendPacket(player, this)

fun Player.sendPacket(packet: PacketWrapper<*>) = packet.send(this)

fun PacketWrapper<*>.sendSilent(player: Player) =
    PacketEvents.getAPI().playerManager.sendPacketSilently(player, this)

fun Player.sendPacketSilent(packet: PacketWrapper<*>) = packet.sendSilent(this)