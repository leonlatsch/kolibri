package dev.leonlatsch.kolibri.database

import com.activeandroid.Model

/**
 * This Interface is used to notify components that a model hash changed
 * Mainly used for [dev.leonlatsch.kolibri.database.interfaces.CacheInterface]
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface EntityChangedListener<T : Model> {

    /**
     * Called when a model has changed
     *
     * @param newEntity
     */
    fun entityChanged(newEntity: T?)
}
