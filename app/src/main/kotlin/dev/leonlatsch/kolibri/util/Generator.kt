package dev.leonlatsch.kolibri.util

import java.util.*

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
object Generator {

    fun genUUid(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
}
