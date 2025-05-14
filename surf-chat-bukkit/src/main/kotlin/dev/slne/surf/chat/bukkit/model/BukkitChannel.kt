package dev.slne.surf.chat.bukkit.model

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BukkitChannel(
    override val name: String,
    override var status: ChannelStatusType = ChannelStatusType.PRIVATE,
    override val members: Object2ObjectMap<ChatUserModel, ChannelRoleType> = Object2ObjectOpenHashMap(),
    override val bannedPlayers: ObjectSet<ChatUserModel> = ObjectArraySet(),
    override val invites: ObjectSet<ChatUserModel> = ObjectArraySet()
) : ChannelModel {
    override fun join(user: ChatUserModel, silent: Boolean) {
        members[user] = ChannelRoleType.MEMBER

        if (!silent) {
            members.filter { it.key != user }.forEach {
                it.key.sendText(buildText {
                    plugin.launch {
                        variableValue(user.getName())
                        info(" ist dem Nachrichtenkanal beigetreten.")
                    }
                })
            }
        }
    }

    override fun leave(user: ChatUserModel, silent: Boolean) {
        members.remove(user)

        if (!silent) {
            members.forEach {
                it.key.sendText(buildText {
                    plugin.launch {
                        variableValue(user.getName())
                        info(" hat den Nachrichtenkanal verlassen.")
                    }
                })
            }
        }
    }

    override fun isInvited(user: ChatUserModel): Boolean {
        return invites.contains(user)
    }

    override fun isInvited(user: CommandSender): Boolean {
        if (user is Player) {
            return invites.any { it.uuid == user.uniqueId }
        }
        return false
    }

    override fun invite(user: ChatUserModel) {
        invites.add(user)
    }

    override fun unInvite(user: ChatUserModel) {
        invites.remove(user)
    }

    override fun transferOwnership(user: ChatUserModel) {
        val oldOwner = this.getOwner()

        members[user] = ChannelRoleType.OWNER
        members[oldOwner] = ChannelRoleType.MODERATOR

        members.filter { it.key != user && it.key != oldOwner }.forEach {
            it.key.sendText(buildText {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" ist jetzt der Besitzer des Nachrichtenkanals.")
                }
            })
        }
    }

    override fun promote(user: ChatUserModel) {
        if (members[user] == ChannelRoleType.MEMBER) {
            members[user] = ChannelRoleType.MODERATOR
        }

        members.filter { it.key != user }.forEach {
            it.key.sendText(buildText {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" wurde zum Moderator befördert.")
                }
            })
        }
    }

    override fun demote(user: ChatUserModel) {
        if (members[user] == ChannelRoleType.MODERATOR) {
            members[user] = ChannelRoleType.MEMBER
        }

        members.filter { it.key != user }.forEach {
            it.key.sendText(buildText {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" wurde zum Mitglied degradiert.")
                }
            })
        }
    }

    override fun kick(user: ChatUserModel) {
        this.leave(user)
    }

    override fun ban(user: ChatUserModel) {
        this.leave(user)
        bannedPlayers.add(user)
    }

    override fun unban(user: ChatUserModel) {
        bannedPlayers.remove(user)
    }

    override fun isBanned(user: ChatUserModel): Boolean {
        return bannedPlayers.contains(user)
    }

    override fun getOwner(): ChatUserModel {
        return members.entries.first { it.value == ChannelRoleType.OWNER }.key
    }

    override fun getMembers(): ObjectSet<ChatUserModel> {
        return members.keys
    }

    override fun getOnlyMembers(): ObjectSet<ChatUserModel> {
        return members.entries.filter { it.value == ChannelRoleType.MEMBER }.map { it.key }
            .toObjectSet()
    }

    override fun getModerators(): ObjectSet<ChatUserModel> {
        return members.entries.filter { it.value == ChannelRoleType.MODERATOR }.map { it.key }
            .toObjectSet()
    }

    override fun isOwner(user: ChatUserModel): Boolean {
        return members[user] == ChannelRoleType.OWNER
    }

    override fun isModerator(user: ChatUserModel): Boolean {
        return members[user] == ChannelRoleType.MODERATOR || members[user] == ChannelRoleType.OWNER
    }

    override fun isMember(user: ChatUserModel): Boolean {
        return members.contains(user)
    }

    override fun isMember(user: CommandSender): Boolean {
        if (user is Player) {
            return members.keys.any { it.uuid == user.uniqueId }
        }
        return false
    }

    override fun isMember(user: Player): Boolean {
        return members.keys.any { it.uuid == user.uniqueId }
    }
}