package dev.slne.surf.chat.api.entity

import java.util.*

/**
 * Interface for a configurable user in the chat system.
 *
 * Provides asynchronous methods to query and modify user settings such as pings,
 * direct messages, and invitations.
 *
 * @property uuid The unique identifier of the user.
 */
interface ConfigurableUser {
    val uuid: UUID // Unique user ID

    /**
     * Checks if pings are enabled for the user.
     * @return `true` if pings are enabled, otherwise `false`.
     */
    suspend fun pingsEnabled(): Boolean

    /**
     * Enables pings for the user.
     */
    suspend fun enablePings()

    /**
     * Disables pings for the user.
     */
    suspend fun disablePings()

    /**
     * Checks if direct messages are enabled for the user.
     * @return `true` if direct messages are enabled, otherwise `false`.
     */
    suspend fun directMessagesEnabled(): Boolean

    /**
     * Enables direct messages for the user.
     */
    suspend fun enableDirectMessages()

    /**
     * Disables direct messages for the user.
     */
    suspend fun disableDirectMessages()

    /**
     * Checks if invitations are enabled for the user.
     * @return `true` if invitations are enabled, otherwise `false`.
     */
    suspend fun invitesEnabled(): Boolean

    /**
     * Enables invitations for the user.
     */
    suspend fun enableInvites()

    /**
     * Disables invitations for the user.
     */
    suspend fun disableInvites()
}