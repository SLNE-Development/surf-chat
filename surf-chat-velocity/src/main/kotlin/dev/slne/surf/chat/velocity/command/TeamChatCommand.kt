package dev.slne.surf.chat.velocity.command

import com.velocitypowered.api.command.SimpleCommand
import dev.slne.surf.chat.velocity.teamMembers
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class TeamChatCommand: SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        val source = invocation.source()

        if (args.isEmpty()) {
            source.sendText {
                appendPrefix()
                error("Du musst eine Nachricht angeben.")
            }
            return
        }

        val message = args.joinToString(" ")

        teamMembers().forEach { it.sendText {

        } }
    }
}