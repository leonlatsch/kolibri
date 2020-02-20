package dev.leonlatsch.kolibri.database.interfaces

import com.activeandroid.query.Select

import dev.leonlatsch.kolibri.database.model.KeyPair

/**
 * Database interface to persist KeyPars
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object KeyPairInterface {

    private const val QUEUE_UID_WHERE = "uid = ?"

    fun createOrGet(keyPair: KeyPair?): KeyPair? {
        if (keyPair?.uid == null) {
            return null
        }

        val saved = Select().from(KeyPair::class.java).where(QUEUE_UID_WHERE, keyPair.uid).executeSingle<KeyPair>()

        return if (saved == null) {
            keyPair.save()
            keyPair
        } else {
            saved
        }
    }

    fun createOrGet(keyPair: KeyPair?, uid: String): KeyPair? {
        if (keyPair == null) {
            return null
        }

        keyPair.uid = uid
        return createOrGet(keyPair)
    }

    operator fun get(uid: String): KeyPair {
        return Select().from(KeyPair::class.java).where(QUEUE_UID_WHERE, uid).executeSingle()
    }
}
