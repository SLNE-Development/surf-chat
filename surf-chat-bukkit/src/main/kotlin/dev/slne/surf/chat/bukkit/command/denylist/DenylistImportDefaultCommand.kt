package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import kotlin.time.Duration.Companion.days

/**
 * ⚠️ DISCLAIMER:
 *
 * All offensive words, swear words, or potentially sensitive language contained
 * in this file exist **solely for the purpose of moderation, testing, and
 * functional operation of this plugin**. They are included to ensure that
 * the plugin can properly detect, handle, and respond to such language
 * where necessary.
 *
 * Under no circumstances are these words intended to insult, harm, or
 * demean any individual, group, or community. Their presence is strictly
 * technical and functional, and any resemblance to real-life offensive
 * language is purely coincidental.
 *
 * This file and its contents are designed for controlled and responsible
 * usage within the plugin's systems. Any use outside of this intended
 * context is **unauthorized and unintended**.
 *
 * By including these words, the goal is to improve moderation capabilities
 * and ensure the plugin behaves correctly in scenarios where offensive
 * language might appear—not to promote or normalize inappropriate speech.
 *
 * ⚠️ Please treat all content in this file as **strictly functional**,
 * not personal or offensive.
 */
fun CommandAPICommand.denylistImportDefaultCommand() = subcommand("importdefaults") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_DEFAULTS)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendInfoPrefix()
            info("Importiere Standard-Wortfilter...")
        }

        plugin.launch(Dispatchers.IO) {
            listOf(
                DenylistBatchEntry.builder()
                    .withReason("Verwenden von Kennzeichen verfassungswidriger und terroristischer Organisationen")
                    .withActionType(DenylistActionType.COMMUNITY_BAN)
                    .withPunishReason("Verwenden von Kennzeichen verfassungswidriger und terroristischer Organisationen")
                    .withWords(
                        "heil hitler",
                        "heilhitler"
                    )
                    .build(),
                DenylistBatchEntry.builder()
                    .withReason("Gewaltverherrlichende Inhalte")
                    .withActionType(DenylistActionType.PERMANENT_BAN)
                    .withPunishReason("Gewaltverherrlichende Inhalte")
                    .withWords(
                        "killyourself",
                        "kys",
                        "nigger",
                        "ngga",
                        "nigga"
                    )
                    .build(),
                DenylistBatchEntry.builder()
                    .withReason("Starke Beleidigungen")
                    .withActionType(DenylistActionType.EXPIRABLE_BAN)
                    .withPunishReason("Inhalte mit abwertender, beleidigender oder diskriminierender Sprache")
                    .withDuration(14.days)
                    .withWords(
                        "hure",
                        "hurensohn",
                        "fotze",
                        "nutte",
                        "bastard",
                        "schlampe",
                        "hs"
                    ).build(),
                DenylistBatchEntry.builder()
                    .withReason("Mittelstarke Beleidigungen")
                    .withActionType(DenylistActionType.EXPIRABLE_BAN)
                    .withPunishReason("Inhalte mit persönlichen Beleidigungen mittlerer Stufe")
                    .withDuration(7.days)
                    .withWords(
                        "ass", "arschloch", "arsch", "opfer", "wichser", "pisser", "pussy"
                    ).build(),
                DenylistBatchEntry.builder()
                    .withReason("Leichte Beleidigungen")
                    .withActionType(DenylistActionType.MUTE)
                    .withPunishReason("Beleidigende Inhalte")
                    .withDuration(3.days)
                    .withWords(
                        "dummkopf",
                        "idiot",
                        "miststück",
                        "blödmann",
                        "verpiss dich",
                        "verpissdich",
                        "loser",
                        "noob",
                        "n00b",
                        "leck"
                    ).build()
            ).forEach { entry ->
                entry.execute()
            }

            executor.sendText {
                appendSuccessPrefix()
                success("Import der Standard-Wortfilter abgeschlossen.")
            }
        }
    }
}