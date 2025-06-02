package dev.slne.surf.chat.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.Component
import kotlin.math.ceil
import kotlin.math.min

@DslMarker
annotation class PageableMessageBuilderDsl

@PageableMessageBuilderDsl
class PageableMessageBuilder(private val linesPerPage: Int = 5) {

    private val lines = mutableObjectListOf<Component>()
    var pageCommand = "An error occurred while trying to display the page."
    private var title: Component = Component.empty()

    companion object {
        operator fun invoke(block: PageableMessageBuilder.() -> Unit): PageableMessageBuilder {
            return PageableMessageBuilder().apply(block)
        }
    }

    fun line(block: SurfComponentBuilder.() -> Unit) {
        lines.add(SurfComponentBuilder(block))
    }

    fun title(block: SurfComponentBuilder.() -> Unit) {
        title = SurfComponentBuilder(block)
    }

    fun send(sender: Player, page: Int) {
        val totalPages = ceil(lines.size.toDouble() / linesPerPage).toInt().coerceAtLeast(1)
        if (page < 1 || page > totalPages) {
            sender.sendText {
                error("Seite ")
                variableValue(page.toString())
                error(" existiert nicht.")
            }
            return
        }

        val start = (page - 1) * linesPerPage
        val end = min(start + linesPerPage, lines.size)

        sender.sendText {
            if (title != Component.empty()) {
                append(title)
                appendNewline()
            }

            for (i in start until end) {
                append(lines[i])
                appendNewline()
            }

            append(paginationComponent(page, totalPages))
        }
    }

    private fun navButton(label: String, targetPage: Int, enabled: Boolean): Component {
        return buildText {
            if (enabled) {
                success(label)
                clickRunsCommand(pageCommand.replace("%page%", targetPage.toString()))
            } else {
                error(label)
            }
        }
    }

    private fun paginationComponent(page: Int, totalPages: Int): Component {
        return buildText {
            append(navButton("[<<] ", 1, page > 1))
            append(navButton("[<] ", page - 1, page > 1))
            darkSpacer("Seite $page/$totalPages (gesamt: ${totalPages * linesPerPage})".toSmallCaps())
            append(navButton(" [>] ", page + 1, page < totalPages))
            append(navButton(" [>>]", totalPages, page < totalPages))
        }
    }
}

