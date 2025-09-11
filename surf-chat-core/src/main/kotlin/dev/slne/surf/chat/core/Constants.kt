package dev.slne.surf.chat.core

/**
 * The Constants object contains several predefined constants used across the application for managing
 * permissions, communication channels, and message handling in the Surf Chat application.
 */
object Constants {
    /**
     * Constant representing the permission string used to determine if a player
     * has access to team-related chat functions within the system.
     *
     * This permission is typically used to filter users who are authorized to
     * receive or interact with specific team-based messages. For example, it is
     * checked before sending messages to a subset of players to ensure they
     * possess the appropriate access rights.
     */
    const val TEAM_PERMISSION = "surf.chat.team"

    /**
     * Represents the channel identifier for team chat communication within the Surf Chat system.
     *
     * This constant serves as the unique identifier for plugin messaging channels related to team chat,
     * enabling the synchronization and handling of team-specific messages across different servers or components.
     *
     * Its primary use includes:
     * - Registering the channel for team chat-related plugin messages.
     * - Facilitating the transmission and reception of messages specific to a team communication context.
     */
    const val CHANNEL_TEAM = "surf-chat:teamchat"

    /**
     * Represents the identifier for the channel used to synchronize general chat messages
     * across different servers or components in the system.
     *
     * This constant is used to register and handle chat-related plugin messages to ensure
     * seamless message communication across a network environment.
     */
    const val CHANNEL_CHAT = "surf-chat:chat-sync"

    /**
     * Represents the channel identifier for direct message synchronization in the chat system.
     *
     * This constant is used as the unique identifier for plugin messaging channels
     * when handling direct message events such as sending, receiving, and logging messages.
     *
     * The corresponding channel listens for events related to direct messages between users in the system.
     */
    const val CHANNEL_DM = "surf-chat:dm-sync"

    /**
     * Represents the identifier for the server request communication channel used in the plugin messaging system.
     * This channel is primarily utilized to send and handle server-related plugin messages between the proxy
     * server and backend servers.
     */
    const val CHANNEL_SERVER_REQUEST = "surf-chat:server-request"

    /**
     * Represents the channel identifier used to transmit responses from the server
     * in the plugin message communication system.
     *
     * This constant is utilized for handling server responses within
     * the plugin messaging framework, including identifying the specific channel
     * for responding back to a Bukkit or Velocity connection that has sent a request.
     *
     * Its primary use includes:
     * - Sending server response messages in a bidirectional plugin messaging system.
     * - Identifying and processing inbound messages on the `surf-chat:server-response` channel.
     */
    const val CHANNEL_SERVER_RESPONSE = "surf-chat:server-response"
}