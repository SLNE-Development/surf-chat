package dev.slne.surf.chat.api.model

import java.util.UUID

interface HistoryEntryModel {
   val id: UUID
   val uuid: UUID
   val type: String

   val timestamp: Long
   val message: String
   val deleted: Boolean
   val deletedBy: String

   val server: String
}