package dev.slne.surf.chat.api.channel

import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.cloud.api.common.player.CloudPlayer

/**
 * Represents a member of a chat channel.
 *
 * @property uuid The unique identifier of the channel member.
 * @property name The name of the channel member.
 * @property role The role of the channel member in the chat channel.
 */
data class ChannelMember(
    val uuid: ChatUuid,
    val name: String,
    var role: ChannelRole
) {
    /**
     * Checks if the member has moderator permissions.
     *
     * @return `true` if the member has the role of OWNER or MODERATOR, otherwise `false`.
     */
    fun hasModeratorPermissions() = role == ChannelRole.OWNER || role == ChannelRole.MODERATOR

    val cloudPlayer
        get() = CloudPlayer.Companion[uuid]
            ?: error("CloudPlayer not found for ChannelMember $uuid")
}