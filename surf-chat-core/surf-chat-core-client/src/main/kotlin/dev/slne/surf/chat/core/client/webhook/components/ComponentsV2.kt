package dev.slne.surf.chat.core.client.webhook.components

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

/**
 * Leichtgewichtige DSL für Discord Components v2 Webhook-Payloads.
 *
 * ```kotlin
 * val json = componentsV2Message {
 *     container(accentColor = 0xED4245) {
 *         section {
 *             textDisplay("## Titel")
 *             thumbnail("https://example.com/head.png")
 *         }
 *         separator()
 *         actionRow {
 *             linkButton("https://example.com", label = "Öffnen")
 *         }
 *     }
 * }
 * ```
 *
 * Hinweis: Webhooks benötigen den Query-Parameter `with_components=true`. Discord akzeptiert bei Webhooks nur nicht-interaktive Komponenten (plus Link-Buttons) – interaktive Builder in dieser DSL werden von Webhooks abgelehnt.
 */
fun componentsV2Message(block: MessageBuilder.() -> Unit): JsonObject =
    MessageBuilder().apply(block).build()

@DslMarker
annotation class ComponentsV2Dsl

enum class ButtonStyle(internal val id: Int) {
    PRIMARY(1), SECONDARY(2), SUCCESS(3), DANGER(4), LINK(5), PREMIUM(6)
}

enum class SeparatorSpacing(internal val id: Int) {
    SMALL(1), LARGE(2)
}

class Emoji private constructor(
    private val name: String,
    private val id: String?,
    private val animated: Boolean
) {
    internal fun toJson() = buildJsonObject {
        put("name", name)
        if (id != null) {
            put("id", id)
            if (animated) put("animated", true)
        }
    }

    companion object {
        fun unicode(emoji: String) = Emoji(emoji, null, false)
        fun custom(name: String, id: String, animated: Boolean = false) = Emoji(name, id, animated)
    }
}

private object ComponentType {
    const val ACTION_ROW = 1
    const val BUTTON = 2
    const val STRING_SELECT = 3
    const val USER_SELECT = 5
    const val ROLE_SELECT = 6
    const val MENTIONABLE_SELECT = 7
    const val CHANNEL_SELECT = 8
    const val SECTION = 9
    const val TEXT_DISPLAY = 10
    const val THUMBNAIL = 11
    const val MEDIA_GALLERY = 12
    const val FILE = 13
    const val SEPARATOR = 14
    const val CONTAINER = 17
}

private const val FLAG_SUPPRESS_NOTIFICATIONS = 1 shl 12
private const val FLAG_IS_COMPONENTS_V2 = 1 shl 15

private fun JsonObjectBuilder.putId(id: Int?) {
    if (id != null) put("id", id)
}

private fun JsonObjectBuilder.putMedia(key: String, url: String) {
    put(key, buildJsonObject { put("url", url) })
}

/**
 * Gemeinsame Basis für alle Ebenen, die Layout-Komponenten aufnehmen können
 * (Nachricht selbst und Container).
 */
@ComponentsV2Dsl
abstract class ComponentParentBuilder internal constructor() {
    protected val components = mutableListOf<JsonObject>()

    fun textDisplay(content: String, id: Int? = null) {
        components += buildTextDisplay(content, id)
    }

    fun section(id: Int? = null, block: SectionBuilder.() -> Unit) {
        components += SectionBuilder().apply(block).build(id)
    }

    fun mediaGallery(id: Int? = null, block: MediaGalleryBuilder.() -> Unit) {
        components += MediaGalleryBuilder().apply(block).build(id)
    }

    /** [attachmentUrl] muss auf ein Attachment zeigen, z. B. `attachment://datei.txt`. */
    fun file(attachmentUrl: String, spoiler: Boolean = false, id: Int? = null) {
        components += buildJsonObject {
            put("type", ComponentType.FILE)
            putId(id)
            putMedia("file", attachmentUrl)
            if (spoiler) put("spoiler", true)
        }
    }

    fun separator(
        divider: Boolean = true,
        spacing: SeparatorSpacing = SeparatorSpacing.SMALL,
        id: Int? = null
    ) {
        components += buildJsonObject {
            put("type", ComponentType.SEPARATOR)
            putId(id)
            put("divider", divider)
            put("spacing", spacing.id)
        }
    }

    fun actionRow(id: Int? = null, block: ActionRowBuilder.() -> Unit) {
        components += ActionRowBuilder().apply(block).build(id)
    }
}

@ComponentsV2Dsl
class MessageBuilder internal constructor() : ComponentParentBuilder() {
    var username: String? = null
    var avatarUrl: String? = null
    var threadName: String? = null

    private var flags = FLAG_IS_COMPONENTS_V2

    fun suppressNotifications() {
        flags = flags or FLAG_SUPPRESS_NOTIFICATIONS
    }

    fun container(
        accentColor: Int? = null,
        spoiler: Boolean = false,
        id: Int? = null,
        block: ContainerBuilder.() -> Unit
    ) {
        components += ContainerBuilder().apply(block).build(accentColor, spoiler, id)
    }

    internal fun build(): JsonObject {
        require(components.isNotEmpty()) { "Eine Components-v2-Nachricht benötigt mindestens eine Komponente" }
        return buildJsonObject {
            put("flags", flags)
            username?.let { put("username", it) }
            avatarUrl?.let { put("avatar_url", it) }
            threadName?.let { put("thread_name", it) }
            putJsonArray("components") { this@MessageBuilder.components.forEach { add(it) } }
        }
    }
}

@ComponentsV2Dsl
class ContainerBuilder internal constructor() : ComponentParentBuilder() {
    internal fun build(accentColor: Int?, spoiler: Boolean, id: Int?): JsonObject {
        require(components.isNotEmpty()) { "Ein Container benötigt mindestens eine Komponente" }
        return buildJsonObject {
            put("type", ComponentType.CONTAINER)
            putId(id)
            accentColor?.let { put("accent_color", it) }
            if (spoiler) put("spoiler", true)
            putJsonArray("components") { this@ContainerBuilder.components.forEach { add(it) } }
        }
    }
}

/**
 * Section: 1–3 Text Displays links, rechts ein Accessory
 * (Thumbnail oder Button).
 */
@ComponentsV2Dsl
class SectionBuilder internal constructor() {
    private val texts = mutableListOf<JsonObject>()
    private var accessory: JsonObject? = null

    fun textDisplay(content: String, id: Int? = null) {
        texts += buildTextDisplay(content, id)
    }

    fun thumbnail(url: String, description: String? = null, spoiler: Boolean = false, id: Int? = null) {
        accessory = buildJsonObject {
            put("type", ComponentType.THUMBNAIL)
            putId(id)
            putMedia("media", url)
            description?.let { put("description", it) }
            if (spoiler) put("spoiler", true)
        }
    }

    fun accessoryButton(
        customId: String,
        label: String? = null,
        style: ButtonStyle = ButtonStyle.SECONDARY,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        id: Int? = null
    ) {
        accessory = buildButton(style, label, emoji, customId = customId, url = null, skuId = null, disabled = disabled, id = id)
    }

    fun accessoryLinkButton(
        url: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        id: Int? = null
    ) {
        accessory = buildButton(ButtonStyle.LINK, label, emoji, customId = null, url = url, skuId = null, disabled = disabled, id = id)
    }

    internal fun build(id: Int?): JsonObject {
        require(texts.size in 1..3) { "Eine Section benötigt 1 bis 3 Text Displays" }
        val accessory = requireNotNull(accessory) { "Eine Section benötigt ein Accessory (thumbnail/accessoryButton)" }
        return buildJsonObject {
            put("type", ComponentType.SECTION)
            putId(id)
            putJsonArray("components") { texts.forEach { add(it) } }
            put("accessory", accessory)
        }
    }
}

@ComponentsV2Dsl
class MediaGalleryBuilder internal constructor() {
    private val items = mutableListOf<JsonObject>()

    fun item(url: String, description: String? = null, spoiler: Boolean = false) {
        items += buildJsonObject {
            putMedia("media", url)
            description?.let { put("description", it) }
            if (spoiler) put("spoiler", true)
        }
    }

    internal fun build(id: Int?): JsonObject {
        require(items.size in 1..10) { "Eine Media Gallery benötigt 1 bis 10 Items" }
        return buildJsonObject {
            put("type", ComponentType.MEDIA_GALLERY)
            putId(id)
            putJsonArray("items") { items.forEach { add(it) } }
        }
    }
}

@ComponentsV2Dsl
class ActionRowBuilder internal constructor() {
    private val components = mutableListOf<JsonObject>()

    fun button(
        customId: String,
        label: String? = null,
        style: ButtonStyle = ButtonStyle.SECONDARY,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        id: Int? = null
    ) {
        require(style != ButtonStyle.LINK && style != ButtonStyle.PREMIUM) {
            "Für LINK/PREMIUM linkButton() bzw. premiumButton() verwenden"
        }
        components += buildButton(style, label, emoji, customId = customId, url = null, skuId = null, disabled = disabled, id = id)
    }

    fun linkButton(
        url: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        id: Int? = null
    ) {
        components += buildButton(ButtonStyle.LINK, label, emoji, customId = null, url = url, skuId = null, disabled = disabled, id = id)
    }

    fun premiumButton(skuId: String, disabled: Boolean = false, id: Int? = null) {
        components += buildButton(ButtonStyle.PREMIUM, null, null, customId = null, url = null, skuId = skuId, disabled = disabled, id = id)
    }

    fun stringSelect(
        customId: String,
        placeholder: String? = null,
        minValues: Int = 1,
        maxValues: Int = 1,
        disabled: Boolean = false,
        id: Int? = null,
        block: StringSelectBuilder.() -> Unit
    ) {
        val options = StringSelectBuilder().apply(block).options
        require(options.size in 1..25) { "Ein String Select benötigt 1 bis 25 Optionen" }
        components += buildSelect(ComponentType.STRING_SELECT, customId, placeholder, minValues, maxValues, disabled, id) {
            putJsonArray("options") { options.forEach { add(it) } }
        }
    }

    fun userSelect(
        customId: String,
        placeholder: String? = null,
        minValues: Int = 1,
        maxValues: Int = 1,
        disabled: Boolean = false,
        defaultUsers: List<String> = emptyList(),
        id: Int? = null
    ) {
        components += buildSelect(ComponentType.USER_SELECT, customId, placeholder, minValues, maxValues, disabled, id) {
            putDefaultValues(users = defaultUsers)
        }
    }

    fun roleSelect(
        customId: String,
        placeholder: String? = null,
        minValues: Int = 1,
        maxValues: Int = 1,
        disabled: Boolean = false,
        defaultRoles: List<String> = emptyList(),
        id: Int? = null
    ) {
        components += buildSelect(ComponentType.ROLE_SELECT, customId, placeholder, minValues, maxValues, disabled, id) {
            putDefaultValues(roles = defaultRoles)
        }
    }

    fun mentionableSelect(
        customId: String,
        placeholder: String? = null,
        minValues: Int = 1,
        maxValues: Int = 1,
        disabled: Boolean = false,
        defaultUsers: List<String> = emptyList(),
        defaultRoles: List<String> = emptyList(),
        id: Int? = null
    ) {
        components += buildSelect(ComponentType.MENTIONABLE_SELECT, customId, placeholder, minValues, maxValues, disabled, id) {
            putDefaultValues(users = defaultUsers, roles = defaultRoles)
        }
    }

    /** [channelTypes] sind Discord-Channel-Type-IDs (0 = Text, 2 = Voice, …). */
    fun channelSelect(
        customId: String,
        placeholder: String? = null,
        minValues: Int = 1,
        maxValues: Int = 1,
        disabled: Boolean = false,
        channelTypes: List<Int> = emptyList(),
        defaultChannels: List<String> = emptyList(),
        id: Int? = null
    ) {
        components += buildSelect(ComponentType.CHANNEL_SELECT, customId, placeholder, minValues, maxValues, disabled, id) {
            if (channelTypes.isNotEmpty()) {
                putJsonArray("channel_types") { channelTypes.forEach { add(it) } }
            }
            putDefaultValues(channels = defaultChannels)
        }
    }

    internal fun build(id: Int?): JsonObject {
        require(components.isNotEmpty()) { "Eine Action Row benötigt mindestens eine Komponente" }
        return buildJsonObject {
            put("type", ComponentType.ACTION_ROW)
            putId(id)
            putJsonArray("components") { this@ActionRowBuilder.components.forEach { add(it) } }
        }
    }
}

@ComponentsV2Dsl
class StringSelectBuilder internal constructor() {
    internal val options = mutableListOf<JsonObject>()

    fun option(
        label: String,
        value: String,
        description: String? = null,
        emoji: Emoji? = null,
        default: Boolean = false
    ) {
        options += buildJsonObject {
            put("label", label)
            put("value", value)
            description?.let { put("description", it) }
            emoji?.let { put("emoji", it.toJson()) }
            if (default) put("default", true)
        }
    }
}

private fun buildTextDisplay(content: String, id: Int?) = buildJsonObject {
    put("type", ComponentType.TEXT_DISPLAY)
    putId(id)
    put("content", content)
}

private fun buildButton(
    style: ButtonStyle,
    label: String?,
    emoji: Emoji?,
    customId: String?,
    url: String?,
    skuId: String?,
    disabled: Boolean,
    id: Int?
) = buildJsonObject {
    put("type", ComponentType.BUTTON)
    putId(id)
    put("style", style.id)
    label?.let { put("label", it) }
    emoji?.let { put("emoji", it.toJson()) }
    customId?.let { put("custom_id", it) }
    url?.let { put("url", it) }
    skuId?.let { put("sku_id", it) }
    if (disabled) put("disabled", true)
}

private fun buildSelect(
    type: Int,
    customId: String,
    placeholder: String?,
    minValues: Int,
    maxValues: Int,
    disabled: Boolean,
    id: Int?,
    extra: JsonObjectBuilder.() -> Unit
) = buildJsonObject {
    put("type", type)
    putId(id)
    put("custom_id", customId)
    placeholder?.let { put("placeholder", it) }
    put("min_values", minValues)
    put("max_values", maxValues)
    if (disabled) put("disabled", true)
    extra()
}

private fun JsonObjectBuilder.putDefaultValues(
    users: List<String> = emptyList(),
    roles: List<String> = emptyList(),
    channels: List<String> = emptyList()
) {
    if (users.isEmpty() && roles.isEmpty() && channels.isEmpty()) return
    putJsonArray("default_values") {
        users.forEach { addJsonObject { put("id", it); put("type", "user") } }
        roles.forEach { addJsonObject { put("id", it); put("type", "role") } }
        channels.forEach { addJsonObject { put("id", it); put("type", "channel") } }
    }
}
