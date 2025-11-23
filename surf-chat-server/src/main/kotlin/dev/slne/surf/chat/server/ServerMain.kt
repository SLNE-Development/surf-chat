package dev.slne.surf.chat.server

import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.config.discordConfig
import dev.slne.surf.chat.server.config.filterConfig
import dev.slne.surf.chat.server.config.messageConfig
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
        messageConfig
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

    fun loadSyncValues() {
        SyncValues.allowedDomains.addAll(filterConfig.config.allowedDomains)
        SyncValues.spamInterval.set(filterConfig.config.interval)
        SyncValues.spamAmount.set(filterConfig.config.amount)

        SyncValues.autoDisablingMinAmounts.addAll(filterConfig.config.disablingServers.map { it.server to it.maximumPlayersBeforeDisable })
        SyncValues.connectMessages.addAll(messageConfig.config.connectionMessages.map { it.server to it.joinMessage })
        SyncValues.disconnectMessages.addAll(messageConfig.config.connectionMessages.map { it.server to it.leaveMessage })
        SyncValues.chatMotds.addAll(messageConfig.config.chatMotds.map { it.server to it.motd })
    }
}

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)