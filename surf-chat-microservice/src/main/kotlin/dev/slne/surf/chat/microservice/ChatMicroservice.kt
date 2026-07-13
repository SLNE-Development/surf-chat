package dev.slne.surf.chat.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.common.rabbit.rpc.ModerationService
import dev.slne.surf.chat.microservice.handler.FunctionalityHandler
import dev.slne.surf.chat.microservice.handler.HistoryHandler
import dev.slne.surf.chat.microservice.handler.IgnoreListHandler
import dev.slne.surf.chat.microservice.rpc.ModerationServiceImpl
import dev.slne.surf.chat.microservice.table.FunctionalityTable
import dev.slne.surf.chat.microservice.table.HistoryTable
import dev.slne.surf.chat.microservice.table.IgnoreListTable
import dev.slne.surf.chat.microservice.table.ModerationsTable
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import kotlin.io.path.Path

@AutoService(Microservice::class)
class ChatMicroservice : Microservice() {
    override val dataPath = Path("config")
    private val databaseApi = DatabaseApi.create(dataPath)
    private val rabbitApi = ServerRabbitMQApi.create("surf-chat", dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                FunctionalityTable, HistoryTable, IgnoreListTable, ModerationsTable
            )
        }

        rabbitApi.registerRequestHandler(HistoryHandler)
        rabbitApi.registerRequestHandler(FunctionalityHandler)
        rabbitApi.registerRequestHandler(IgnoreListHandler)

        rabbitApi.registerRpcService<ModerationService>(ModerationServiceImpl)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}