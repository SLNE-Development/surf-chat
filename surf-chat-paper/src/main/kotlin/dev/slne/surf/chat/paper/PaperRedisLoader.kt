package dev.slne.surf.chat.paper

import dev.slne.surf.chat.paper.redis.listener.RedisEventListener
import dev.slne.surf.redis.RedisApi

val redisLoader = BukkitRedisLoader()
val redisApi get() = redisLoader.redisApi

class BukkitRedisLoader {
    val redisApi = RedisApi.create()

    fun connect() {
        redisApi.subscribeToEvents(RedisEventListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}