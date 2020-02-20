package dev.leonlatsch.kolibri.rest.dto

import dev.leonlatsch.kolibri.util.empty

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
class Container<T> {

    var code: Int = 0
    var message: String = String.empty()
    var timestamp: String = String.empty()
    var content: T? = null

    constructor()

    constructor(code: Int, message: String, timestamp: String, content: T) {
        this.code = code
        this.message = message
        this.timestamp = timestamp
        this.content = content
    }
}
