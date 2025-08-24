package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.channel.Channel
import java.util.*

/**
 * Represents a user in the chat system.
 *
 * @property name The name of the user.
 * @property uuid The unique identifier of the user.
 */
interface User {
    val name: String
    val uuid: UUID

    /**
     * Checks if the user has a specific permission.
     *
     * @param permission The permission to check.
     * @return `true` if the user has the specified permission, otherwise `false`.
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Provides a configurable interface for the user.
     *
     * @return An instance of `ConfigurableUser` to manage user settings.
     */
    fun configure(): ConfigurableUser

    /**
     * Retrieves the user's membership in a specific channel.
     *
     * @param channel The channel to check membership for.
     * @return An instance of `ChannelMember` if the user is a member of the channel, otherwise `null`.
     */
    fun channelMember(channel: Channel): ChannelMember?
}