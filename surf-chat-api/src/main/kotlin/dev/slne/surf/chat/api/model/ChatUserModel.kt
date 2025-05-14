package dev.slne.surf.chat.api.model

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface ChatUserModel {
    val uuid: UUID

    val ignoreList: ObjectSet<UUID>
    var pmToggled: Boolean
    var likesSound: Boolean

    fun isIgnoring(target: UUID): Boolean
    fun ignore(target: UUID)
    fun unIgnore(target: UUID)
    fun toggleIgnore(target: UUID): Boolean

    fun toggleSound(): Boolean

    fun acceptInvite(channel: ChannelModel)
    fun declineInvite(channel: ChannelModel)

    fun hasOpenPms(): Boolean
    fun togglePm(): Boolean

    fun moveToChannel(channel: ChannelModel)

    fun getName(): String
}