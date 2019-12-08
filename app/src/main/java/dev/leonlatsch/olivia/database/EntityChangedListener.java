package dev.leonlatsch.olivia.database;

import com.activeandroid.Model;

/**
 * This Interface is used to notify components that a model hash changed
 * Mainly used for {@link dev.leonlatsch.olivia.database.interfaces.CacheInterface}
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface EntityChangedListener<T extends Model> {

    /**
     * Called when a model has changed
     *
     * @param newEntity
     */
    void entityChanged(T newEntity);
}
