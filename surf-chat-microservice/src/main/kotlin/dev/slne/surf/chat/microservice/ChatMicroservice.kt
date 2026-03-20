package dev.slne.surf.chat.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.chat.microservice.handler.HistoryHandler
import dev.slne.surf.chat.microservice.table.FunctionalityTable
import dev.slne.surf.chat.microservice.table.HistoryTable
import dev.slne.surf.chat.microservice.table.IgnoreListTable
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import kotlin.io.path.Path

@AutoService(Microservice::class)
class ChatMicroservice : Microservice() {
    private val databaseApi = DatabaseApi.create(Path("config"))
    private val rabbitApi = ServerRabbitMQApi.create("surf-chat", Path("config"))

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                FunctionalityTable, HistoryTable, IgnoreListTable
            )
        }

        rabbitApi.registerRequestHandler(HistoryHandler)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}