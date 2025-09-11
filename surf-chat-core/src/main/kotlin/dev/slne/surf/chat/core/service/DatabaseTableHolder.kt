package dev.slne.surf.chat.core.service

/**
 * Represents an interface for classes that manage database tables.
 * Implementors of this interface are responsible for ensuring that their
 * respective database tables are created and properly initialized when necessary.
 */
interface DatabaseTableHolder {
    /**
     * Creates a database table. This method is intended to define and initialize
     * the required structure for persisting data in the implementing service's database.
     */
    fun createTable()
}