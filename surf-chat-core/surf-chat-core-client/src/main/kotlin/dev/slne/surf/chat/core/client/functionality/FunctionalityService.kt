package dev.slne.surf.chat.core.client.functionality

import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CloudServer
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service

@Service
class FunctionalityService {
    fun isEnabled(server: String) =
        SyncValues.chatFunctionalities.firstOrNull { it.first == server }?.second == true

    fun enableFunctionality(server: String) {
        val entry = SyncValues.chatFunctionalities.firstOrNull { it.first == server }

        if (entry != null) {
            SyncValues.chatFunctionalities.removeIf { it.first == server }
            SyncValues.chatFunctionalities.add(entry.first to true)
        } else {
            SyncValues.chatFunctionalities.add(server to true)
        }
    }

    fun disableFunctionality(server: String) {
        val entry = SyncValues.chatFunctionalities.firstOrNull { it.first == server }

        if (entry != null) {
            SyncValues.chatFunctionalities.removeIf { it.first == server }
            SyncValues.chatFunctionalities.add(entry.first to false)
        } else {
            SyncValues.chatFunctionalities.add(server to false)
        }
    }

    fun isLocalChatEnabled() =
        SyncValues.chatFunctionalities.firstOrNull { it.first == CloudServer.current().name }?.second == true

    fun getServerFunctionalities() = SyncValues.chatFunctionalities
}

val functionalityService get() = ChatContextHolderImpl.instance.context.getBean<FunctionalityService>()