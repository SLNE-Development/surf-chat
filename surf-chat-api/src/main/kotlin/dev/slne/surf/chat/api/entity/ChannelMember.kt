package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.model.ChannelRole
import java.util.*

/**
 * Represents a member of a chat channel.
 *
 * @property uuid The unique identifier of the channel member.
 * @property name The name of the channel member.
 * @property role The role of the channel member in the chat channel.
 */
interface ChannelMember {
    val uuid: UUID // The unique ID of the member
    val name: String // The name of the member
    var role: ChannelRole // The role of the member in the channel

    /**
     * Checks if the member has moderator permissions.
     *
     * @return `true` if the member has the role of OWNER or MODERATOR, otherwise `false`.
     */
    fun hasModeratorPermissions() = role == ChannelRole.OWNER || role == ChannelRole.MODERATOR
}