package dev.slne.surf.chat.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.DatabaseLoader
import dev.slne.surf.chat.fallback.table.*
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(DatabaseLoader::class)
class DatabaseLoaderImpl : DatabaseLoader, Services.Fallback {
    lateinit var databaseApi: DatabaseApi
    override suspend fun connect(dataPath: Path) {
        databaseApi = DatabaseApi.create(dataPath)

        suspendTransaction {
            SchemaUtils.create(
                DenylistActionsTable, DenylistTable,
                FunctionalityTable, HistoryTable, IgnoreListTable, UserTable
            )
        }
    }

    override fun disconnect() {
        databaseApi.shutdown()
    }
}