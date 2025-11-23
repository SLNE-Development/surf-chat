package dev.slne.surf.chat.server

import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.config.*
import dev.slne.surf.chat.server.database.repository.DenylistActionRepository
import dev.slne.surf.chat.server.database.repository.DenylistRepository
import dev.slne.surf.chat.server.database.table.*
import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.getBean

class ServerMain : StandalonePlugin() {
    init {
        SyncValues.init()
    }

    val denylistActionRepository by lazy {
        ChatContextHolderImpl.instance.context.getBean<DenylistActionRepository>()
    }

    val denylistRepository by lazy {
        ChatContextHolderImpl.instance.context.getBean<DenylistRepository>()
    }

    override suspend fun load() {
        autoDisablingConfig
        chatMotdConfig
        connectionMessageConfig
        discordConfig
        filterConfig
    }

    override suspend fun enable() {
        transaction {
            SchemaUtils.create(
                DenylistActionsTable, DenylistTable, FunctionalityTable, HistoryTable,
                IgnoreListTable
            )//TODO: REMOVE FOR PROD
        }

        denylistActionRepository.cacheActions()
        denylistRepository.cacheDenylist()
    }

    override suspend fun disable() {
        denylistActionRepository.storeActions()
        denylistRepository.storyDenylist()
    }
}

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)