package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.api.type.ChannelStatusType
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet

interface ChannelModel {
    val name: String
    val status: ChannelStatusType

    val members: Object2ObjectMap<ChatUserModel, ChannelRoleType>
    val bannedPlayers: ObjectSet<ChatUserModel>

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
}