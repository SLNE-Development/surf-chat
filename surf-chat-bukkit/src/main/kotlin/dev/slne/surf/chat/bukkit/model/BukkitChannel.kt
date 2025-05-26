package dev.slne.surf.chat.bukkit.model

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BukkitChannel(
    override val name: String,
    override var status: ChannelStatus = ChannelStatus.PRIVATE,
    override val members: Object2ObjectMap<ChatUser, ChannelRole> = mutableObject2ObjectMapOf(),
    override val bannedPlayers: ObjectSet<ChatUser> = mutableObjectSetOf(),
    override val invites: ObjectSet<ChatUser> = mutableObjectSetOf()
) : Channel {
    override fun join(user: ChatUser, silent: Boolean) {
        members[user] = ChannelRole.MEMBER

        if (!silent) {
            members.filter { it.key != user }.forEach {
                it.key.sendPrefixed {
                    plugin.launch {
                        variableValue(user.getName())
                        info(" ist dem Nachrichtenkanal beigetreten.")
                    }
                }
            }
        }
    }

    override fun leave(user: ChatUser, silent: Boolean) {
        members.remove(user)

        if (!silent) {
            members.forEach {
                it.key.sendPrefixed {
                    plugin.launch {
                        variableValue(user.getName())
                        info(" hat den Nachrichtenkanal verlassen.")
                    }
                }
            }
        }
    }

    override fun isInvited(user: ChatUser): Boolean {
        return invites.contains(user)
    }

    override fun isInvited(user: CommandSender): Boolean {
        if (user is Player) {
            return invites.any { it.uuid == user.uniqueId }
        }
        return false
    }

    override fun invite(user: ChatUser) {
        invites.add(user)
    }

    override fun revokeInvite(user: ChatUser) {
        invites.remove(user)
    }

    override fun transferOwnership(user: ChatUser) {
        val oldOwner = this.getOwner()

        members[user] = ChannelRole.OWNER
        members[oldOwner] = ChannelRole.MODERATOR

        members.filter { it.key != user && it.key != oldOwner }.forEach {
            it.key.sendPrefixed {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" ist jetzt der Besitzer des Nachrichtenkanals.")
                }
            }
        }
    }

    override fun promote(user: ChatUser) {
        if (members[user] == ChannelRole.MEMBER) {
            members[user] = ChannelRole.MODERATOR
        }

        members.filter { it.key != user }.forEach {
            it.key.sendPrefixed {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" wurde zum Moderator befördert.")
                }
            }
        }
    }

    override fun demote(user: ChatUser) {
        if (members[user] == ChannelRole.MODERATOR) {
            members[user] = ChannelRole.MEMBER
        }

        members.filter { it.key != user }.forEach {
            it.key.sendPrefixed {
                plugin.launch {
                    variableValue(user.getName())
                    primary(" wurde zum Mitglied degradiert.")
                }
            }
        }
    }

    override fun kick(user: ChatUser) {
        this.leave(user)
    }

    override fun ban(user: ChatUser) {
        this.leave(user)
        bannedPlayers.add(user)
    }

    override fun unban(user: ChatUser) {
        bannedPlayers.remove(user)
    }

    override fun isBanned(user: ChatUser): Boolean {
        return bannedPlayers.contains(user)
    }

    override fun getOwner(): ChatUser {
        return members.entries.first { it.value == ChannelRole.OWNER }.key
    }

    override fun getMembers(includeElevatedUsers: Boolean): ObjectSet<ChatUser> {
        return when(includeElevatedUsers) {
            true -> members.keys
            false -> members.entries.filter { it.value == ChannelRole.MEMBER }.map { it.key }.toObjectSet()
        }
    }

    override fun getModerators(): ObjectSet<ChatUser> {
        return members.entries.filter { it.value == ChannelRole.MODERATOR }.map { it.key }
            .toObjectSet()
    }

    override fun isOwner(user: ChatUser): Boolean {
        return members[user] == ChannelRole.OWNER
    }

    override fun hasModeratorPermissions(user: ChatUser): Boolean {
        return members[user] == ChannelRole.MODERATOR || members[user] == ChannelRole.OWNER
    }

    override fun isMember(user: ChatUser): Boolean {
        return members.contains(user)
    }

    override fun isMember(user: CommandSender): Boolean {
        if (user is Player) {
            return members.keys.any { it.uuid == user.uniqueId }
        }
        return false
    }
}