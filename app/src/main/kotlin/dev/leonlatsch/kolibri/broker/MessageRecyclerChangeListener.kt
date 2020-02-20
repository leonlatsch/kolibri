package dev.leonlatsch.kolibri.broker

import dev.leonlatsch.kolibri.database.model.Message

/**
 * Listener for the MessageRecycler
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface MessageRecyclerChangeListener {

    /**
     * Called when a message has changed or a new is received
     *
     * @param message
     */
    fun receive(message: Message)
}
