package dev.leonlatsch.kolibri.rest.dto

import dev.leonlatsch.kolibri.database.model.MessageType

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
class MessageDTO {

    var mid: String? = null
    var from: String? = null
    var to: String? = null
    var type: MessageType? = null
    var timestamp: String? = null
    var content: String? = null

    constructor()

    constructor(mid: String, from: String, to: String, type: MessageType, timestamp: String, content: String) {
        this.mid = mid
        this.from = from
        this.to = to
        this.type = type
        this.timestamp = timestamp
        this.content = content
    }
}
