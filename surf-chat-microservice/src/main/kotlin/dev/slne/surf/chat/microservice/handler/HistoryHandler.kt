package dev.slne.surf.chat.microservice.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler

object HistoryHandler {
    @RabbitHandler
    fun handle
}