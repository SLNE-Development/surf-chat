package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.redis.listener.RedisEventListener
import dev.slne.surf.chat.bukkit.redis.listener.RedisRequestListener
import dev.slne.surf.redis.RedisApi

val redisLoader = BukkitRedisLoader()
val redisApi get() = redisLoader.redisApi

class BukkitRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create()
        redisApi.registerRequestHandler(RedisRequestListener)
        redisApi.subscribeToEvents(RedisEventListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}