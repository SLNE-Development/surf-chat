package dev.slne.surf.chat.core.common.util

import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.common.netty.packet.serializer.SerializableRegex
import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import kotlinx.serialization.Serializable

typealias NameAndUuid = Pair<String, ChatUuid>

object SyncValues {
    val denylistEntries: SyncSet<DenylistEntry> = SyncSet("chat:denylist:entries")
    val denylistActions: SyncSet<DenylistAction> = SyncSet("chat:denylist:actions")

    val chatFunctionalities: SyncSet<Pair<String, Boolean>> =
        SyncSet("chat:functionalities")
    val latestPrivateMessages: SyncSet<LastPrivateMessage> =
        SyncSet("chat:private:target")
    val ignoreList = SyncSet<Ignorelist>("chat:ignorelist")

    val allowedDomains = SyncSet<String>("chat:filter:domains")
    val spamInterval = SyncValue<Long>("chat:filter:spam_interval", 10000L)
    val spamAmount = SyncValue<Int>("chat:filter:spam_amount", 5)

    val autoDisablingMinAmounts = SyncSet<ServerInteger>("chat:disabling:amounts")

    val connectMessages = SyncSet<ServerMessage>("chat:connections:messages")
    val disconnectMessages = SyncSet<ServerMessage>("chat:quit:messages")
    val chatMotds = SyncSet<ServerMessage>("chat:motd:messages")

    fun init() {}

    @Serializable
    data class ServerInteger(
        val serverPattern: SerializableRegex,
        val value: Int
    )

    @Serializable
    data class ServerMessage(
        val serverPattern: SerializableRegex,
        val message: String
    )

    @Serializable
    data class Ignorelist(
        val user: ChatUuid,
        val entries: MutableSet<IgnoreListEntry>
    )

    @Serializable
    data class LastPrivateMessage(
        val user: ChatUuid,
        val target: ChatUuid
    )
}