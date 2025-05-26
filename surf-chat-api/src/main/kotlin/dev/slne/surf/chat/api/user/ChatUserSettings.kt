package dev.slne.surf.chat.api.user

interface ChatUserSettings {
    var pmEnabled: Boolean
    var pingsEnabled: Boolean
    var pmFriendBypassEnabled: Boolean
    var channelInvitesEnabled: Boolean
}