package dev.slne.surf.chat.core.client.webhook

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.net.URL

class DiscordClient(
    private val webhookUrl: URL
) : AutoCloseable {
    private val client = HttpClient(OkHttp)

    suspend fun sendJson(json: String): Boolean {
        val response = client.post(webhookUrl.toString()) {
            url { parameters.append("with_components", "true") }
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11")
            setBody(json)
        }

        return response.status.isSuccess()
    }

    override fun close() {
        client.close()
    }
}