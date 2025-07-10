package dev.slne.surf.chat.fallback.model.user

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.user.ChatUserSettings
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import java.util.UUID

data class FallbackChatUser (
    override val uuid: UUID,
    override val ignoreList: ObjectSet<UUID>,
    override val settings: ChatUserSettings,
    override val name: String,
) : ChatUser {
    override suspend fun toggleChannelInvites(): Boolean {
        return !settings.channelInvitesEnabled.also {
            settings.channelInvitesEnabled = it
        }
    }

    override suspend fun togglePingsEnabled(): Boolean {
        return !settings.pingsEnabled.also {
            settings.pingsEnabled = it
        }
    }

    override suspend fun togglePmsEnabled(): Boolean {
        return !settings.pmEnabled.also {
            settings.pmEnabled = it
        }
    }

    override suspend fun togglePmFriendBypassEnabled(): Boolean {
        return !settings.pmFriendBypassEnabled.also {
            settings.pmFriendBypassEnabled = it
        }
    }

    override suspend fun channelInvitesEnabled(): Boolean {
        return settings.channelInvitesEnabled
    }

    override suspend fun pingsEnabled(): Boolean {
        return settings.pingsEnabled
    }

    override suspend fun pmEnabled(): Boolean {
        return settings.pmEnabled
    }

    override suspend fun pmFriendBypassEnabled(): Boolean {
        return settings.pmFriendBypassEnabled
    }

    override suspend fun ignores(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    override suspend fun toggleIgnore(target: UUID): Boolean {
        return if (ignoreList.contains(target)) {
            this.ignoreList.remove(target)
            false
        } else {
            this.ignoreList.add(target)
            true
        }
    }

    override suspend fun acceptInvite(channel: Channel) {
        TODO("Not yet implemented")
    }

    override suspend fun declineInvite(channel: Channel) {
        TODO("Not yet implemented")
    }

    override suspend fun moveToChannel(channel: Channel) {
        TODO("Not yet implemented")
    }
}