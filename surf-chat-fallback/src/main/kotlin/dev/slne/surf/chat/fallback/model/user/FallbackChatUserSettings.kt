package dev.slne.surf.chat.fallback.model.user

import dev.slne.surf.chat.api.user.ChatUserSettings

data class FallbackChatUserSettings (
    override var pmEnabled: Boolean = true,
    override var pingsEnabled: Boolean = true,
    override var pmFriendBypassEnabled: Boolean = true,
    override var channelInvitesEnabled: Boolean = true
) : ChatUserSettings