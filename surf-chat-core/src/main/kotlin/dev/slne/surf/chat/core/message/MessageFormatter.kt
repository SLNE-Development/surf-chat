package dev.slne.surf.chat.core.message

import net.kyori.adventure.text.Component

/**
 * Interface for formatting messages in various contexts within the chat system.
 *
 * This interface defines methods for formatting global, private, team, channel, and spy messages.
 * Each method accepts message data and returns a formatted `Component` object suitable for the
 * specific context in which the message will be displayed or processed.
 */
interface MessageFormatter {
    /**
     * Represents the formatted message within the `MessageFormatter` class.
     *
     * The `message` variable holds a `Component` that is typically used as the result
     * of various formatting methods, such as formatting global messages, private messages,
     * team chats, or channel messages. This property ensures that the message is
     * properly structured and ready for display in the appropriate context.
     */
    val message: Component

    /**
     * Formats a global message using the provided message data.
     *
     * This method is used to apply specific formatting rules for global messages,
     * ensuring that they are displayed appropriately across all users in the system.
     *
     * @param messageData the data representing the message to be formatted, including its content, sender, and metadata
     * @return a formatted message as a `Component`, ready for display in the global context
     */
    fun formatGlobal(messageData: MessageData): Component

    /**
     * Formats an incoming private message for display.
     *
     * This method processes the provided message data in the context of an incoming private message,
     * applying necessary formatting rules to prepare the message for presentation to the recipient.
     *
     * @param messageData The data of the incoming message, including its content, sender, recipient, metadata, and context.
     * @return A formatted `Component` representing the incoming private message for display.
     */
    fun formatIncomingPm(messageData: MessageData): Component

    /**
     * Formats an outgoing private message for display or further processing.
     *
     * This function processes message data in the context of an outgoing private message,
     * applying relevant formatting and rendering rules defined within the chat system.
     *
     * @param messageData The data of the message to be formatted, including its content,
     * metadata, and context such as sender and recipient details.
     * @return A formatted `Component` representing the outgoing private message.
     */
    fun formatOutgoingPm(messageData: MessageData): Component

    /**
     * Formats a message intended for a team chat context.
     * This method takes a `MessageData` object as input, processes its content along with associated metadata,
     * and returns a formatted `Component` representation suitable for display in a team chat.
     *
     * @param messageData The `MessageData` object containing the message's content, metadata, and context to be formatted.
     * @return A `Component` that represents the formatted message customized for a team chat context.
     */
    fun formatTeamchat(messageData: MessageData): Component

    /**
     * Formats a message for display in a channel context.
     *
     * @param messageData The data of the message being formatted, including content, sender, and context.
     * @return A formatted message as a `Component` suitable for rendering in a channel.
     */
    fun formatChannel(messageData: MessageData): Component

    /**
     * Formats a private message for spy purposes.
     *
     * This method transforms a private message into a formatted `Component`
     * specifically intended for display or logging by users with elevated
     * permissions, such as moderators or administrators.
     *
     * @param messageData The data of the message to be formatted, including its content, metadata, and context.
     * @return A formatted `Component` containing the message view for spy purposes.
     */
    fun formatPmSpy(messageData: MessageData): Component

    /**
     * Formats the provided message data into a `Component` specific to channel spy messages.
     *
     * This method processes the given `MessageData` to create a formatted representation
     * of a channel spy message. The output is tailored for logging or displaying
     * information about channel communications that are being monitored.
     *
     * @param messageData the data of the message to be formatted, including its content, metadata, and context.
     * @return a `Component` representing the formatted channel spy message.
     */
    fun formatChannelSpy(messageData: MessageData): Component
}