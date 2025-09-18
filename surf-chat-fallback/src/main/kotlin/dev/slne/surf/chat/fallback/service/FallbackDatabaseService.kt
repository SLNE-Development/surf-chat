package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.*
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.database.database.DatabaseProvider
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    lateinit var databaseProvider: DatabaseProvider
    override fun establishConnection(path: Path) {
        databaseProvider = DatabaseManager(path, path).databaseProvider
        databaseProvider.connect()
    }

    override fun createTables() {
        notificationService.createTable()
        directMessageService.createTable()
        historyService.createTable()
        denylistService.createTable()
        ignoreService.createTable()
        functionalityService.createTable()
    }

    override fun closeConnection() {
        databaseProvider.disconnect()
    }
}