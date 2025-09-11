package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

/**
 * A service interface for managing interactions with a database backend.
 *
 * This service provides an abstraction layer for establishing, managing,
 * and terminating connections to a database. It is designed to facilitate
 * database operations, including creating required tables and ensuring appropriate
 * lifecycle management of connections.
 */
interface DatabaseService {
    /**
     * Establishes a connection to the database using the specified path.
     *
     * @param path The file system path to the database file or resource.
     * This path is used to locate and connect to the desired database.
     */
    fun establishConnection(path: Path)

    /**
     * Creates the necessary database tables for the application.
     *
     * This method is responsible for initializing and creating the
     * required database tables in the underlying system.
     * It ensures that all tables defined within the database
     * schema are properly created and ready to use.
     *
     * The method should be called after establishing a proper connection
     * to the database and before any data operations are performed.
     */
    fun createTables()

    /**
     * Closes the currently active connection to the database.
     *
     * This method is typically used to terminate the established connection
     * safely and release any associated resources. It is the caller's
     * responsibility to ensure no subsequent database operations are attempted
     * after the connection is closed.
     *
     * This method should be called when database operations are complete
     * to ensure proper cleanup.
     *
     * If the connection is already closed, calling this method has no effect.
     */
    fun closeConnection()

    /**
     * Companion object for providing the singleton instance of the `DatabaseService`.
     * It utilizes the `requiredService` utility to retrieve the instance.
     */
    companion object {
        /**
         *
         */
        val INSTANCE = requiredService<DatabaseService>()
    }
}

/**
 * A property to access the singleton instance of the DatabaseService.
 * DatabaseService is responsible for managing the database-related functionalities and operations
 * across the system, providing a centralized way to interact with the underlying database.
 */
val databaseService get() = DatabaseService.INSTANCE