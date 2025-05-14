package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.api.type.ChannelStatusType
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Represents a chat channel model.
 * This interface defines the structure and behavior of a chat channel,
 * including its members, status, and various operations for managing the channel.
 */
interface ChannelModel {

    /**
     * The name of the channel.
     */
    val name: String

    /**
     * The current status of the channel (e.g., active, inactive).
     */
    var status: ChannelStatusType

    /**
     * A map of channel members and their roles within the channel.
     */
    val members: Object2ObjectMap<ChatUserModel, ChannelRoleType>

    /**
     * A set of players who are banned from the channel.
     */
    val bannedPlayers: ObjectSet<ChatUserModel>

    /**
     * A set of players who have been invited to the channel.
     */
    val invites: ObjectSet<ChatUserModel>

    /**
     * Allows a user to join the channel.
     * @param user The user joining the channel.
     * @param silent Whether the join action should be silent.
     */
    fun join(user: ChatUserModel, silent: Boolean = false)

    /**
     * Allows a user to leave the channel.
     * @param user The user leaving the channel.
     * @param silent Whether the leave action should be silent.
     */
    fun leave(user: ChatUserModel, silent: Boolean = false)

    /**
     * Checks if a user is invited to the channel.
     * @param user The user to check.
     * @return True if the user is invited, false otherwise.
     */
    fun isInvited(user: ChatUserModel): Boolean

    /**
     * Checks if a command sender is invited to the channel.
     * @param user The command sender to check.
     * @return True if the sender is invited, false otherwise.
     */
    fun isInvited(user: CommandSender): Boolean

    /**
     * Invites a user to the channel.
     * @param user The user to invite.
     */
    fun invite(user: ChatUserModel)

    /**
     * Revokes an invitation for a user.
     * @param user The user whose invitation is revoked.
     */
    fun unInvite(user: ChatUserModel)

    /**
     * Transfers ownership of the channel to another user.
     * @param user The new owner of the channel.
     */
    fun transferOwnership(user: ChatUserModel)

    /**
     * Promotes a user to a higher role in the channel.
     * @param user The user to promote.
     */
    fun promote(user: ChatUserModel)

    /**
     * Demotes a user to a lower role in the channel.
     * @param user The user to demote.
     */
    fun demote(user: ChatUserModel)

    /**
     * Kicks a user from the channel.
     * @param user The user to kick.
     */
    fun kick(user: ChatUserModel)

    /**
     * Bans a user from the channel.
     * @param user The user to ban.
     */
    fun ban(user: ChatUserModel)

    /**
     * Unbans a user from the channel.
     * @param user The user to unban.
     */
    fun unban(user: ChatUserModel)

    /**
     * Checks if a user is banned from the channel.
     * @param user The user to check.
     * @return True if the user is banned, false otherwise.
     */
    fun isBanned(user: ChatUserModel): Boolean

    /**
     * Gets the owner of the channel.
     * @return The owner of the channel.
     */
    fun getOwner(): ChatUserModel

    /**
     * Gets all members of the channel.
     * @return A set of all channel members.
     */
    fun getMembers(): ObjectSet<ChatUserModel>

    /**
     * Gets only the members of the channel (excluding moderators and owners).
     * @return A set of regular channel members.
     */
    fun getOnlyMembers(): ObjectSet<ChatUserModel>

    /**
     * Gets all moderators of the channel.
     * @return A set of channel moderators.
     */
    fun getModerators(): ObjectSet<ChatUserModel>

    /**
     * Checks if a user is the owner of the channel.
     * @param user The user to check.
     * @return True if the user is the owner, false otherwise.
     */
    fun isOwner(user: ChatUserModel): Boolean

    /**
     * Checks if a user is a moderator of the channel.
     * @param user The user to check.
     * @return True if the user is a moderator, false otherwise.
     */
    fun isModerator(user: ChatUserModel): Boolean

    /**
     * Checks if a user is a member of the channel.
     * @param user The user to check.
     * @return True if the user is a member, false otherwise.
     */
    fun isMember(user: ChatUserModel): Boolean

    /**
     * Checks if a command sender is a member of the channel.
     * @param user The command sender to check.
     * @return True if the sender is a member, false otherwise.
     */
    fun isMember(user: CommandSender): Boolean

    /**
     * Checks if a player is a member of the channel.
     * @param user The player to check.
     * @return True if the player is a member, false otherwise.
     */
    fun isMember(user: Player): Boolean
}