package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.api.type.ChannelStatusType
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.command.CommandSender

interface ChannelModel {
    val name: String
    val status: ChannelStatusType

    val members: Object2ObjectMap<ChatUserModel, ChannelRoleType>
    val bannedPlayers: ObjectSet<ChatUserModel>
    val invites: ObjectSet<ChatUserModel>

    fun isInvited(user: ChatUserModel): Boolean
    fun isInvited(user: CommandSender): Boolean
    fun invite(user: ChatUserModel)
    fun unInvite(user: ChatUserModel)

    fun transferOwnership(user: ChatUserModel)

    fun promote(user: ChatUserModel)
    fun demote(user: ChatUserModel)

    fun kick(user: ChatUserModel)
    fun ban(user: ChatUserModel)
    fun unban(user: ChatUserModel)
    fun isBanned(user: ChatUserModel): Boolean

    fun getOwner(): ChatUserModel
    fun getMembers(): ObjectSet<ChatUserModel>
    fun getModerators(): ObjectSet<ChatUserModel>
    fun getBannedPlayers(): ObjectSet<ChatUserModel>

    fun isOwner(user: ChatUserModel): Boolean
    fun isModerator(user: ChatUserModel): Boolean
    fun isMember(user: ChatUserModel): Boolean
    fun isMember(user: CommandSender): Boolean
}