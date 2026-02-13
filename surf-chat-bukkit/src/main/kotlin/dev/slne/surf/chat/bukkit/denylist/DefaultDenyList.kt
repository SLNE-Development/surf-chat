/**
 * Note:
 * This file contains offensive terms strictly for moderation and filtering purposes.
 * Their presence does not imply endorsement.
 */
package dev.slne.surf.chat.bukkit.denylist

import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.bukkit.command.denylist.DenylistBatchEntry
import kotlin.time.Duration.Companion.days

object DefaultDenyList {
    val default = listOf(
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
    )
}