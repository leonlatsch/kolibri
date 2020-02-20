package dev.leonlatsch.kolibri.database.interfaces

import com.activeandroid.Model
import dev.leonlatsch.kolibri.database.EntityChangedListener

/**
 * Class to store a cached model in memory and database
 * Mainly used for the logged in user
 *
 * @param <T> extends Model
 * @author Leon Latsch
 * @since 1.0.0
</T> */
abstract class CacheInterface<T : Model> {

    /**
     * Cached model to be synced with database a every transaction
     */
    protected var model: T? = null

    /**
     * [List] of [EntityChangedListener]s that get notified when a model has changed
     */
    private val listeners = arrayListOf<EntityChangedListener<T>>()

    /**
     * Adds a [EntityChangedListener] that gets notified when the model hat changed
     *
     * @param listener the listener to add
     */
    fun addEntityChangedListener(listener: EntityChangedListener<T>) {
        listeners.add(listener)
    }

    /**
     * Notify all listeners with the same model
     *
     * @param model
     */
    protected fun notifyListeners(model: T?) {
        for (listener in listeners) {
            listener.entityChanged(model)
        }
    }

    fun save(model: T?) {
        if (model != null) {
            if (model != null) {
                delete(model)
            }
            model.save()
            this.model = model
            notifyListeners(model)
        }
    }

    fun delete(model: T) {
        model.delete()
        this.model = null
        notifyListeners(null)
    }
}
